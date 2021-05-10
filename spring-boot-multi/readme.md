### 1.需求
* 1.单进程启动多个spring实例
    * 占用不同端口
    * 加载不同配置文件
    * 类实例对象隔离
#### 1.1 概述
  * spring-boot-web-s1
    * 监听8081，访问/test /test1 
  * spring-boot-web-s2  
    * 监听8082，访问/test /test2
  * spring-boot-web-boot
    * 监听8081，访问/test /test1 不能访问test2
    * 监听8082，访问/test /test2 不能访问test1
#### 1.2 解决方法
* spring-boot-web-s1

```
@SpringBootApplication
@ComponentScan("com.coral.spring.boot.starter.s1")
public class StarterS1Application {
}

```
* spring-boot-web-s2

```
@SpringBootApplication
@ComponentScan({"com.coral.spring.boot.starter.s2"})
public class StarterS2Application {
}

```

* spring-boot-web-boot

```
public class StarterMultiApplication {
    private static final Logger LOGGER = LoggerFactory.getLogger(StarterMultiApplication.class);

    public static void main(String[] args) {

        SpringApplication.run(StarterS1Application.class, mergeArgs("--spring.profiles.active=s1", args));

        SpringApplication.run(StarterS2Application.class, mergeArgs("--spring.profiles.active=s2", args));
    }

    public static String[] mergeArgs(String arg, String[] args) {
        String[] retArgs = new String[args.length + 1];
        retArgs[0] = arg;
        for (int i = 1; i < retArgs.length; i++) {
            retArgs[i] = args[i - 1];
        }
        return retArgs;
    }
}

```