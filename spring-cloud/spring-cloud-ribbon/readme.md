    一：Ribbon是什么？


 Ribbon是Netflix发布的开源项目，主要功能是提供客户端的软件负载均衡算法，将Netflix的中间层服务连接在一起。Ribbon客户端组件提供一系列完善的配置项如连接超时，重试等。简单的说，就是在配置文件中列出Load Balancer（简称LB）后面所有的机器，Ribbon会自动的帮助你基于某种规则（如简单轮询，随即连接等）去连接这些机器。我们也很容易使用Ribbon实现自定义的负载均衡算法。



   二:LB方案分类


目前主流的LB方案可分成两类：一种是集中式LB, 即在服务的消费方和提供方之间使用独立的LB设施(可以是硬件，如F5, 也可以是软件，如nginx), 由该设施负责把访问请求通过某种策略转发至服务的提供方；另一种是进程内LB，将LB逻辑集成到消费方，消费方从服务注册中心获知有哪些地址可用，然后自己再从这些地址中选择出一个合适的服务器。Ribbon就属于后者，它只是一个类库，集成于消费方进程，消费方通过它来获取到服务提供方的地址。





   三：Ribbon的主要组件与工作流程


     Ribbon的核心组件(均为接口类型)有以下几个：

	ServerList 

	用于获取地址列表。它既可以是静态的(提供一组固定的地址)，也可以是动态的(从注册中心中定期查询地址列表)。



	ServerListFilter 

	仅当使用动态ServerList时使用，用于在原始的服务列表中使用一定策略过虑掉一部分地址。



	IRule 

	选择一个最终的服务地址作为LB结果。选择策略有轮询、根据响应时间加权、断路器(当Hystrix可用时)等。



	Ribbon在工作时首选会通过ServerList来获取所有可用的服务列表，然后通过ServerListFilter过虑掉一部分地址，最后在剩下的地址中通过IRule选择出一台服务器作为最终结果。





   四：Ribbon提供的主要负载均衡策略介绍


   1:简单轮询负载均衡（RoundRobin）

     以轮询的方式依次将请求调度不同的服务器，即每次调度执行i = (i + 1) mod n，并选出第i台服务器。



   2:随机负载均衡 （Random）

     随机选择状态为UP的Server



   3:加权响应时间负载均衡 （WeightedResponseTime）

     根据相应时间分配一个weight，相应时间越长，weight越小，被选中的可能性越低。



   4:区域感知轮询负载均衡（ZoneAvoidanceRule）

     复合判断server所在区域的性能和server的可用性选择server



  Ribbon自带负载均衡策略比较

策略名	策略声明	策略描述	实现说明
BestAvailableRule	public class BestAvailableRule extends ClientConfigEnabledRoundRobinRule	选择一个最小的并发请求的server	逐个考察Server，如果Server被tripped了，则忽略，在选择其中ActiveRequestsCount最小的server
AvailabilityFilteringRule	public class AvailabilityFilteringRule extends PredicateBasedRule	过滤掉那些因为一直连接失败的被标记为circuit tripped的后端server，并过滤掉那些高并发的的后端server（active connections 超过配置的阈值）	使用一个AvailabilityPredicate来包含过滤server的逻辑，其实就就是检查status里记录的各个server的运行状态
WeightedResponseTimeRule	public class WeightedResponseTimeRule extends RoundRobinRule	根据相应时间分配一个weight，相应时间越长，weight越小，被选中的可能性越低。	一 个后台线程定期的从status里面读取评价响应时间，为每个server计算一个weight。Weight的计算也比较简单responsetime 减去每个server自己平均的responsetime是server的权重。当刚开始运行，没有形成statas时，使用roubine策略选择 server。
RetryRule	public class RetryRule extends AbstractLoadBalancerRule	对选定的负载均衡策略机上重试机制。	在一个配置时间段内当选择server不成功，则一直尝试使用subRule的方式选择一个可用的server
RoundRobinRule	public class RoundRobinRule extends AbstractLoadBalancerRule	roundRobin方式轮询选择server	轮询index，选择index对应位置的server
RandomRule	public class RandomRule extends AbstractLoadBalancerRule	随机选择一个server	在index上随机，选择index对应位置的server
ZoneAvoidanceRule	public class ZoneAvoidanceRule extends PredicateBasedRule	复合判断server所在区域的性能和server的可用性选择server	使 用ZoneAvoidancePredicate和AvailabilityPredicate来判断是否选择某个server，前一个判断判定一个 zone的运行性能是否可用，剔除不可用的zone（的所有server），AvailabilityPredicate用于过滤掉连接数过多的 Server。



   五：Ribbon单独使用


    创建一个maven工程 名称 ribbon_client

     pom内容

        <dependencies>
		<dependency>
			<groupId>com.netflix.ribbon</groupId>
			<artifactId>ribbon-core</artifactId>
			<version>2.2.0</version>
		</dependency>
		<dependency>
			<groupId>com.netflix.ribbon</groupId>
			<artifactId>ribbon-httpclient</artifactId>
			<version>2.2.0</version>
		</dependency>
	</dependencies>


sample-client.properties配置文件



# Max number of retries   
sample-client.ribbon.MaxAutoRetries=1
  
# Max number of next servers to retry (excluding the first server)  
sample-client.ribbon.MaxAutoRetriesNextServer=1
  
# Whether all operations can be retried for this client  
sample-client.ribbon.OkToRetryOnAllOperations=true
  
# Interval to refresh the server list from the source  
sample-client.ribbon.ServerListRefreshInterval=2000
  
# Connect timeout used by Apache HttpClient  
sample-client.ribbon.ConnectTimeout=3000
  
# Read timeout used by Apache HttpClient  
sample-client.ribbon.ReadTimeout=3000
  
# Initial list of servers, can be changed via Archaius dynamic property at runtime  
sample-client.ribbon.listOfServers=www.sohu.com:80,www.163.com:80,www.sina.com.cn:80
  
sample-client.ribbon.EnablePrimeConnections=true


RibbonMain代码

import java.net.URI;
import com.netflix.client.ClientFactory;
import com.netflix.client.http.HttpRequest;
import com.netflix.client.http.HttpResponse;
import com.netflix.config.ConfigurationManager;
import com.netflix.loadbalancer.ZoneAwareLoadBalancer;
import com.netflix.niws.client.http.RestClient;

public class RibbonMain {
    public static void main( String[] args ) throws Exception {  
        ConfigurationManager.loadPropertiesFromResources("sample-client.properties");  
        System.out.println(ConfigurationManager.getConfigInstance().getProperty("sample-client.ribbon.listOfServers"));  
          
        RestClient client = (RestClient)ClientFactory.getNamedClient("sample-client");  
        HttpRequest request = HttpRequest.newBuilder().uri(new URI("/")).build();  
          
        for(int i = 0; i < 4; i ++) {  
            HttpResponse response = client.executeWithLoadBalancer(request);  
            System.out.println("Status for URI:" + response.getRequestedURI() + " is :" + response.getStatus());  
        }  
          
        ZoneAwareLoadBalancer lb = (ZoneAwareLoadBalancer) client.getLoadBalancer();  
        System.out.println(lb.getLoadBalancerStats());  
          
        ConfigurationManager.getConfigInstance().setProperty("sample-client.ribbon.listOfServers", "ccblog.cn:80,www.linkedin.com:80");  
          
        System.out.println("changing servers ...");  
        Thread.sleep(3000);  
          
        for(int i = 0; i < 3; i ++) {  
            HttpResponse response = client.executeWithLoadBalancer(request);  
            System.out.println("Status for URI:" + response.getRequestedURI() + " is :" + response.getStatus());  
        }  
        System.out.println(lb.getLoadBalancerStats());  
    }  
}




代码解析

使用 Archaius ConfigurationManager 加载属性；

使用 ClientFactory 创建客户端和负载均衡器；

使用 builder 构建 http 请求。注意我们只支持 URI 的 "/" 部分的路径，一旦服务器被负载均衡器选中，会由客户端计算出完整的 URI；

调用 API client.executeWithLoadBalancer()，不是 exeucte() API；

动态修正配置中的服务器池；

等待服务器列表刷新(配置文件中定义的刷新间隔是为 3 秒钟)；

打印出负载均衡器记录的服务器统计信息。





   六：Ribbon结合eureka使用


   先要启动eureka_register_service工程（注册中心）和biz-service-0工程（服务生产者）

    

   创建maven工程 eureka_ribbon_client 该工程启动和相关配置依赖eureka_register_service和biz-service-0



    pom加入



<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>1.4.3.RELEASE</version>
    <relativePath/> <!-- lookup parent from repository -->
</parent>
<dependencies>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-ribbon</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-eureka</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
<dependencyManagement>
    <dependencies>
        <dependency>
	    <groupId>org.springframework.cloud</groupId>
	    <artifactId>spring-cloud-dependencies</artifactId>
	    <version>Brixton.RELEASE</version>
	    <type>pom</type>
	    <scope>import</scope>
	</dependency>
    </dependencies>
</dependencyManagement>


在应用主类中，通过@EnableDiscoveryClient注解来添加发现服务能力。创建RestTemplate实例，并通过@LoadBalanced注解开启均衡负载能力。

@SpringBootApplication
@EnableDiscoveryClient
public class RibbonApplication {
	@Bean
	@LoadBalanced
	RestTemplate restTemplate() {
		return new RestTemplate();
	}
	public static void main(String[] args) {
		SpringApplication.run(RibbonApplication.class, args);
	}
}


创建ConsumerController来消费biz-service-0的getuser服务。通过直接RestTemplate来调用服务



@RestController
public class ConsumerController {

    @Autowired
    RestTemplate restTemplate;

    @RequestMapping(value = "/getuserinfo", method = RequestMethod.GET)
    public String add() {
        return restTemplate.getForEntity("http://biz-service-0/getuser", String.class).getBody();
    }
}
application.properties中配置eureka服务注册中心



spring.application.name=ribbon-consumer

server.port=8003

eureka.client.serviceUrl.defaultZone=http://localhost:8000/eureka/



完成后可以打开http://localhost:8003/getuserinfo 可以看到结果



总结：Ribbon其实就是一个软负载均衡的客户端组件，他可以和其他所需请求的客户端结合使用，和eureka结合只是其中的一个实例。