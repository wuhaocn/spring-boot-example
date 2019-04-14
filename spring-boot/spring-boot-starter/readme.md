#### 1.启动类配置
##### 1.1 gradle.properties

```config

junit_version = 4.11
zipkin_version = 2.0.1
netty_version=4.1.32.Final
redis_version = 2.9.1
swagger_version = 2.7.0
springboot_version = 2.1.1.RELEASE

```
##### 1.2 gradle赖:
```gradle

dependencies {
    compile group: 'org.springframework.boot', name: 'spring-boot-starter-web', version: springboot_version
    compile group: 'org.springframework.boot', name: 'spring-boot-starter-aop', version: springboot_version
    compile group: 'org.springframework.boot', name: 'spring-boot-starter-actuator', version: springboot_version
    
    testCompile group: 'org.springframework.boot', name: 'spring-boot-starter-test', version: springboot_version
    
}


```
#### 2.Java实现
    
    业务类：
        @RestController
        @EnableAutoConfiguration
        @SpringBootApplication
        public class StarterApplication {
            @RequestMapping("/test")
            public String doIndex() {
                return "test";
            }
            public static void main(String[] args) {
                SpringApplication.run(StarterApplication.class, args);
                
            }
        }



#### 3.spring boot注解
    > @Configuration 等同于spring的XML配置文件；使用Java代码可以检查类型安全。
    > @EnableAutoConfiguration 自动配置。
    > @ComponentScan 组件扫描，可自动发现和装配一些Bean。
    > @Component可配合CommandLineRunner使用，在程序启动后执行一些基础任务。
    > @RestController注解是@Controller和@ResponseBody的合集,表示这是个控制器bean,并且是将函数的返回值直 接填入HTTP响应体中,是REST风格的控制器。
    > @Autowired自动导入。
    > @PathVariable获取参数。
    > @JsonBackReference解决嵌套外链问题。
    > @RepositoryRestResourcepublic配合spring-boot-starter-data-rest使用。
    > @Conditional(TestCondition.class) 这句代码可以标注在类上面，表示该类下面的所有@Bean都会启用配置，也可以标注在方法上面，只是对该方法启用配置。
    > @ConditionalOnBean（仅仅在当前上下文中存在某个对象时，才会实例化一个Bean）
    > @ConditionalOnClass（某个class位于类路径上，才会实例化一个Bean）
    > @ConditionalOnExpression（当表达式为true的时候，才会实例化一个Bean）
    > @ConditionalOnMissingBean（仅仅在当前上下文中不存在某个对象时，才会实例化一个Bean）
    > @ConditionalOnMissingClass（某个class类路径上不存在的时候，才会实例化一个Bean）
    > @ConditionalOnNotWebApplication（不是web应用）
    > @ConditionalOnClass：该注解的参数对应的类必须存在，否则不解析该注解修饰的配置类；
    > @ConditionalOnMissingBean：该注解表示，如果存在它修饰的类的bean，则不需要再创建这个bean；可以给该注解传入参数例如@ConditionOnMissingBean(name = "example")，这个表示如果name为“example”的bean存在，这该注解修饰的代码块不执行。



#### 4.Spring 启动分析：
    1.创建了应用的监听器SpringApplicationRunListeners并开始监听
    
    2.加载SpringBoot配置环境(ConfigurableEnvironment)，如果是通过web容器发布，会加载StandardEnvironment，其最终也是继承了ConfigurableEnvironment，
       *Environment最终都实现了PropertyResolver接口，我们平时通过environment对象获取配置文件中指定Key对应的value方法时，就是调用了propertyResolver接口的getProperty方法
    
    3.配置环境(Environment)加入到监听器对象中(SpringApplicationRunListeners)
    
    4.创建run方法的返回对象：ConfigurableApplicationContext(应用配置上下文)，我们可以看一下创建方法：
    方法会先获取显式设置的应用上下文(applicationContextClass)，如果不存在，再加载默认的环境配置（通过是否是web environment判断），默认选择AnnotationConfigApplicationContext注解上下文（通过扫描所有注解类来加载bean），最后通过BeanUtils实例化上下文对象，并返回，ConfigurableApplicationContext类图如下：
    主要看其继承的两个方向：
    LifeCycle：生命周期类，定义了start启动、stop结束、isRunning是否运行中等生命周期空值方法
    ApplicationContext：应用上下文类，其主要继承了beanFactory(bean的工厂类)
    5.回到run方法内，prepareContext方法将listeners、environment、applicationArguments、banner等重要组件与上下文对象关联
    
    6.接下来的refreshContext(context)方法(初始化方法如下)将是实现spring-boot-starter-*(mybatis、redis等)自动化配置的关键，包括spring.factories的加载，bean的实例化等核心工作。
    refresh方法
    配置结束后，Springboot做了一些基本的收尾工作，返回了应用环境上下文。回顾整体流程，Springboot的启动，主要创建了配置环境(environment)、事件监听(listeners)、应用上下文(applicationContext)，并基于以上条件，在容器中开始实例化我们需要的Bean，至此，通过SpringBoot启动的程序已经构造完成，接下来我们来探讨自动化配置是如何实现。
    自动化配置：
    
    7. SpringBoot自动配置模块
    该配置模块的主要使用到了SpringFactoriesLoader，即Spring工厂加载器，该对象提供了loadFactoryNames方法，入参为factoryClass和classLoader，即需要传入上图中的工厂类名称和对应的类加载器，方法会根据指定的classLoader，加载该类加器搜索路径下的指定文件，即spring.factories文件，传入的工厂类为接口，而文件中对应的类则是接口的实现类，或最终作为实现类，所以文件中一般为如下图这种一对多的类名集合，获取到这些实现类的类名后，loadFactoryNames方法返回类名集合，方法调用方得到这些集合后，再通过反射获取这些类的类对象、构造方法，最终生成实例
    工厂接口与其若干实现类接口名称
    下图有助于我们形象理解自动配置流程
    SpringBoot自动化配置关键组件关系图
    mybatis-spring-boot-starter、spring-boot-starter-web等组件的META-INF文件下均含有spring.factories文件，自动配置模块中，SpringFactoriesLoader收集到文件中的类全名并返回一个类全名的数组，返回的类全名通过反射被实例化，就形成了具体的工厂实例，工厂实例来生成组件具体需要的bean。
    之前我们提到了EnableAutoConfiguration注解，其类图如下
    可以发现其最终实现了ImportSelector(选择器)和BeanClassLoaderAware(bean类加载器中间件)，重点关注一下AutoConfigurationImportSelector的selectImports方法
    该方法在springboot启动流程——bean实例化前被执行，返回要实例化的类信息列表。我们知道，如果获取到类信息，spring自然可以通过类加载器将类加载到jvm中，现在我们已经通过spring-boot的starter依赖方式依赖了我们需要的组件，那么这些组建的类信息在select方法中也是可以被获取到的，不要急我们继续向下分析
    该方法中的getCandidateConfigurations方法，通过方法注释了解到，其返回一个自动配置类的类名列表，方法调用了loadFactoryNames方法，查看该方法
    在上面的代码可以看到自动配置器会跟根据传入的factoryClass.getName到项目系统路径下所有的spring.factories文件中找到相应的key，从而加载里面的类。我们就选取这个mybatis-spring-boot-autoconfigure下的spring.factories文件
    进入org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration中，主要看一下类头
    发现@Spring的Configuration，俨然是一个通过注解标注的springBean，继续向下看，
    @ConditionalOnClass({ SqlSessionFactory.class, SqlSessionFactoryBean.class})这个注解的意思是：当存在SqlSessionFactory.class, SqlSessionFactoryBean.class这两个类时才解析MybatisAutoConfiguration配置类,否则不解析这一个配置类，make sence，我们需要mybatis为我们返回会话对象，就必须有会话工厂相关类
    @CondtionalOnBean(DataSource.class):只有处理已经被声明为bean的dataSource
    @ConditionalOnMissingBean(MapperFactoryBean.class)这个注解的意思是如果容器中不存在name指定的bean则创建bean注入
    
