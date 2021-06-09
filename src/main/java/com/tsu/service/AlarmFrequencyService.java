package com.tsu.service;

import com.tsu.entity.AlarmFrequency;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zzz
 */
public interface AlarmFrequencyService {
    /**
     * 为设备的报警值添加一
     *
     * @param id
     */
    void addAlarmFrequency(Integer deviceId);

    Integer getCount();

    List<AlarmFrequency> getCountByDates(List<String> dateList);

    List<AlarmFrequency> getCountByDatesAndDeviceIds(List<String> dateList, List<Integer> deviceIds);

    Integer getCountByDeviceId(ArrayList<Integer> deviceIds);
}
