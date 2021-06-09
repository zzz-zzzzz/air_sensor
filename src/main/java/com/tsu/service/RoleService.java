package com.tsu.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tsu.entity.Role;

import java.util.List;

/**
 * @author zzz
 */
public interface RoleService {
    /**
     * 通过用户id查询角色信息
     * @param userId
     * @return
     */
    List<String> getRoleByUserId(Integer userId);
}
