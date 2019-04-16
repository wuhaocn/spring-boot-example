package com.coral.learning.data.mybatis.service;

import com.coral.learning.data.mybatis.entity.User;

public interface UserService {
    public User getUserById(int userId);

    boolean addUser(User record);

}