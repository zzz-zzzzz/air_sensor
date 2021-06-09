package com.tsu.service.impl;

import com.tsu.entity.AlarmFrequency;
import com.tsu.mapper.AlarmFrequencyMapper;
import com.tsu.service.AlarmFrequencyService;
import com.tsu.service.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author zzz
 */
@Service
public class AlarmFrequencyServiceImpl implements AlarmFrequencyService {


    @Autowired
    AlarmFrequencyMapper alarmFrequencyMapper;

    @Autowired
    DeviceService deviceService;

    @Override
    public void addAlarmFrequency(Integer deviceId) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date currentDay = calendar.getTime();
        AlarmFrequency alarmFrequency = alarmFrequencyMapper.getByDeviceIdAndAlarmDate(deviceId, currentDay);
        if (alarmFrequency == null) {
            AlarmFrequency saveAlarmFrequency = new AlarmFrequency()
                    .setAlarmDate(currentDay)
                    .setFrequency(1)
                    .setDeviceId(deviceId);
            alarmFrequencyMapper.save(saveAlarmFrequency);
        } else {
            alarmFrequencyMapper.updateFrequencyById(alarmFrequency.getFrequency() + 1, alarmFrequency.getId());
        }
    }

    @Override
    public Integer getCount() {
        return alarmFrequencyMapper.getCount();
    }

    @Override
    public List<AlarmFrequency> getCountByDates(List<String> dateList) {
        return alarmFrequencyMapper.getCountByDates(dateList);
    }

    @Override
    public List<AlarmFrequency> getCountByDatesAndDeviceIds(List<String> dateList, List<Integer> deviceIds) {
        return alarmFrequencyMapper.getCountByDatesAndDeviceIds(dateList,deviceIds);
    }

    @Override
    public Integer getCountByDeviceId(ArrayList<Integer> deviceIds) {
        return alarmFrequencyMapper.getCountByDeviceId(deviceIds);
    }
}
