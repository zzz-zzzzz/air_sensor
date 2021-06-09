package com.tsu.entity;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author zzz
 */
@Data
@ToString
@Accessors(chain = true)
public class User {
    /**
     * 用户主键
     */
    private Integer id;
    /**
     * 用户用户名
     */
    private String username;
    /**
     * 用户密码
     */
    private String password;
    /**
     * 用户加密密码所用的盐
     */
    private String salt;
    /**
     * 基地列表
     */
    private List<Integer> locationIdList;
}
