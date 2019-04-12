#### spring-cloud
spring-cloud:

spring-cloud-starter
> spring-cloud启动类

spring-cloud-eureka
> 云端服务发现，一个基于 REST 的服务，用于定位服务，以实现云端中间层服务发现和故障转移。

spring-cloud-hystrix
> 断路器：Hystrix客户端
  Netflix的创造了一个调用的库Hystrix实现了断路器图案。在微服务架构中，通常有多层服务调用
  
spring-cloud-ribbon
> ribbon负载均衡器
Ribbon是Netflix发布的开源项目，主要功能是提供客户端的软件负载均衡算法，将Netflix的中间层服务连接在一起。Ribbon客户端组件提供一系列完善的配置项如连接超时，重试等。简单的说，就是在配置文件中列出Load Balancer（简称LB）后面所有的机器，Ribbon会自动的帮助你基于某种规则（如简单轮询，随即连接等）去连接这些机器。我们也很容易使用Ribbon实现自定义的负载均衡算法。

spring-cloud-sleuth
> 链路追踪，用于服务内追踪

spring-cloud-config
> 云配置服务

spring-cloud-hystrix
>   
spring-cloud-ribbon
> 

#### spring-data-jpa