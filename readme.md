#### 使用说明
    修改当前目录下setting.gradle
    
        rootProject.name = 'spring-boot-example'
        
        ////启动模块
        //include 'spring-boot:spring-boot-docker'
        //include 'spring-boot:spring-boot-starter'
        
        
        //安全认证
        include 'spring-security:spring-security-shiro'
        include 'spring-security:spring-security-starter'
        
        //数据模块
        //include 'spring-data:spring-data-mybatis'
        //include 'spring-data:spring-data-jpa'
        //include 'spring-data:spring-data-jpa-mybatis'
        
        ////分布式链路追踪
        //include 'spring-cloud-sleuth:spring-cloud-sleuth-invoking'
        //include 'spring-cloud-sleuth:spring-cloud-sleuth-server'
        //include 'spring-cloud-sleuth:spring-cloud-sleuth-service'
        //
        //
        ////文档生成
        //include 'spring-swagger'
        
        
        ////spring-for-rpc
        //include 'spring-dubbo:spring-dubbo-api'
        //include 'spring-dubbo:spring-dubbo-provider'
        //include 'spring-dubbo:spring-dubbo-consumer'
    
    可测试不同模块
    
    刷新依赖
    gradle build -x test
    
#### 具备demo示例
    
    

