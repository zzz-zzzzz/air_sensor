package com.tsu.entity;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author zzz
 */
@Data
@Accessors(chain = true)
@ToString
public class AirDataValueAverage {
    private String deviceId;
    private Double average;
}
