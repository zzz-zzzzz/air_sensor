package com.tsu.mapper;

import com.tsu.entity.AirData;
import com.tsu.entity.AirDataValueAverage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author zzz
 */
@Mapper
public interface AirDataMapper {
    /**
     * 保存AirData对象到数据库
     *
     * @param airData
     */
    void save(AirData airData);

    /**
     * 获取指定时间所有设备的平均温度
     *
     * @param startTime
     * @param endTime
     * @return
     */
    List<AirDataValueAverage> getTemperatureAverage(@Param("startTime") Date startTime, @Param("endTime") Date endTime);

    Long getCount();

    Long getCountByDeviceIds(@Param("deviceIds") List<String> deviceIds);

    AirData getLatest(String deviceId);

    List<AirDataValueAverage> getHumidityAverage(@Param("startTime") Date startTime, @Param("endTime") Date endTime);
}
