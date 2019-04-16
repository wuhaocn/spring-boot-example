package com.coral.learning.security.shiro.service.impl;


import com.coral.learning.security.shiro.dao.UserDao;
import com.coral.learning.security.shiro.entity.User;
import com.coral.learning.security.shiro.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("userService")
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;


    public User getUserById(int userId) {
        return userDao.findById(userId);
    }

    @Override
    public User getUserByUser(String user) {
        return userDao.findByUserName(user);
    }


}