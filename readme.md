#### 使用说明
    修改当前目录下setting.gradle
    
        rootProject.name = 'spring-boot-example'
        
        //include 'spring-boot:spring-boot-docker'
        //include 'spring-boot:spring-boot-starter'
        
        include 'spring-swagger'
        
        include 'spring-dubbo:spring-dubbo-api'
        include 'spring-dubbo:spring-dubbo-provider'
        include 'spring-dubbo:spring-dubbo-consumer'
    
    可测试不同模块
    
    刷新依赖
    gradle build -x test
    
#### 具备demo示例
    
    

