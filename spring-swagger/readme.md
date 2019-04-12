#### Swagger使用说明

github示例:[spring-swagger示例](https://github.com/coral-learning/spring-boot-example/tree/master/spring-swagger)

##### Swagger介绍 

    Swagger能成为最受欢迎的REST APIs文档生成工具之一，有以下几个原因：
        Swagger 可以生成一个具有互动性的API控制台，开发者可以用来快速学习和尝试API。
        Swagger 可以生成客户端SDK代码用于各种不同的平台上的实现。
        Swagger 文件可以在许多不同的平台上从代码注释中自动生成。
        Swagger 有一个强大的社区，里面有许多强悍的贡献者。
        
    Swagger 文档提供了一个方法，使我们可以用指定的 JSON 或者 YAML 摘要来描述你的 API，包括了比如 names、order 等 API 信息。
     
    你可以通过一个文本编辑器来编辑 Swagger 文件，或者你也可以从你的代码注释中自动生成。各种工具都可以使用 Swagger 文件来生成互动的 API 文档。
     
    注意：用 Swagger 文件生成互动的 API 文档是最精简的，它展示了资源、参数、请求、响应。但是它不会提供你的API如何工作的其他任何一个细节。
     
##### 功能介绍

    Swagger是一组开源项目，其中主要要项目如下：
    1.Swagger-tools:提供各种与Swagger进行集成和交互的工具。例如模式检验、Swagger 1.2文档转换成Swagger 2.0文档等功能。
    2.Swagger-core: 用于Java/Scala的的Swagger实现。与JAX-RS(Jersey、Resteasy、CXF...)、Servlets和Play框架进行集成。
    3.Swagger-js: 用于JavaScript的Swagger实现。
    4.Swagger-node-express: Swagger模块，用于node.js的Express web应用框架。
    5.Swagger-ui：一个无依赖的HTML、JS和CSS集合，可以为Swagger兼容API动态生成优雅文档。
    6.Swagger-codegen：一个模板驱动引擎，通过分析用户Swagger资源声明以各种语言生成客户端代码。

#### 使用示例
    
    1.添加依赖
 
        compile group: 'io.springfox', name: 'springfox-swagger-ui', version:'2.7.0'
        compile group: 'io.springfox', name: 'springfox-swagger2', version:'2.7.0'
        
    2.创建Swagger2配置类
        在Application.java同级创建Swagger2的配置类Swagger2
        通过createRestApi函数创建Docket的Bean之后，apiInfo()用来创建该Api的基本信息（这些基本信息会展现在文档页面中）。
        /**
         * Swagger2配置类
         * 在与spring boot集成时，放在与Application.java同级的目录下。
         * 通过@Configuration注解，让Spring来加载该类配置。
         * 再通过@EnableSwagger2注解来启用Swagger2。
         */
        @Configuration
        @EnableSwagger2
        public class Swagger2 {
            
            /**
             * 创建API应用
             * apiInfo() 增加API相关信息
             * 通过select()函数返回一个ApiSelectorBuilder实例,用来控制哪些接口暴露给Swagger来展现，
             * 本例采用指定扫描的包路径来定义指定要建立API的目录。
             * 
             * @return
             */
            @Bean
            public Docket createRestApi() {
                return new Docket(DocumentationType.SWAGGER_2)
                        .apiInfo(apiInfo())
                        .select()
                        .apis(RequestHandlerSelectors.basePackage("com.coral.learning.swagger"))
                        .paths(PathSelectors.any())
                        .build();
            }
            
            /**
             * 创建该API的基本信息（这些基本信息会展现在文档页面中）
             * 访问地址：http://项目实际地址/swagger-ui.html
             * @return
             */
            private ApiInfo apiInfo() {
                return new ApiInfoBuilder()
                        .title("Spring Boot中使用Swagger2构建RESTful APIs")
                        .description("更多请关注http://www.baidu.com")
                        .termsOfServiceUrl("http://www.baidu.com")
                        .contact("wuhao")
                        .version("1.0")
                        .build();
            }
        }
    3.添加文档内容
        在完成了上述配置后，其实已经可以生产文档内容，但是这样的文档主要针对请求本身，描述的主要来源是函数的命名，
        对用户并不友好，我们通常需要自己增加一些说明来丰富文档内容。
        
        Swagger使用的注解及其说明：
        @Api：用在类上，说明该类的作用。
        @ApiOperation：注解来给API增加方法说明。
        @ApiImplicitParams : 用在方法上包含一组参数说明。
        @ApiImplicitParam：用来注解来给方法入参增加说明。
        @ApiResponses：用于表示一组响应
        
        @ApiResponse：用在@ApiResponses中，一般用于表达一个错误的响应信息
            code：数字，例如400   
            message：信息，例如"请求参数没填好"   
            response：抛出异常的类   
        @ApiModel：描述一个Model的信息（一般用在请求参数无法使用@ApiImplicitParam注解进行描述的时候）    
            @ApiModelProperty：描述一个model的属性
        
        注意：@ApiImplicitParam的参数说明：
        Parameter Types
        
            OpenAPI 3.0 distinguishes between the following parameter types based on the parameter location. 
            The location is determined by the parameter’s in key, for example, in: query or in: path.
            path parameters, such as /users/{id}
            query parameters, such as /users?role=admin
            header parameters, such as X-MyHeader: Value
            cookie parameters, which are passed in the Cookie header, such as Cookie: debug=0; csrftoken=BUSe35dohU3O1MZvDCU
            
            paramType：指定参数放在哪个地方
            header：请求参数放置于Request Header，使用@RequestHeader获取
            query：请求参数放置于请求地址，使用@RequestParam获取
            path：（用于restful接口）-->请求参数的获取：@PathVariable
            body：（不常用）
            form（不常用）
            name：参数名
            dataType：参数类型
            required：参数是否必须传
            true | false
            value：说明参数的意思
            defaultValue：参数的默认值
    4.spring-boot control使用示例
        @Controller
        @RequestMapping("/sgc")
        @Api(value = "Swagger接口说明")
        
        public class SwaggerControl {
        
            @ResponseBody
            @RequestMapping(value ="/getUserName", method= RequestMethod.GET)
            @ApiOperation(value="根据用户编号获取用户姓名", notes="test: 仅1和2有正确返回")
            @ApiImplicitParam(paramType="query", name = "userNumber", value = "用户编号", required = true, dataType = "Integer")
            public String getUserName(@RequestParam Integer userNumber){
                if(userNumber == 1){
                    return "张三丰";
                }
                else if(userNumber == 2){
                    return "慕容复";
                }
                else{
                    return "未知";
                }
            }
        
            @ResponseBody
            @RequestMapping("/updatePassword")
            @ApiOperation(value="修改用户密码", notes="根据用户id修改密码")
            @ApiImplicitParams({
                    @ApiImplicitParam(paramType="query", name = "userId", value = "用户ID", required = true, dataType = "Integer"),
                    @ApiImplicitParam(paramType="query", name = "password", value = "旧密码", required = true, dataType = "String"),
                    @ApiImplicitParam(paramType="query", name = "newPassword", value = "新密码", required = true, dataType = "String")
            })
            public String updatePassword(@RequestParam(value="userId") Integer userId, @RequestParam(value="password") String password,
                                         @RequestParam(value="newPassword") String newPassword){
                if(userId <= 0 || userId > 2){
                    return "未知的用户";
                }
                if(StringUtils.isEmpty(password) || StringUtils.isEmpty(newPassword)){
                    return "密码不能为空";
                }
                if(password.equals(newPassword)){
                    return "新旧密码不能相同";
                }
                return "密码修改成功!";
            }
        }
        
    5.结果查看
      http://localhost:8131/swagger-ui.html