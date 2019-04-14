#### 一、简介

Add sleuth to the classpath of a Spring Boot application (see below for Maven and Gradle examples), and you will see the correlation data being collected in logs, as long as you are logging requests.
Spring Cloud Sleuth 主要功能就是在分布式系统中提供追踪解决方案，并且兼容支持了 zipkin，你只需要在pom文件中引入相应的依赖即可。

####  二、服务追踪分析

微服务架构上通过业务来划分服务的，通过REST调用。
对外暴露的一个接口，可能需要很多个服务协同才能完成这个接口功能
如果链路上任何一个服务出现问题或者网络超时，都会形成导致接口调用失败。
随着业务的不断扩张，服务之间互相调用会越来越复杂。

####  三、术语

Span：基本工作单元，例如，在一个新建的span中发送一个RPC等同于发送一个回应请求给RPC，span通过一个64位ID唯一标识，trace以另一个64位ID表示，span还有其他数据信息，比如摘要、时间戳事件、关键值注释(tags)、span的ID、以及进度ID(通常是IP地址) 
span在不断的启动和停止，同时记录了时间信息，当你创建了一个span，你必须在未来的某个时刻停止它。

Trace：一系列spans组成的一个树状结构，例如，如果你正在跑一个分布式大数据工程，你可能需要创建一个trace。

Annotation：用来及时记录一个事件的存在，一些核心annotations用来定义一个请求的开始和结束 
cs - Client Sent -客户端发起一个请求，这个annotion描述了这个span的开始
sr - Server Received -服务端获得请求并准备开始处理它，如果将其sr减去cs时间戳便可得到网络延迟
ss - Server Sent -注解表明请求处理的完成(当请求返回客户端)，如果ss减去sr时间戳便可得到服务端需要的处理请求时间
cr - Client Received -表明span的结束，客户端成功接收到服务端的回复，如果cr减去cs时间戳便可得到客户端从服务端获取回复的所有所需时间 
将Span和Trace在一个系统中使用Zipkin注解的过程图形化：
![image](http://upload-images.jianshu.io/upload_images/2279594-4b865f2a2c271def.png)


#### 四、构建工程

基本知识讲解完毕，下面我们来实战，本文的案例主要有三个工程组成:一个server-zipkin,它的主要作用使用ZipkinServer 的功能，收集调用数据，并展示；一个service-hi,对外暴露hi接口；一个service-miya,对外暴露miya接口；这两个service可以相互调用；并且只有调用了，server-zipkin才会收集数据的，这就是为什么叫服务追踪了。

4.1 构建spring-cloud-sleuth
引入gradle依赖:

```java

dependencies {
    // https://mvnrepository.com/artifact/io.zipkin.java/zipkin-server
    compile group: 'io.zipkin.java', name: 'zipkin-server', version: '2.0.1'
    // https://mvnrepository.com/artifact/io.zipkin.java/zipkin-autoconfigure-ui
    compile group: 'io.zipkin.java', name: 'zipkin-autoconfigure-ui', version: '2.0.1'
    compile group: 'org.springframework.cloud', name: 'spring-cloud-starter-eureka', version: "${springBootVersion}"
    compile group: 'org.springframework.cloud', name: 'spring-cloud-starter-eureka-server', version: "${springBootVersion}"
    compile('org.springframework.boot:spring-boot-starter')
    compile('org.springframework.boot:spring-boot-starter-web')
}
```

创建启动类：ZipKinServerApplication
在其程序入口类, 加上注解@EnableZipkinServer，开启ZipkinServer的功能：
```java
@SpringBootApplication
@EnableZipkinServer
public class ZipKinServerApplication {
    public static void main(String[] args) throws IOException {
        Properties configProperties = new Properties();
        InputStream config = ZipKinServerApplication.class.getClassLoader().getResourceAsStream("application-zipkin-server.properties");
        configProperties.load(config);
        SpringApplication springApplication = new SpringApplication(ZipKinServerApplication.class);
        springApplication.setDefaultProperties(configProperties);
        springApplication.run(args);
    }
}
```
gradle

在配置文件application-zipkin-server.properties指定服务端口为：

```java

server.port: 8190

```
4.2 创建spring-cloud-sleuth-service服务

引入起步依赖spring-cloud-starter-zipkin，代码如下：
引入gradle依赖:

```java
dependencies {
    // http://mvnrepository.com/artifact/io.zipkin.java
    // https://mvnrepository.com/artifact/org.springframework.cloud/spring-cloud-starter-zipkin
    compile group: 'org.springframework.cloud', name: 'spring-cloud-starter-zipkin', version: '2.0.0.M1'
    compile('org.springframework.boot:spring-boot-starter')
    compile('org.springframework.boot:spring-boot-starter-web')
    testCompile group: 'junit', name: 'junit', version: '4.11'
}
```
在其配置文件application-zipkin-service.properties指定zipkin server的地址，头通过配置“spring.zipkin.base-url”指定：

```java
server.port: 8191
spring.zipkin.base-url=http://localhost:8190
spring.application.name=spring-cloud-sleuth-service
```java


代码示例：

```java

@SpringBootApplication
@RestController
public class ZipKinServiceApplication {
    @Autowired
    private RestTemplate restTemplate;

    @Bean
    public RestTemplate getRestTemplate(){
        return new RestTemplate();
    }

    @RequestMapping("/send")
    public String send(){
        return restTemplate.getForObject("http://localhost:8192/receiver", String.class);
    }
    @RequestMapping("/receiver")
    public String receiver(){
        return "All Is End";

    }

    @Bean
    public AlwaysSampler defaultSampler(){
        return new AlwaysSampler();
    }

    public static void main(String[] args) throws IOException {
        Properties configProperties = new Properties();
        InputStream config = ZipKinServiceApplication.class.getClassLoader().getResourceAsStream("application-zipkin-service.properties");
        configProperties.load(config);
        SpringApplication springApplication = new SpringApplication(ZipKinServiceApplication.class);
        springApplication.setDefaultProperties(configProperties);
        springApplication.run(args);
    }
}
```

4.3 创建spring-cloud-sleuth-invoking

引入起步依赖spring-cloud-starter-zipkin，代码如下：
引入gradle依赖:

```java
dependencies {
    // http://mvnrepository.com/artifact/io.zipkin.java
    // https://mvnrepository.com/artifact/org.springframework.cloud/spring-cloud-starter-zipkin
    compile group: 'org.springframework.cloud', name: 'spring-cloud-starter-zipkin', version: '2.0.0.M1'
    compile('org.springframework.boot:spring-boot-starter')
    compile('org.springframework.boot:spring-boot-starter-web')
    testCompile group: 'junit', name: 'junit', version: '4.11'
}
```
在其配置文件application-zipkin-invoking.properties指定zipkin server的地址，头通过配置“spring.zipkin.base-url”指定：

```java
server.port: 8192
spring.zipkin.base-url=http://localhost:8190
spring.application.name=spring-cloud-sleuth-invoking
```

代码示例：

```java
@SpringBootApplication
@RestController
public class ZipKinInvokingApplication {
    @Autowired
    private RestTemplate restTemplate;

    @Bean
    public RestTemplate getRestTemplate(){
        return new RestTemplate();
    }

    @RequestMapping("/send")
    public String send(){
        return restTemplate.getForObject("http://localhost:8191/send", String.class);
    }
    @RequestMapping("/receiver")
    public String receiver(){
        return restTemplate.getForObject("http://localhost:8191/receiver", String.class);

    }

    @Bean
    public AlwaysSampler defaultSampler(){
        return new AlwaysSampler();
    }

    public static void main(String[] args) throws IOException {
        Properties configProperties = new Properties();
        InputStream config = ZipKinInvokingApplication.class.getClassLoader().getResourceAsStream("application-zipkin-invoking.properties");
        configProperties.load(config);
        SpringApplication springApplication = new SpringApplication(ZipKinInvokingApplication.class);
        springApplication.setDefaultProperties(configProperties);
        springApplication.run(args);
    }
}
```

4.4 启动工程，演示追踪

启动上面的三个工程：
访问:http://localhost:8192/send系统内部自动调用。

打开浏览器访问：http://localhost:8190：
如下界面：
![image](http://img.blog.csdn.net/20170930144755890?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvY253dWhhbw==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

详细界面：
![image](http://img.blog.csdn.net/20170930145529320?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvY253dWhhbw==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

