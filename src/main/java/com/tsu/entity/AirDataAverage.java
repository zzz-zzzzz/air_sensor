package com.tsu.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @author zzz
 */
@Data
@Accessors(chain = true)
public class AirDataAverage {
    /**
     * 主键
     */
    private Long id;

    /**
     * 平均温度
     */
    private Double temperatureAverage;

    /**
     * 平均湿度
     */
    private Double humidityAverage;

    /**
     * 平均大气压
     */
    private Double pressureAverage;

    /**
     * 平均光照
     */
    private Double illuminationAverage;

    /**
     * 平均CO2浓度
     */
    private Double co2Average;

    /**
     * 平均TOVC
     */
    private Double tvocAverage;

    /**
     * 记录时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date recordTime;

    /**
     * 唯一标识
     */
    private String deviceId;

}
