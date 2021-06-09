package com.tsu.service;


import com.github.pagehelper.PageInfo;
import com.tsu.entity.User;
import com.tsu.exception.HasUserException;

import java.util.List;

/**
 * @author zzz
 */
public interface UserService {
    /**
     * 通过用户名查询用户
     *
     * @param name
     * @return
     */
    User getByUsername(String name);

    /**
     * 添加用户
     *
     * @param user
     */
    void add(User user) throws HasUserException;

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
     * 查询所有的用户（分页）
     *
     * @param page
     * @param size
     * @return
     */
    PageInfo<User> getAllByPage(Integer page, Integer size);
}
