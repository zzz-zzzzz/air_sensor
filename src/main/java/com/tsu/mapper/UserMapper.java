package com.tsu.mapper;


import com.tsu.entity.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author zzz
 */
@Mapper
public interface UserMapper {
    /**
     * 通过username获取用户
     *
     * @param username
     * @return
     */
    User getByUsername(String username);

    /**
     * 通过id获取用户
     *
     * @param userId
     * @return
     */
    User getById(String userId);

    /**
     * 保存用户
     *
     * @param user
     */
    void save(User user);

    /**
     * 通过id删除用户
     *
     * @param userId
     */
    void delete(Integer userId);

    /**
     * 更新用户
     *
     * @param user
     */
    void update(User user);

    /**
     * 查询所有用户
     *
     * @return
     */
    List<User> findAll();

    /**
     * 根据userId删除与location之间的关系
     * @param userId
     */
    void deleteRelationByUserId(Integer userId);

}
