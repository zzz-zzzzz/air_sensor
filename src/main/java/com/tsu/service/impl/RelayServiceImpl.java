package com.tsu.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tsu.entity.Relay;
import com.tsu.gateway.MqttGateway;
import com.tsu.mapper.RelayMapper;
import com.tsu.service.GreenHouseService;
import com.tsu.service.LocationService;
import com.tsu.service.RelayService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.codec.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.message.AdviceMessage;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author zzz
 */
@Service
@Slf4j
public class RelayServiceImpl implements RelayService {
    @Autowired
    DataSourceTransactionManager transactionManager;

    @Autowired
    RelayMapper relayMapper;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    GreenHouseService greenHouseService;

    @Autowired
    LocationService locationService;

    @Autowired
    MqttGateway mqttGateway;


    @Override
    public void add(Relay relay) {
        relayMapper.save(relay);
    }

    @Transactional
    @Override
    public void saveMqttBytes(byte[] bytes) {
        // TODO: 2021/5/10 存在bug
        DefaultTransactionDefinition definition = new DefaultTransactionDefinition();
        definition.setName("relayService.saveMqttBytes");
        definition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus status = transactionManager.getTransaction(definition);
        try {
            Map map = objectMapper.readValue(bytes, Map.class);
            Map uplinkMessage = (Map) map.get("uplink_message");
            if (uplinkMessage != null) {
                Map endDeviceIds = (Map) map.get("end_device_ids");
                String deviceId = (String) endDeviceIds.get("device_id");
                String relayStatusBase64 = (String) uplinkMessage.get("frm_payload");
                byte[] relayStatusHexBytes = Base64.decode(relayStatusBase64);
                String relayStatusHex = Hex.encodeToString(relayStatusHexBytes);
                relayStatusHex = relayStatusHex.substring(0, relayStatusHex.length() - 2);
                String relayStatusBin = hexToBin(relayStatusHex);
                for (int i = relayStatusBin.length() - 1; i >= 0; i--) {
                    int identityId = relayStatusBin.length() - i;
                    relayMapper.updateIsOpenByIdentityIdAndDeviceId(relayStatusBin.charAt(i) != '0', identityId, deviceId);
                }
                transactionManager.commit(status);
            }
        } catch (IOException e) {
            transactionManager.rollback(status);
            e.printStackTrace();
            log.info("在转换mqtt发送的数据时发生错误");
        }
    }

    private String hexToBin(String hex) {
        String bin = "";
        String binFragment = "";
        int iHex;
        hex = hex.trim();
        hex = hex.replaceFirst("0x", "");

        for (int i = 0; i < hex.length(); i++) {
            iHex = Integer.parseInt("" + hex.charAt(i), 16);
            binFragment = Integer.toBinaryString(iHex);

            while (binFragment.length() < 4) {
                binFragment = "0" + binFragment;
            }
            bin += binFragment;
        }
        return bin;
    }

    @Override
    public List<Relay> getByGreenHouseId(Integer greenHouseId) {
        return relayMapper.getByGreenHouseId(greenHouseId);
    }

    @Override
    public PageInfo<Relay> getAllHasGreenHouseByPage(Integer page, Integer size) {
        PageHelper.startPage(page, size);
        List<Relay> relayList = relayMapper.getAllHasGreenHouse();
        return new PageInfo<>(relayList);
    }

    @Override
    public PageInfo<Relay> getAllHasGreenHouseByPageAndUserId(Integer page, Integer size, Integer userId) {
        List<Integer> locationIds = locationService.getIdsByUserId(userId);
        List<Integer> greenHouseIds = greenHouseService.getIdsByLocationIds(locationIds);
        PageHelper.startPage(page, size);
        List<Relay> relayList = relayMapper.getAllHasGreenHouseByGreenHouseIds(greenHouseIds);
        return new PageInfo<>(relayList);
    }

    @Override
    public void delete(Integer id) {
        relayMapper.deleteById(id);
    }

    @Override
    public void update(Relay relay) {
        relayMapper.update(relay);
    }

    @Override
    @Transactional
    public void sendInstruction(Integer id, Boolean action) {
        //1.获取设备的唯一id和继电器设备标识设备id
        Relay relay = relayMapper.getIdentityIdAndDeviceIdById(id);
        send(relay, action);
    }

    private void send(Relay relay, Boolean action) {
        //2.获取指令
        String instruction = encodeInstruction(action, relay.getIdentityId());
        //3.发送指令
        AdviceMessage<byte[]> out = new AdviceMessage<byte[]>(getSendMqttContent(instruction).getBytes(), null);
        mqttGateway.send(getTopic(relay.getDeviceId()), out);
        relayMapper.updateIsOpenById(action, relay.getId());
    }

    private String getTopic(String deviceId) {
        String preTopic = "v3/lora-relay@ttn/devices/";
        String postTopic = "/down/push";
        return preTopic + deviceId + postTopic;
    }

    private static String getSendMqttContent(String Instruction) {
        String preContent = " {   \"downlinks\": [{     \"f_port\": 15,     \"frm_payload\": \"";
        String postContent = "\"   }] }";
        return preContent + Instruction + postContent;
    }

    private static String encodeInstruction(boolean isOpen, Integer identityId) {
        if (identityId == 0) {
            throw new RuntimeException("");
        }

        String binaryString = Integer.toBinaryString(identityId);
        if (binaryString.length() < 6) {
            while (binaryString.length() < 6) {
                binaryString = "0" + binaryString;
            }
        }

        binaryString = "1" + (isOpen ? "1" : "0") + binaryString;
        int decimal = Integer.parseInt(binaryString, 2);
        byte[] bytes = {(byte) decimal};
        return Base64.encodeToString(bytes);
    }

    @Override
    public void checkAndSendInstruction(Integer id, Boolean action, Integer userId) throws AuthorizationException {
        Integer greenHouseId = relayMapper.getIdGreenHouseIdById(userId);
        Integer locationId = greenHouseService.getLocationIdByGreenHouseId(greenHouseId);
        if (locationService.hasLocation(userId, locationId)) {
            sendInstruction(id, action);
        } else {
            throw new AuthorizationException("没有权限");
        }
    }

    @Override
    public void checkDelete(Integer id, Integer userId) throws AuthorizationException {
        Integer greenHouseId = relayMapper.getIdGreenHouseIdById(id);
        Integer locationId = greenHouseService.getLocationIdByGreenHouseId(greenHouseId);
        if (locationService.hasLocation(userId, locationId)) {
            delete(id);
        } else {
            throw new AuthorizationException("没有权限");
        }
    }

    @Override
    public void checkAdd(Relay relay, Integer userId) {
        Integer locationId = greenHouseService.getLocationIdByGreenHouseId(relay.getGreenHouseId());
        if (locationService.hasLocation(userId, locationId)) {
            add(relay);
        } else {
            throw new AuthorizationException("没有权限");
        }
    }

    @Override
    public void checkUpdate(Relay relay, Integer userId) {
        Integer locationId = greenHouseService.getLocationIdByGreenHouseId(relay.getGreenHouseId());
        if (locationService.hasLocation(userId, locationId)) {
            update(relay);
        } else {
            throw new AuthorizationException("没有权限");
        }
    }

    @Override
    public Integer getCount() {
        return relayMapper.getCount();
    }

    @Override
    public Integer getCountByGreenHouseIds(List<Integer> greenHouseIds) {
        return relayMapper.getCountByGreenHouseIds(greenHouseIds);
    }

    @Override
    public List<Relay> getByIdsAndType(int type, List<Integer> ids) {
        return relayMapper.getByIdsAndType(type, ids);
    }

    @Override
    public void sendBatchInstruction(List<Relay> relayList, boolean action) {
        relayList.forEach(relay -> {
            send(relay, action);
        });
    }

    @Override
    public void switchAutoTrigger(Integer id, Boolean autoTrigger) {
        relayMapper.updateAutoTriggerById(id, autoTrigger);
    }

    @Override
    public void checkSwitchAutoTrigger(Integer id, Boolean autoTrigger, Integer userId) {
        Integer greenHouseId = relayMapper.getIdGreenHouseIdById(id);
        Integer locationId = greenHouseService.getLocationIdByGreenHouseId(greenHouseId);
        if (locationService.hasLocation(userId, locationId)) {
            switchAutoTrigger(id, autoTrigger);
        } else {
            throw new AuthorizationException("没有权限");
        }
    }
}
