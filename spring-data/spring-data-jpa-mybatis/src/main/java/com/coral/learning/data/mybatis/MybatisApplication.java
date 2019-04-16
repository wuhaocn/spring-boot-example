package com.coral.learning.data.jpa;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
@MapperScan("com.coral.learning.data.mybatis.dao")
public class MybatisApplication{
    public static void main(String[] args) {
        SpringApplication.run(MybatisApplication.class,args);
    }
}