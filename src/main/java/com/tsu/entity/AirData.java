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
public class AirData {

    private Long id;

    private String deviceId;

    private Double humidity;

    private Double temperature;

    private Double pressure;

    @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss", timezone = "GMT+8")
    private Date createTime;
    
    private Double tovc;

    private Double batteryVoltage;

    private Double co2;

    private Double illumination;

}
