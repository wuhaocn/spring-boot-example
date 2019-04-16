package com.coral.learning.security.shiro.dao;

import com.coral.learning.security.shiro.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UserDao extends JpaRepository<User, String>, JpaSpecificationExecutor<User> {
    User findById(Integer id);
    User findByUserName(String userName);
}