package com.tsu.mapper;

import com.tsu.entity.Role;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author zzz
 */
@Mapper
public interface RoleMapper {
    /**
     * 通过用户id查询用户的角色
     * @param userId
     * @return
     */
    List<String> getByUserId(Integer userId);
}
