
服务发现：Eureka客户端
服务发现是基于微服务架构的关键原则之一。尝试配置每个客户端或某种形式的约定可能非常困难，可以非常脆弱。Netflix服务发现服务器和客户端是Eureka。可以将服务器配置和部署为高可用性，每个服务器将注册服务的状态复制到其他服务器。

如何包含Eureka客户端
要在您的项目中包含Eureka客户端，请使用组org.springframework.cloud和工件ID spring-cloud-starter-eureka的启动器。有关 使用当前的Spring Cloud发布列表设置构建系统的详细信息，请参阅Spring Cloud项目页面。

注册Eureka
当客户端注册Eureka时，它提供关于自身的元数据，例如主机和端口，健康指示符URL，主页等。Eureka从属于服务的每个实例接收心跳消息。如果心跳失败超过可配置的时间表，则通常将该实例从注册表中删除。

示例eureka客户端：

@Configuration
@ComponentScan
@EnableAutoConfiguration
@EnableEurekaClient
@RestController
public class Application {

    @RequestMapping("/")
    public String home() {
        return "Hello world";
    }

    public static void main(String[] args) {
        new SpringApplicationBuilder(Application.class).web(true).run(args);
    }

}
（即完全正常的Spring Boot应用程序）。在这个例子中，我们明确地使用@EnableEurekaClient，但只有Eureka可用，你也可以使用@EnableDiscoveryClient。需要配置才能找到Eureka服务器。例：

application.yml
eureka:
  client:
    serviceUrl:
      defaultZone: http://localhost:8761/eureka/
其中“defaultZone”是一个魔术字符串后备值，为任何不表示首选项的客户端提供服务URL（即它是有用的默认值）。

从Environment获取的默认应用程序名称（服务ID），虚拟主机和非安全端口分别为${spring.application.name}，${spring.application.name}和${server.port}。

@EnableEurekaClient将应用程序同时进入一个Eureka“实例”（即注册自己）和一个“客户端”（即它可以查询注册表以查找其他服务）。实例行为由eureka.instance.*配置键驱动，但是如果您确保您的应用程序具有spring.application.name（这是Eureka服务ID或VIP的默认值），那么默认值将是正常的。

有关可配置选项的更多详细信息，请参阅EurekaInstanceConfigBean和EurekaClientConfigBean。

使用Eureka服务器进行身份验证
如果其中一个eureka.client.serviceUrl.defaultZone网址中包含一个凭据（如http://user:password@localhost:8761/eureka）），HTTP基本身份验证将自动添加到您的eureka客户端。对于更复杂的需求，您可以创建DiscoveryClientOptionalArgs类型的@Bean，并将ClientFilter实例注入到其中，所有这些都将应用于从客户端到服务器的调用。

注意
由于Eureka中的限制，不可能支持每个服务器的基本身份验证凭据，所以只能使用第一个找到的集合。
状态页和健康指标
Eureka实例的状态页面和运行状况指示器分别默认为“/ info”和“/ health”，它们是Spring Boot执行器应用程序中有用端点的默认位置。如果您使用非默认上下文路径或servlet路径（例如server.servletPath=/foo）或管理端点路径（例如management.contextPath=/admin），则需要更改这些，即使是执行器应用程序。例：

application.yml
eureka:
  instance:
    statusPageUrlPath: ${management.context-path}/info
    healthCheckUrlPath: ${management.context-path}/health
这些链接显示在客户端使用的元数据中，并在某些情况下用于决定是否将请求发送到应用程序，因此如果它们是准确的，这是有帮助的。

注册安全应用程序
如果您的应用程序想通过HTTPS联系，则可以分别在EurekaInstanceConfig，即 eureka.instance.[nonSecurePortEnabled,securePortEnabled]=[false,true] 中设置两个标志。这将使Eureka发布实例信息显示安全通信的明确偏好。Spring Cloud DiscoveryClient将始终为以这种方式配置的服务返回一个https://…​; URI，并且Eureka（本机）实例信息将具有安全的健康检查URL。

由于Eureka内部的工作方式，它仍然会发布状态和主页的非安全网址，除非您也明确地覆盖。您可以使用占位符来配置eureka实例URL，例如

application.yml
eureka:
  instance:
    statusPageUrl: https://${eureka.hostname}/info
    healthCheckUrl: https://${eureka.hostname}/health
    homePageUrl: https://${eureka.hostname}/
（请注意，${eureka.hostname}是仅在稍后版本的Eureka中可用的本地占位符，您也可以使用Spring占位符实现同样的功能，例如使用${eureka.instance.hostName}。

注意
如果您的应用程序在代理服务器后面运行，并且SSL终止服务在代理中（例如，如果您运行在Cloud Foundry或其他平台作为服务），则需要确保代理“转发”头部被截取并处理应用程序。Spring Boot应用程序中的嵌入式Tomcat容器会自动执行“X-Forwarded - \ *”标头的显式配置。你这个错误的一个迹象就是你的应用程序本身所呈现的链接是错误的（错误的主机，端口或协议）。
Eureka的健康检查
默认情况下，Eureka使用客户端心跳来确定客户端是否启动。除非另有规定，否则发现客户端将不会根据Spring Boot执行器传播应用程序的当前运行状况检查状态。这意味着成功注册后Eureka将永远宣布申请处于“UP”状态。通过启用Eureka运行状况检查可以改变此行为，从而将应用程序状态传播到Eureka。因此，每个其他应用程序将不会在“UP”之外的状态下将流量发送到应用程序。

application.yml
eureka:
  client:
    healthcheck:
      enabled: true
警告
eureka.client.healthcheck.enabled=true只能在application.yml中设置。设置bootstrap.yml中的值将导致不期望的副作用，例如在具有UNKNOWN状态的eureka中注册。
如果您需要更多的控制健康检查，您可以考虑实施自己的com.netflix.appinfo.HealthCheckHandler。

Eureka实例和客户端的元数据
值得花点时间了解Eureka元数据的工作原理，以便您可以在平台上使用它。有主机名，IP地址，端口号，状态页和运行状况检查等标准元数据。这些发布在服务注册表中，由客户使用，以直接的方式联系服务。额外的元数据可以添加到eureka.instance.metadataMap中的实例注册中，并且这将在远程客户端中可访问，但一般不会更改客户端的行为，除非意识到元数据的含义。下面描述了几个特殊情况，其中Spring Cloud已经为元数据映射指定了含义。

在Cloudfoundry上使用Eureka
Cloudfoundry有一个全局路由器，所以同一个应用程序的所有实例都具有相同的主机名（在具有相似架构的其他PaaS解决方案中也是如此）。这不一定是使用Eureka的障碍，但如果您使用路由器（建议，甚至是强制性的，具体取决于您的平台的设置方式），则需要明确设置主机名和端口号（安全或非安全），以便他们使用路由器。您可能还需要使用实例元数据，以便您可以区分客户端上的实例（例如，在自定义负载平衡器中）。默认情况下，eureka.instance.instanceId为vcap.application.instance_id。例如：

application.yml
eureka:
  instance:
    hostname: ${vcap.application.uris[0]}
    nonSecurePort: 80
根据Cloudfoundry实例中安全规则的设置方式，您可以注册并使用主机VM的IP地址进行直接的服务到服务调用。此功能尚未在Pivotal Web Services（PWS）上提供。

在AWS上使用Eureka
如果应用程序计划将部署到AWS云，那么Eureka实例必须被配置为AWS意识到，这可以通过定制来完成EurekaInstanceConfigBean方式如下：

@Bean
@Profile("!default")
public EurekaInstanceConfigBean eurekaInstanceConfig(InetUtils inetUtils) {
  EurekaInstanceConfigBean b = new EurekaInstanceConfigBean(inetUtils);
  AmazonInfo info = AmazonInfo.Builder.newBuilder().autoBuild("eureka");
  b.setDataCenterInfo(info);
  return b;
}
更改Eureka实例ID
香草Netflix Eureka实例注册了与其主机名相同的ID（即每个主机只有一个服务）。Spring Cloud Eureka提供了一个明智的默认，如下所示：${spring.cloud.client.hostname}:${spring.application.name}:${spring.application.instance_id:${server.port}}}。例如myhost:myappname:8080。

使用Spring Cloud，您可以通过在eureka.instance.instanceId中提供唯一的标识符来覆盖此。例如：

application.yml
eureka:
  instance:
    instanceId: ${spring.application.name}:${vcap.application.instance_id:${spring.application.instance_id:${random.value}}}
使用这个元数据和在localhost上部署的多个服务实例，随机值将在那里进行，以使实例是唯一的。在Cloudfoundry中，vcap.application.instance_id将在Spring Boot应用程序中自动填充，因此不需要随机值。

使用EurekaClient
一旦您拥有@EnableDiscoveryClient（或@EnableEurekaClient）的应用程序，您就可以使用它来从Eureka服务器发现服务实例。一种方法是使用本机com.netflix.discovery.EurekaClient（而不是Spring云DiscoveryClient），例如

@Autowired
private EurekaClient discoveryClient;

public String serviceUrl() {
    InstanceInfo instance = discoveryClient.getNextServerFromEureka("STORES", false);
    return instance.getHomePageUrl();
}
小费
不要使用@PostConstruct方法或@Scheduled方法（或ApplicationContext可能尚未启动的任何地方）EurekaClient。它被初始化为SmartLifecycle（带有phase=0），所以最早可以依靠它可用的是另一个具有更高阶段的SmartLifecycle。
本机Netflix EurekaClient的替代方案
您不必使用原始的Netflix EurekaClient，通常在某种包装器后面使用它更为方便。Spring Cloud支持Feign（REST客户端构建器），还支持Spring RestTemplate使用逻辑Eureka服务标识符（VIP）而不是物理URL。要使用固定的物理服务器列表配置Ribbon，您可以将<client>.ribbon.listOfServers设置为逗号分隔的物理地址（或主机名）列表，其中<client>是客户端的ID。

您还可以使用org.springframework.cloud.client.discovery.DiscoveryClient，它为Netflix不具体的发现客户端提供简单的API，例如

@Autowired
private DiscoveryClient discoveryClient;

public String serviceUrl() {
    List<ServiceInstance> list = discoveryClient.getInstances("STORES");
    if (list != null && list.size() > 0 ) {
        return list.get(0).getUri();
    }
    return null;
}
为什么注册服务这么慢？
作为一个实例也包括定期心跳到注册表（通过客户端的serviceUrl），默认持续时间为30秒。在实例，服务器和客户端在其本地缓存中都具有相同的元数据（因此可能需要3个心跳）之前，客户端才能发现服务。您可以使用eureka.instance.leaseRenewalIntervalInSeconds更改期限，这将加快客户端连接到其他服务的过程。在生产中，最好坚持使用默认值，因为服务器内部有一些计算可以对租赁更新期进行假设。

区
如果您已将Eureka客户端部署到多个区域，您可能希望这些客户端在使用另一个区域中的服务之前，利用同一区域内的服务。为此，您需要正确配置您的Eureka客户端。

首先，您需要确保将Eureka服务器部署到每个区域，并且它们是彼此的对等体。有关详细信息，请参阅区域和区域部分 。

接下来，您需要告知Eureka您的服务所在的区域。您可以使用metadataMap属性来执行此操作。例如，如果service 1部署到zone 1和zone 2，则需要在service 1中设置以下Eureka属性

1区服务1

eureka.instance.metadataMap.zone = zone1
eureka.client.preferSameZoneEureka = true
第2区的服务1

eureka.instance.metadataMap.zone = zone2
eureka.client.preferSameZoneEureka = true
服务发现：Eureka服务器
如何包含Eureka服务器
要在项目中包含Eureka服务器，请使用组org.springframework.cloud和工件id spring-cloud-starter-eureka-server的启动器。有关 使用当前的Spring Cloud发布列表设置构建系统的详细信息，请参阅Spring Cloud项目页面。

如何运行Eureka服务器
示例eureka服务器;

@SpringBootApplication
@EnableEurekaServer
public class Application {

    public static void main(String[] args) {
        new SpringApplicationBuilder(Application.class).web(true).run(args);
    }

}
服务器具有一个带有UI的主页，并且根据/eureka/*下的正常Eureka功能的HTTP API端点。

Eureka背景阅读：看助焊剂电容和谷歌小组讨论。

小费
由于Gradle的依赖关系解决规则和父母的bom功能缺乏，只要依靠spring-cloud-starter-eureka-server就可能导致应用程序启动失败。要解决这个问题，必须添加Spring Boot Gradle插件，并且必须导入Spring云启动器父母bom：

的build.gradle
buildscript {
  dependencies {
    classpath("org.springframework.boot:spring-boot-gradle-plugin:1.3.5.RELEASE")
  }
}

apply plugin: "spring-boot"

dependencyManagement {
  imports {
    mavenBom "org.springframework.cloud:spring-cloud-dependencies:Brixton.RELEASE"
  }
}
高可用性，区域和地区
Eureka服务器没有后端存储，但是注册表中的服务实例都必须发送心跳以保持其注册更新（因此可以在内存中完成）。客户端还具有eureka注册的内存缓存（因此，他们不必为注册表提供每个服务请求）。

默认情况下，每个Eureka服务器也是一个Eureka客户端，并且需要（至少一个）服务URL来定位对等体。如果您不提供该服务将运行和工作，但它将淋浴您的日志与大量的噪音无法注册对等体。

关于区域和区域的客户端Ribbon支持的详细信息，请参见下文。

独立模式
只要存在某种监视器或弹性运行时间（例如Cloud Foundry），两个高速缓存（客户机和服务器）和心跳的组合使独立的Eureka服务器对故障具有相当的弹性。在独立模式下，您可能更喜欢关闭客户端行为，因此不会继续尝试并且无法访问其对等体。例：

application.yml（Standalone Eureka Server）
server:
  port: 8761

eureka:
  instance:
    hostname: localhost
  client:
    registerWithEureka: false
    fetchRegistry: false
    serviceUrl:
      defaultZone: http://${eureka.instance.hostname}:${server.port}/eureka/
请注意，serviceUrl指向与本地实例相同的主机。

同行意识
通过运行多个实例并请求他们相互注册，可以使Eureka更具弹性和可用性。事实上，这是默认的行为，所以你需要做的只是为对方添加一个有效的serviceUrl，例如

application.yml（Two Peer Aware Eureka服务器）
---
spring:
  profiles: peer1
eureka:
  instance:
    hostname: peer1
  client:
    serviceUrl:
      defaultZone: http://peer2/eureka/

---
spring:
  profiles: peer2
eureka:
  instance:
    hostname: peer2
  client:
    serviceUrl:
      defaultZone: http://peer1/eureka/
在这个例子中，我们有一个YAML文件，可以通过在不同的Spring配置文件中运行，在2台主机（peer1和peer2）上运行相同的服务器。您可以使用此配置来测试单个主机上的对等体感知（通过操作/etc/hosts来解析主机名，在生产中没有太多价值）。事实上，如果您在一台知道自己的主机名的机器上运行（默认情况下使用java.net.InetAddress查找），则不需要eureka.instance.hostname。

您可以向系统添加多个对等体，只要它们至少一个边缘彼此连接，则它们将在它们之间同步注册。如果对等体在物理上分离（在数据中心内或多个数据中心之间），则系统原则上可以分裂脑型故障。

喜欢IP地址
在某些情况下，Eureka优先发布服务的IP地址而不是主机名。将eureka.instance.preferIpAddress设置为true，并且当应用程序向eureka注册时，它将使用其IP地址而不是其主机名。
