package com.coral.learning.data.jpamy.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.coral.learning.data.jpamy.mapper")
public class MybatisConfig {
}
