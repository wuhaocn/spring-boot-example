package com.coral.learning.security.shiro.service;


import com.coral.learning.security.shiro.entity.User;

public interface UserService {
    User getUserById(int userId);
    User getUserByUser(String user);

}