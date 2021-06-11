package com.tsu.service.impl;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tsu.entity.User;
import com.tsu.exception.HasUserException;
import com.tsu.mapper.UserMapper;
import com.tsu.service.UserService;
import com.tsu.utils.EncryptUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.List;
import java.util.Map;

/**
 * @author zzz
 */
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private DataSourceTransactionManager transactionManager;

    @Autowired
    UserMapper userMapper;

    @Override
    public User getByUsername(String name) {
        return userMapper.getByUsername(name);
    }

    @Override
    public void add(User user) throws HasUserException {
        // TODO: 2021/5/17 查询是否有这个用户 如果有返回错误码
        if (userMapper.getByUsername(user.getUsername()) != null) {
            throw new HasUserException("存在这个用户");
        }
        Map<String, String> encrypt = EncryptUtil.encrypt(user.getPassword());
        String password = encrypt.get("password");
        String salt = encrypt.get("salt");
        user
                .setPassword(password)
                .setSalt(salt);
        userMapper.save(user);
    }

    @Override
    @Transactional
    public void delete(Integer userId) {
        DefaultTransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
        transactionDefinition.setName("UserService.delete");
        transactionDefinition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus status = transactionManager.getTransaction(transactionDefinition);
        try {
            userMapper.delete(userId);
            userMapper.deleteRelationByUserId(userId);
            transactionManager.commit(status);
        } catch (Exception e) {
            transactionManager.rollback(status);
            throw e;
        }
    }

    @Override
    public void update(User user) {
        Map<String, String> encrypt = EncryptUtil.encrypt(user.getPassword());
        user
                .setPassword(encrypt.get("password"))
                .setSalt(encrypt.get("salt"));
        userMapper.update(user);
    }

    @Override
    public PageInfo<User> getAllByPage(Integer page, Integer size) {
        PageHelper.startPage(page, size);
        List<User> userList = userMapper.findAll();
        PageInfo<User> pageInfo = new PageInfo<>(userList);
        return pageInfo;
    }

    public static void main(String[] args) {
        Map<String, String> encrypt = EncryptUtil.encrypt("123456");
        System.out.println(encrypt);
    }
}
