package com.coral.learning.data.jpamy.dao;

import com.coral.learning.data.jpamy.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UserDao extends JpaRepository<User, String>, JpaSpecificationExecutor<User> {
    User findById(Integer id);
}