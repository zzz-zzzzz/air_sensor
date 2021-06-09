package com.tsu.entity;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author zzz
 */
@Data
@Accessors(chain = true)
public class Relay {
    /**
     * 主键
     */
    private Integer id;

    /**
     * 继电器名称
     */
    private String name;

    /**
     * 继电器类型
     */
    private Integer type;

    /**
     * 大棚id
     */
    private Integer greenHouseId;

    /**
     * 设备是否开启
     */
    private Boolean isOpen;

    /**
     * 唯一标识id
     */
    private Integer identityId;


    /**
     * 所属大棚
     */
    private GreenHouse greenHouse;

    /**
     * 所属的继电器设备（不是检测设备）
     */
    private String deviceId;


    /**
     * 是否可以自动触发
     */
    private Boolean autoTrigger;
}
