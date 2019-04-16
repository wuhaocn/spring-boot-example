package com.coral.learning.data.jpa.service;

import com.coral.learning.data.jpa.entity.User;

public interface UserService {
    public User getUserById(int userId);

    boolean addUser(User record);

}