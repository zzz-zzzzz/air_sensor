package com.tsu.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tsu.constant.RelayType;
import com.tsu.entity.AirData;
import com.tsu.entity.AirDataScope;
import com.tsu.entity.AirDataValueAverage;
import com.tsu.entity.Relay;
import com.tsu.mapper.AirDataMapper;
import com.tsu.service.AirDataScopeService;
import com.tsu.service.AirDataService;
import com.tsu.service.DeviceService;
import com.tsu.service.RelayService;
import com.tsu.utils.AirDataWaringAction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author zzz
 */
@Service
@Slf4j
public class AirDataServiceImpl implements AirDataService {
    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    AirDataMapper airDataMapper;

    @Autowired
    AirDataScopeService airDataScopeService;

    @Autowired
    DeviceService deviceService;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    DataSourceTransactionManager transactionManager;


    @Autowired
    RelayService relayService;

    @Override
    @Transactional
    public void saveMqttBytes(byte[] bytes) {
        DefaultTransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
        transactionDefinition.setName("saveMqttBytes");
        transactionDefinition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus status = transactionManager.getTransaction(transactionDefinition);
        try {

            Map map = objectMapper.readValue(bytes, Map.class);
            if (map == null) {
                return;
            }
            Map uplinkMessage = (Map) map.get("uplink_message");
            Map endDeviceIds = (Map) map.get("end_device_ids");
            if (uplinkMessage == null) {
                return;
            }
            Map decodedPayload = (Map) uplinkMessage.get("decoded_payload");
            if (decodedPayload == null || endDeviceIds == null) {
                return;
            }
            AirData airData = new AirData();
            List als = (List) decodedPayload.get("als");
            List bat = (List) decodedPayload.get("bat");
            List eco2 = (List) decodedPayload.get("eco2");
            List humi = (List) decodedPayload.get("humi");
            List pres = (List) decodedPayload.get("pres");
            List temp = (List) decodedPayload.get("temp");
            List tvoc = (List) decodedPayload.get("tvoc");
            if (als != null) {
                airData.setIllumination(Double.parseDouble(als.get(0).toString()));
            }
            if (bat != null) {
                airData.setBatteryVoltage(Double.parseDouble(bat.get(0).toString()));
            }
            if (eco2 != null) {
                airData.setCo2(Double.parseDouble(eco2.get(0).toString()));
            }
            if (pres != null) {
                airData.setPressure(Double.parseDouble(pres.get(0).toString()));
            }
            if (temp != null) {
                airData.setTemperature(Double.parseDouble(temp.get(0).toString()));
            }
            if (tvoc != null) {
                airData.setTovc(Double.parseDouble(tvoc.get(0).toString()));
            }
            if (humi != null) {
                airData.setHumidity(Double.parseDouble(humi.get(0).toString()));
            }
            airData
                    .setDeviceId(endDeviceIds.get("dev_eui").toString())
                    .setCreateTime(new Date());
            Integer deviceId = deviceService.getIdByDeviceId(airData.getDeviceId());
            checkAirData(airData, deviceId);
            airDataMapper.save(airData);
            redisTemplate.opsForValue().set("air_sensor:online_device:" + deviceId, ".", 30, TimeUnit.MINUTES);
            transactionManager.commit(status);
        } catch (IOException e) {
            log.error("在保存MQTT服务器的上行端口传递来的数据时出现异常 {}", e.getMessage());
            transactionManager.rollback(status);
        }
    }

    private void checkAirData(AirData airData, Integer deviceId) throws IOException {
        AirDataScope airDataScope = airDataScopeService.getByDeviceId(deviceId);
        if (airDataScope == null || !airDataScope.getIsOpen()) {
            return;
        }
        AtomicBoolean flag = new AtomicBoolean(true);
        List<Integer> relayIds = objectMapper.readValue(airDataScope.getRelayIdsString().getBytes(), List.class);
        check(airData.getTemperature(), airDataScope.getTemperatureMax(), airDataScope.getTemperatureMin(), () -> {
            log.info("设备{}温度出现异常，即将开启对应继电器", deviceId);
            //1.将设备设置为报警状态
            deviceService.setDeviceIsAlarm(deviceId);
            if (relayIds != null && relayIds.size() > 0) {
                //2.在所得继电器中找到温度类型的继电器
                List<Relay> relayList = relayService.getByIdsAndType(RelayType.TEMPERATURE, relayIds);
                //3.触发继电器
                relayService.sendBatchInstruction(relayList, true);
                //4.将标志为设置为false
                flag.set(false);
            }
        }, () -> {
            log.info("设备{}温度出现异常，即将关闭对应继电器", deviceId);
            deviceService.setDeviceIsAlarm(deviceId);
            if (relayIds != null && relayIds.size() > 0) {
                List<Relay> relayList = relayService.getByIdsAndType(RelayType.TEMPERATURE, relayIds);
                relayService.sendBatchInstruction(relayList, false);
                flag.set(false);
            }
        });

        check(airData.getHumidity(), airDataScope.getHumidityMin(), airDataScope.getHumidityMax(), () -> {
            deviceService.setDeviceIsAlarm(deviceId);
            if (relayIds != null && relayIds.size() > 0) {
                List<Relay> relayList = relayService.getByIdsAndType(RelayType.HUMIDITY, relayIds);
                relayService.sendBatchInstruction(relayList, true);
                flag.set(false);
            }
        }, () -> {
            deviceService.setDeviceIsAlarm(deviceId);
            if (relayIds != null && relayIds.size() > 0) {
                List<Relay> relayList = relayService.getByIdsAndType(RelayType.HUMIDITY, relayIds);
                relayService.sendBatchInstruction(relayList, false);
                flag.set(false);
            }
        });

        check(airData.getIllumination(), airDataScope.getIlluminationMin(), airDataScope.getIlluminationMax(), () -> {
            deviceService.setDeviceIsAlarm(deviceId);
            if (relayIds != null && relayIds.size() > 0) {
                List<Relay> relayList = relayService.getByIdsAndType(RelayType.ILLUMINATION, relayIds);
                relayService.sendBatchInstruction(relayList, true);
                flag.set(false);
            }
        }, () -> {
            deviceService.setDeviceIsAlarm(deviceId);
            if (relayIds != null && relayIds.size() > 0) {
                List<Relay> relayList = relayService.getByIdsAndType(RelayType.ILLUMINATION, relayIds);
                relayService.sendBatchInstruction(relayList, false);
                flag.set(false);
            }
        });

        check(airData.getTovc(), airDataScope.getTovcMin(), airDataScope.getTovcMax(), () -> {
            deviceService.setDeviceIsAlarm(deviceId);
            if (relayIds != null && relayIds.size() > 0) {
                List<Relay> relayList = relayService.getByIdsAndType(RelayType.TVOC, relayIds);
                relayService.sendBatchInstruction(relayList, true);
                flag.set(false);
            }
        }, () -> {
            deviceService.setDeviceIsAlarm(deviceId);
            if (relayIds != null && relayIds.size() > 0) {
                List<Relay> relayList = relayService.getByIdsAndType(RelayType.TVOC, relayIds);
                relayService.sendBatchInstruction(relayList, false);
                flag.set(false);
            }
        });

        check(airData.getCo2(), airDataScope.getCo2Max(), airDataScope.getCo2Min(), () -> {
            deviceService.setDeviceIsAlarm(deviceId);
            if (relayIds != null && relayIds.size() > 0) {
                List<Relay> relayList = relayService.getByIdsAndType(RelayType.CO2, relayIds);
                relayService.sendBatchInstruction(relayList, true);
                flag.set(false);
            }
        }, () -> {
            deviceService.setDeviceIsAlarm(deviceId);
            if (relayIds != null && relayIds.size() > 0) {
                List<Relay> relayList = relayService.getByIdsAndType(RelayType.CO2, relayIds);
                relayService.sendBatchInstruction(relayList, false);
                flag.set(false);
            }
        });

        check(airData.getPressure(), airDataScope.getPressureMin(), airDataScope.getPressureMax(), () -> {
            deviceService.setDeviceIsAlarm(deviceId);
            if (relayIds != null && relayIds.size() > 0) {
                List<Relay> relayList = relayService.getByIdsAndType(RelayType.PRESSURE, relayIds);
                relayService.sendBatchInstruction(relayList, true);
                flag.set(false);
            }
        }, () -> {
            deviceService.setDeviceIsAlarm(deviceId);
            if (relayIds != null && relayIds.size() > 0) {
                List<Relay> relayList = relayService.getByIdsAndType(RelayType.PRESSURE, relayIds);
                relayService.sendBatchInstruction(relayList, false);
                flag.set(false);
            }
        });
        if (flag.get()) {
            deviceService.setDeviceIsNotAlarm(deviceId);
        }
    }


    private void check(Double checkValue, Double maxValue, Double minValue, AirDataWaringAction openRelays, AirDataWaringAction closeRelays) {
        if (checkValue == null) {
            return;
        }
        // TODO: 2021/5/14 重构逻辑
        if (maxValue != null && minValue != null) {
            if (!(checkValue <= maxValue && checkValue >= minValue)) {
                if (checkValue < minValue) {
                    //开继电器
                    openRelays.execute();
                } else {
                    //关继电器
                    closeRelays.execute();
                }
            }
        } else if (minValue != null) {
            if (checkValue < minValue) {
                //开继电器
                openRelays.execute();
            }
        } else if (maxValue != null) {
            if (checkValue > maxValue) {
                //关继电器
                closeRelays.execute();
            }
        }
    }


    @Override
    public List<AirDataValueAverage> getTemperatureAverage(Date startTime, Date endTime) {
        return airDataMapper.getTemperatureAverage(startTime, endTime);
    }

    @Override
    public List<AirDataValueAverage> getHumidityAverage(Date startTime, Date endTime) {
        return airDataMapper.getHumidityAverage(startTime, endTime);
    }

    @Override
    public Long getCount() {
        return airDataMapper.getCount();
    }

    @Override
    public Long getCountByDeviceId(List<String> deviceIds) {
        return airDataMapper.getCountByDeviceIds(deviceIds);
    }

    @Override
    public AirData getLatest(String deviceId) {
        return airDataMapper.getLatest(deviceId);
    }

}

