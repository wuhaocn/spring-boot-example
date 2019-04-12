package com.sf.spring.cloud.hystrix.controller;

import com.sf.spring.cloud.hystrix.service.HystrixService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 接口
 */
@RestController
@RequestMapping("/SfHystrix")
public class HystrixController {

    @Autowired
    private HystrixService service;
    /**
     * 调用依赖的服务
     */
    @RequestMapping("/doWork")
    public String doWork(){
        return service.doWork();
    }
}