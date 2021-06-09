package com.tsu.mapper;

import com.tsu.entity.AlarmFrequency;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author zzz
 */
@Mapper
public interface AlarmFrequencyMapper {
    /**
     * 通过设备id来获取报警频率
     *
     * @param deviceId
     * @return
     */
    AlarmFrequency getByDeviceIdAndAlarmDate(@Param("deviceId") Integer deviceId, @Param("alarmDate") Date alarmDate);

    /**
     * 保存
     *
     * @param saveAlarmFrequency
     */
    void save(AlarmFrequency saveAlarmFrequency);

    /**
     * 通过主键来添加频率
     *
     * @param frequency
     * @param id
     */
    void updateFrequencyById(@Param("frequency") Integer frequency, @Param("id") Integer id);


    Integer getCount();

    List<AlarmFrequency> getCountByDates(@Param("dateList") List<String> dateList);

    List<AlarmFrequency> getCountByDatesAndDeviceIds(@Param("dateList") List<String> dateList,@Param("deviceIds") List<Integer> deviceIds);

    Integer getCountByDeviceId(@Param("deviceIds") ArrayList<Integer> deviceIds);
}
