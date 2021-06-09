package com.tsu.mapper;

import com.tsu.entity.AirDataAverage;
import com.tsu.entity.AirDataValueAverage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author zzz
 */
@Mapper
public interface AirDataAverageMapper {

    /**
     * 保存
     *
     * @param airDataAverage
     */
    void save(AirDataAverage airDataAverage);

    void batchSave(@Param("airDataValueAverageList") List<AirDataAverage> airDataValueAverageList);

    List<AirDataAverage> getByRecordeTimeAndDeviceId(@Param("recordTimeList") List<String> recordTimeList, @Param("deviceId") String deviceId);
}
