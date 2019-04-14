package com.coral.learning.dubbo.provider;

import com.alibaba.dubbo.spring.boot.annotation.EnableDubboConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableDubboConfiguration
@SpringBootApplication
public class DubboProviderApplication {
    public static void main(String[] args) {
        //starter
        SpringApplication.run(DubboProviderApplication.class, args);
    }
}
