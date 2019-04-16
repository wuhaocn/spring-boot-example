package com.coral.learning.data.jpamy.service.impl;


import com.coral.learning.data.jpamy.dao.UserDao;
import com.coral.learning.data.jpamy.entity.User;
import com.coral.learning.data.jpamy.mapper.UserMapper;
import com.coral.learning.data.jpamy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service("userService")
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;


    @Resource
    private UserMapper userMapper;


    public User getUserById(int userId) {
        return userDao.findById(userId);
    }

    @Override
    public User getUserByUser(String user) {
        return userMapper.selectByUser(user);
    }

}