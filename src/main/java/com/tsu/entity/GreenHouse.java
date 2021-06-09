package com.tsu.entity;

import lombok.Data;

import java.util.List;

/**
 * @author zzz
 */
@Data
public class GreenHouse {
    /**
     * 组件
     */
    private Integer id;
    /**
     * 大棚名
     */
    private String name;
    /**
     * 所在基地id
     */
    private Integer locationId;
    /**
     * 设备列表
     */
    private List<Device> children;

    /**
     * 大棚所在的基地
     */
    private Location location;
}
