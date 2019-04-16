package com.coral.learning.data.mybatis.service.impl;

import com.coral.learning.data.mybatis.dao.UserDao;
import com.coral.learning.data.mybatis.entity.User;
import com.coral.learning.data.mybatis.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service("userService")
public class UserServiceImpl implements UserService {

    @Resource
    private UserDao userDao;


    public User getUserById(int userId) {
        return userDao.selectByPrimaryKey(userId);
    }

    public boolean addUser(User record){
        boolean result = false;
        try {
            userDao.insertSelective(record);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

}