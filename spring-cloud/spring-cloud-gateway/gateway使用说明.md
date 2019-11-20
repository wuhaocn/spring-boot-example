#### spring-cloud-gateway使用说明

github示例:[spring-cloud-gateway示例](https://github.com/coral-learning/spring-boot-example/tree/master/spring-cloud/spring-cloud-gateway)

##### spring-cloud-gateway介绍 

    Spring Cloud Gateway 是Spring Cloud团队的一个全新项目，基于Spring 5.0、SpringBoot2.0、Project Reactor 等技术开发的网关。 旨在为微服务架构提供一种简单有效统一的API路由管理方式。
    
    Spring Cloud Gateway 作为SpringCloud生态系统中的网关，目标是替代Netflix Zuul。Gateway不仅提供统一路由方式，并且基于Filter链的方式提供网关的基本功能。例如：安全，监控/指标，和限流。
    
    本身也是一个微服务，需要注册到Eureka
    
    网关的核心功能：过滤、路由
    
    核心概念：通过画图解释
    
    路由(route)：
    断言Predicate函数：路由转发规则
    过滤器(Filter)：
    实现步骤：
    创建gateway_service工程SpringBoot
    编写基础配置
    编写路由规则，配置静态路由策略
    启动网关服务进行测试
  
    
     
##### 工程搭建
* 1.依赖

```java
    dependencies {
        compile (group: 'org.springframework.cloud', name: 'spring-cloud-starter-gateway', version: '2.1.3.RELEASE')
    
        testCompile group: 'junit', name: 'junit', version: '4.11'
    }
```
* 2.配置
```java
    application.yml
    server:
      port: 10010
    spring:
      application:
        name: api-gateway # 应用名
      cloud:
        gateway:
          # 路由si(集合)
          routes:
            # id唯一标识，(可自定义)
            - id: user-service-route
              # 路由服务地址
              uri: http://127.0.0.1:8092
              # 路由拦截地址(断言)
              predicates:
                - Path=/test/**
```
* 3.测试
    访问
    http://127.0.0.1:10010/test进行测试



##### 参考：
原文链接：https://blog.csdn.net/sinat_39179993/article/details/94358624