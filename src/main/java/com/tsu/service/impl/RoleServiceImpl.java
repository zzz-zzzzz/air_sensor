package com.tsu.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tsu.entity.Role;
import com.tsu.mapper.RoleMapper;
import com.tsu.service.RoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author zzz
 */
@Service
@Slf4j
public class RoleServiceImpl implements RoleService {

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    RoleMapper roleMapper;

    @Override
    public List<String> getRoleByUserId(Integer userId) {
        String redisRolesKey = "air_sensor:roles:" + userId;
        List<String> roleList = null;
        String roleListJson = redisTemplate.opsForValue().get(redisRolesKey);
        try {
            if (roleListJson == null) {
                roleList = roleMapper.getByUserId(userId);
                redisTemplate.opsForValue().set(redisRolesKey, objectMapper.writeValueAsString(roleList));
            } else {
                roleList = objectMapper.readValue(roleListJson, List.class);
            }
            return roleList;
        } catch (JsonProcessingException e) {
            log.error("redis 中获取的角色信息转换失败 {}", e.getMessage());
            return null;
        }
    }
}
