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
import org.springframework.util.ObjectUtils;

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
    public void saveMqttMap(Map payload) {
        Object object = payload.get("object");
        if (ObjectUtils.isEmpty(object)) {
            return;
        }
        Map objectMap = (Map) object;
        Object data = objectMap.get("data");
        if (ObjectUtils.isEmpty(data)) {
            return;
        }
        Map dataMap = (Map) data;

        AirData airData = new AirData();
        List als = (List) dataMap.get("als");
        List bat = (List) dataMap.get("bat");
        List eco2 = (List) dataMap.get("eco2");
        List humi = (List) dataMap.get("humi");
        List pres = (List) dataMap.get("pres");
        List temp = (List) dataMap.get("temp");
        List tvoc = (List) dataMap.get("tvoc");

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
                .setDeviceId(payload.get("devEUI").toString())
                .setCreateTime(new Date());
        Integer deviceId = deviceService.getIdByDeviceId(airData.getDeviceId());
        checkAirData(airData, deviceId);
        airDataMapper.save(airData);
        redisTemplate.opsForValue().set("air_sensor:online_device:" + deviceId, ".", 30, TimeUnit.MINUTES);
    }

    private void checkAirData(AirData airData, Integer deviceId) {
        AirDataScope airDataScope = airDataScopeService.getByDeviceId(deviceId);
        if (airDataScope == null || !airDataScope.getIsOpen()) {
            return;
        }
        AtomicBoolean flag = new AtomicBoolean(true);
        List<Integer> relayIds = null;
        try {
            relayIds = objectMapper.readValue(airDataScope.getRelayIdsString().getBytes(), List.class);
        } catch (IOException e) {
            log.error("在转换数据库存储的relayIds是出现异常，格式不正确");
            return;
        }
        List<Integer> finalRelayIds = relayIds;
        check(airData.getTemperature(), airDataScope.getTemperatureMax(), airDataScope.getTemperatureMin(), () -> {
            log.info("设备{}温度出现异常，即将开启对应继电器", deviceId);
            //1.将设备设置为报警状态
            deviceService.setDeviceIsAlarm(deviceId);
            if (finalRelayIds != null && finalRelayIds.size() > 0) {
                //2.在所得继电器中找到温度类型的继电器
                List<Relay> relayList = relayService.getByIdsAndType(RelayType.TEMPERATURE, finalRelayIds);
                //3.触发继电器
                relayService.sendBatchInstruction(relayList, true);
                //4.将标志为设置为false
                flag.set(false);
            }
        }, () -> {
            log.info("设备{}温度出现异常，即将关闭对应继电器", deviceId);
            deviceService.setDeviceIsAlarm(deviceId);
            if (finalRelayIds != null && finalRelayIds.size() > 0) {
                List<Relay> relayList = relayService.getByIdsAndType(RelayType.TEMPERATURE, finalRelayIds);
                relayService.sendBatchInstruction(relayList, false);
                flag.set(false);
            }
        });

        check(airData.getHumidity(), airDataScope.getHumidityMin(), airDataScope.getHumidityMax(), () -> {
            deviceService.setDeviceIsAlarm(deviceId);
            if (finalRelayIds != null && finalRelayIds.size() > 0) {
                List<Relay> relayList = relayService.getByIdsAndType(RelayType.HUMIDITY, finalRelayIds);
                relayService.sendBatchInstruction(relayList, true);
                flag.set(false);
            }
        }, () -> {
            deviceService.setDeviceIsAlarm(deviceId);
            if (finalRelayIds != null && finalRelayIds.size() > 0) {
                List<Relay> relayList = relayService.getByIdsAndType(RelayType.HUMIDITY, finalRelayIds);
                relayService.sendBatchInstruction(relayList, false);
                flag.set(false);
            }
        });

        check(airData.getIllumination(), airDataScope.getIlluminationMin(), airDataScope.getIlluminationMax(), () -> {
            deviceService.setDeviceIsAlarm(deviceId);
            if (finalRelayIds != null && finalRelayIds.size() > 0) {
                List<Relay> relayList = relayService.getByIdsAndType(RelayType.ILLUMINATION, finalRelayIds);
                relayService.sendBatchInstruction(relayList, true);
                flag.set(false);
            }
        }, () -> {
            deviceService.setDeviceIsAlarm(deviceId);
            if (finalRelayIds != null && finalRelayIds.size() > 0) {
                List<Relay> relayList = relayService.getByIdsAndType(RelayType.ILLUMINATION, finalRelayIds);
                relayService.sendBatchInstruction(relayList, false);
                flag.set(false);
            }
        });

        check(airData.getTovc(), airDataScope.getTovcMin(), airDataScope.getTovcMax(), () -> {
            deviceService.setDeviceIsAlarm(deviceId);
            if (finalRelayIds != null && finalRelayIds.size() > 0) {
                List<Relay> relayList = relayService.getByIdsAndType(RelayType.TVOC, finalRelayIds);
                relayService.sendBatchInstruction(relayList, true);
                flag.set(false);
            }
        }, () -> {
            deviceService.setDeviceIsAlarm(deviceId);
            if (finalRelayIds != null && finalRelayIds.size() > 0) {
                List<Relay> relayList = relayService.getByIdsAndType(RelayType.TVOC, finalRelayIds);
                relayService.sendBatchInstruction(relayList, false);
                flag.set(false);
            }
        });

        check(airData.getCo2(), airDataScope.getCo2Max(), airDataScope.getCo2Min(), () -> {
            deviceService.setDeviceIsAlarm(deviceId);
            if (finalRelayIds != null && finalRelayIds.size() > 0) {
                List<Relay> relayList = relayService.getByIdsAndType(RelayType.CO2, finalRelayIds);
                relayService.sendBatchInstruction(relayList, true);
                flag.set(false);
            }
        }, () -> {
            deviceService.setDeviceIsAlarm(deviceId);
            if (finalRelayIds != null && finalRelayIds.size() > 0) {
                List<Relay> relayList = relayService.getByIdsAndType(RelayType.CO2, finalRelayIds);
                relayService.sendBatchInstruction(relayList, false);
                flag.set(false);
            }
        });

        check(airData.getPressure(), airDataScope.getPressureMin(), airDataScope.getPressureMax(), () -> {
            deviceService.setDeviceIsAlarm(deviceId);
            if (finalRelayIds != null && finalRelayIds.size() > 0) {
                List<Relay> relayList = relayService.getByIdsAndType(RelayType.PRESSURE, finalRelayIds);
                relayService.sendBatchInstruction(relayList, true);
                flag.set(false);
            }
        }, () -> {
            deviceService.setDeviceIsAlarm(deviceId);
            if (finalRelayIds != null && finalRelayIds.size() > 0) {
                List<Relay> relayList = relayService.getByIdsAndType(RelayType.PRESSURE, finalRelayIds);
                relayService.sendBatchInstruction(relayList, false);
                flag.set(false);
            }
        });
        if (flag.get()) {
            deviceService.setDeviceIsNotAlarm(deviceId);
        }
    }


    private void check(Double checkValue, Double maxValue, Double minValue, AirDataWaringAction
            openRelays, AirDataWaringAction closeRelays) {
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

