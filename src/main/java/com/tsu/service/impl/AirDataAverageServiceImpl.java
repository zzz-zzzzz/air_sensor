package com.tsu.service.impl;

import com.tsu.entity.AirDataAverage;
import com.tsu.mapper.AirDataAverageMapper;
import com.tsu.service.AirDataAverageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author zzz
 */
@Service
public class AirDataAverageServiceImpl implements AirDataAverageService {

    @Autowired
    AirDataAverageMapper airDataAverageMapper;

    @Override
    public void batchSave(List<AirDataAverage> airDataAverageList) {
        airDataAverageMapper.batchSave(airDataAverageList);
    }

    @Override
    public List<AirDataAverage> getByRecodeTimeAndDeviceId(List<String> recordTimeList, String deviceId) {
        //todo 其实这里应该有权限的
        return airDataAverageMapper.getByRecordeTimeAndDeviceId(recordTimeList,deviceId);
    }
}
