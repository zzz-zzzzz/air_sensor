package com.tsu.entity;

import lombok.Data;

/**
 * @author zzz
 */
@Data
public class Device {
    /**
     * 主键
     */
    private Integer id;
    /**
     * 设备唯一标识
     */
    private String deviceId;
    /**
     * 设备名称
     */
    private String name;
    /**
     * 设备所在大棚的主键
     */
    private Integer greenHouseId;

    /**
     * 设备所在的大棚
     */
    private GreenHouse greenHouse;
}
