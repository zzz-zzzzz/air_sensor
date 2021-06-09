package com.tsu.entity;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author zzz
 */
@Data
@Accessors(chain = true)
public class Role {
    /**
     * 主键
     */
    private Integer id;
    /**
     * 角色名
     */
    private String name;
    /**
     * 角色code
     */
    private String code;
}
