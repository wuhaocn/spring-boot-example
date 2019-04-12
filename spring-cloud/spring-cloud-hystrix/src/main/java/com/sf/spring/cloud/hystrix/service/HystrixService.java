package com.sf.spring.cloud.hystrix.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 依赖服务
 */
@Service
public class HystrixService {

    @Autowired
    private RemoteWorkService remoteWorkService;
    public String doWork() {
        return remoteWorkService.getUserInfo();
    }
}