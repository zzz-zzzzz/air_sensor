package com.tsu.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.List;

/**
 * @author zzz
 */
@Data
public class AirDataScope {
    /**
     * 主键
     */
    private Integer id;

    /**
     * 设备的主键
     */
    private Integer deviceId;

    /**
     * 是否开启警报
     */
    private Boolean isOpen;

    /**
     * 温度的最小值
     */
    private Double temperatureMin;

    /**
     * 温度的最大值
     */
    private Double temperatureMax;

    /**
     * 湿度的最小值
     */
    private Double humidityMin;

    /**
     * 湿度的最大值
     */
    private Double humidityMax;

    /**
     * 压强的最小值
     */
    private Double pressureMin;

    /**
     * 压强的最大值
     */
    private Double pressureMax;

    /**
     * tovc的最小值
     */
    private Double tovcMin;

    /**
     * tovc的最大值
     */
    private Double tovcMax;

    /**
     * CO2的最小值
     */
    private Double co2Min;

    /**
     * CO2的最大值
     */
    private Double co2Max;

    /**
     * 光照的最小值
     */
    private Double illuminationMin;

    /**
     * 光照的最大值
     */
    private Double illuminationMax;

    /**
     *
     */
    private List<Integer> relayIds;

    /**
     *
     */
    private String relayIdsString;
}
