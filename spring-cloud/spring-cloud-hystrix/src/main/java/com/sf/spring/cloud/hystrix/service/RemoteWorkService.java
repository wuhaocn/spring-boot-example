package com.sf.spring.cloud.hystrix.service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.stereotype.Component;

import java.util.Random;

/**
 * 调用依赖服务，通过hystrix包装调用服务
 */
@Component
public class RemoteWorkService {

    private Random random = new Random();

    /**
     * 模拟获取用户信息(通过网络调用)
     *
     * @return
     */
    @HystrixCommand(fallbackMethod = "getUserInfoCallBack")
    public String getUserInfo() {
        int randomInt = random.nextInt(10);
        if (randomInt < 5) {  //模拟调用失败情况
            throw new RuntimeException("call RemoteWorkService fail.");
        } else {
            return "UserInfo:" + randomInt;
        }
    }

    public String getUserInfoCallBack() {
        return "some exception occur call fallback method.";
    }
}