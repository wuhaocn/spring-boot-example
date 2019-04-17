package com.coral.learning.security.starter.service;


import com.coral.learning.security.starter.entity.User;

public interface UserService {
    User getUserById(int userId);
    User getUserByUser(String user);

}