package com.coral.learning.data.jpamy.mapper;


import com.coral.learning.data.jpamy.entity.User;

public interface UserMapper {

    User selectByUser(String user);
}