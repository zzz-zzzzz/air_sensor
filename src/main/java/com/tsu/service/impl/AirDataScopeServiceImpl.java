package com.tsu.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tsu.entity.AirDataScope;
import com.tsu.mapper.AirDataScopeMapper;
import com.tsu.service.AirDataScopeService;
import com.tsu.service.DeviceService;
import com.tsu.service.LocationService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.AuthorizationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zzz
 */
@Slf4j
@Service
public class AirDataScopeServiceImpl implements AirDataScopeService {
    @Autowired
    AirDataScopeMapper airDataScopeMapper;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    DeviceService deviceService;

    @Autowired
    LocationService locationService;


    @Override
    public AirDataScope getByDeviceId(Integer deviceId) {
        return airDataScopeMapper.getByDeviceId(deviceId);
    }

    @Override
    public void saveOrUpdate(AirDataScope airDataScope) throws JsonProcessingException {
        if (ObjectUtils.isEmpty(airDataScope)) {
            return;
        }
        if (ObjectUtils.isEmpty(airDataScope.getId())) {
            airDataScope.setRelayIdsString(relayIdsTORelayIdsStr(airDataScope.getRelayIds()));
            airDataScopeMapper.save(airDataScope);
        } else {
            airDataScope.setRelayIdsString(relayIdsTORelayIdsStr(airDataScope.getRelayIds()));
            airDataScopeMapper.update(airDataScope);
        }
    }

    private String relayIdsTORelayIdsStr(List<Integer> relayIds) throws JsonProcessingException {
        if (!ObjectUtils.isEmpty(relayIds)) {
            return objectMapper.writeValueAsString(relayIds);
        }
        return null;
    }


    @Override
    public void saveOrUpdateAndCheck(AirDataScope airDataScope, Integer userId) throws JsonProcessingException {
        if (ObjectUtils.isEmpty(airDataScope)) {
            return;
        }
        Integer deviceId = airDataScope.getDeviceId();
        Integer locationId = deviceService.getLocationByDeviceId(deviceId);
        if (locationService.hasLocation(userId, locationId)) {
            if (ObjectUtils.isEmpty(airDataScope.getId())) {
                airDataScope.setRelayIdsString(relayIdsTORelayIdsStr(airDataScope.getRelayIds()));
                airDataScopeMapper.save(airDataScope);
            } else {
                airDataScope.setRelayIdsString(relayIdsTORelayIdsStr(airDataScope.getRelayIds()));
                airDataScopeMapper.update(airDataScope);
            }
        } else {
            throw new AuthorizationException("没有添加权限");
        }
    }


}
