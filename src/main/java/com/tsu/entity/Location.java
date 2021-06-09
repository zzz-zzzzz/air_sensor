package com.tsu.entity;

import lombok.Data;

import java.util.List;

/**
 * @author zzz
 */
@Data
public class Location {
    /**
     * 主键
     */
    private Integer id;
    /**
     * 基地名
     */
    private String name;
    /**
     * 大棚列表
     */
    private List<GreenHouse> children;



    /**
     * 精度
     */
    private Double latitude;

    /**
     * 维度
     */
    private Double longitude;
}
