package com.tsu.service;

import com.tsu.entity.AirData;
import com.tsu.entity.AirDataValueAverage;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author zzz
 */
public interface AirDataService {


    /**
     * 获取一个时间段的平均温度
     *
     * @param startTime
     * @param endTime
     * @return
     */
    List<AirDataValueAverage> getTemperatureAverage(Date startTime, Date endTime);

    /**
     * 获取一个时间段的平均湿度
     *
     * @param startTime
     * @param endTime
     * @return
     */
    List<AirDataValueAverage> getHumidityAverage(Date startTime, Date endTime);


    /**
     * @return
     */
    Long getCount();

    /**
     * @param deviceIds
     * @return
     */
    Long getCountByDeviceId(List<String> deviceIds);

    /**
     * 获取最新的数据
     *
     * @param deviceId
     * @return
     */
    AirData getLatest(String deviceId);

    void saveMqttMap(Map payLoad);
}
