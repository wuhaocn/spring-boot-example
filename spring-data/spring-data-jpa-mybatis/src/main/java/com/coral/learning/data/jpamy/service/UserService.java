package com.coral.learning.data.jpamy.service;


import com.coral.learning.data.jpamy.entity.User;

public interface UserService {
    User getUserById(int userId);
    User getUserByUser(String user);

}