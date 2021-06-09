package com.tsu.service;

import com.tsu.entity.AirDataAverage;

import java.util.List;

/**
 * @author zzz
 */
public interface AirDataAverageService {

    void batchSave(List<AirDataAverage> airDataAverageList);

    List<AirDataAverage> getByRecodeTimeAndDeviceId(List<String> recordTimeList, String deviceId);
}
