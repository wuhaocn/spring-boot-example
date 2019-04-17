package com.coral.learning.security.starter.service.impl;


import com.coral.learning.security.starter.dao.UserDao;
import com.coral.learning.security.starter.entity.User;
import com.coral.learning.security.starter.service.UserService;
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
        return userDao.findByUsername(user);
    }


}