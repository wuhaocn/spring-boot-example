Spring Cloud Config
Table of Contents
关于(about)
贡献者(Contributor)
快速入门(Quick Start)
客户端示例(Client Side Usage)
配置服务(Spring Cloud Config Server)
资源库环境(Environment Repository)
健康指示器(Health Indicator)
安全(Security)
加密与解密(Encryption and Decryption)
密钥管理(Key Management)
创建一个测试密钥库(Creating a Key Store for Testing)
使用多密钥和循环密钥(Using Multiple Keys and Key Rotation)
加密属性服务(Serving Encrypted Properties)
可替换格式服务(Serving Alternative Formats)
文本解释服务(Serving Plain Text)
嵌入配置服务器(Embedding the Config Server)
推送通知和总线(Push Notifications and Spring Cloud Bus)
客户端配置(Spring Cloud Config Client)
配置第一次引导(Config First Bootstrap)
发现第一次引导(Discovery First Bootstrap)
Config Client Fail Fast
配置客户端重试(Config Client Retry)
定位远程配置资源(Locating Remote Configuration Resources)
安全(Security)
Vault
关于(about)
由于翻译质量不高，所以合成为中英对照双语版本，如有不当之处，可联系修正，感谢你的支持。

贡献者(Contributor)
木木彬(252971652@qq.com)

Andy.Ren(rcl026@163.com)

甘明(xyuu@xyuu.net)

Spring Cloud Config provides server and client-side support for externalized configuration in a distributed system. With the Config Server you have a central place to manage external properties for applications across all environments. The concepts on both client and server map identically to the Spring Environment and PropertySource abstractions, so they fit very well with Spring applications, but can be used with any application running in any language. As an application moves through the deployment pipeline from dev to test and into production you can manage the configuration between those environments and be certain that applications have everything they need to run when they migrate. The default implementation of the server storage backend uses git so it easily supports labelled versions of configuration environments, as well as being accessible to a wide range of tooling for managing the content. It is easy to add alternative implementations and plug them in with Spring configuration.

快速入门(Quick Start)
Start the server:

$ cd spring-cloud-config-server
$ ../mvnw spring-boot:run
The server is a Spring Boot application so you can run it from your IDE instead if you prefer (the main class is ConfigServerApplication). Then try out a client:

这个服务是一个Spring Boot应用,如果你喜欢你可以直接用IDE启动(main类是ConfigServerApplication). 然后试着启动一个客户端:

$ curl localhost:8888/foo/development
{"name":"development","label":"master","propertySources":[
  {"name":"https://github.com/scratches/config-repo/foo-development.properties","source":{"bar":"spam"}},
  {"name":"https://github.com/scratches/config-repo/foo.properties","source":{"foo":"bar"}}
]}
The default strategy for locating property sources is to clone a git repository (at spring.cloud.config.server.git.uri) and use it to initialize a mini SpringApplication. The mini-application’s Environment is used to enumerate property sources and publish them via a JSON endpoint.

默认加载property资源的策略是克隆一个git仓库(at spring.cloud.config.server.git.uri'),用它去初始化一个mini `SpringApplication, 这个mini SpringApplication的Environment用来枚举property,通过json节点发布它们.

The HTTP service has resources in the form:

HTTP服务资源的构成:

/{application}/{profile}[/{label}]
/{application}-{profile}.yml
/{label}/{application}-{profile}.yml
/{application}-{profile}.properties
/{label}/{application}-{profile}.properties
where the "application" is injected as the spring.config.name in the SpringApplication (i.e. what is normally "application" in a regular Spring Boot app), "profile" is an active profile (or comma-separated list of properties), and "label" is an optional git label (defaults to "master".)

application是SpringApplication的spring.config.name,(一般来说'application'是一个常规的Spring Boot应用),profile是一个active的profile(或者逗号分隔的属性列表),label是一个可选的git标签(默认为"master").

Spring Cloud Config Server pulls configuration for remote clients from a git repository (which must be provided):

Spring Cloud Config Server从git仓库为 为远程客户端拉取配置信息：

spring:
  cloud:
    config:
      server:
        git:
          uri: https://github.com/spring-cloud-samples/config-repo
客户端示例(Client Side Usage)
To use these features in an application, just build it as a Spring Boot application that depends on spring-cloud-config-client (e.g. see the test cases for the config-client, or the sample app). The most convenient way to add the dependency is via a Spring Boot starter org.springframework.cloud:spring-cloud-starter-config. There is also a parent pom and BOM (spring-cloud-starter-parent) for Maven users and a Spring IO version management properties file for Gradle and Spring CLI users. Example Maven configuration:

通过构建一个依赖spring-cloud-config-client(例如config-client的测试用例或者样例)的Spring Boot应用,就可以使用这些特性.最方便的方法就是添加一个依赖org.springframework.cloud:spring-cloud-starter-config.对于Maven用户,有一个父pom和BOM(spring-cloud-starter-parent),对于Gradle和Spring CLI用户有一个Spring IO 版本管理文件.例如Maven的配置:

pom.xml
   <parent>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-starter-parent</artifactId>
       <version>1.3.5.RELEASE</version>
       <relativePath /> <!-- lookup parent from repository -->
   </parent>

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

<dependencies>
	<dependency>
		<groupId>org.springframework.cloud</groupId>
		<artifactId>spring-cloud-starter-config</artifactId>
	</dependency>
	<dependency>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-test</artifactId>
		<scope>test</scope>
	</dependency>
</dependencies>

<build>
	<plugins>
           <plugin>
               <groupId>org.springframework.boot</groupId>
               <artifactId>spring-boot-maven-plugin</artifactId>
           </plugin>
	</plugins>
</build>

   <!-- repositories also needed for snapshots and milestones -->
Then you can create a standard Spring Boot application, like this simple HTTP server:

然后你可以创建一个标准的Spring Boot应用,比如一个简单的HTTP服务:

@SpringBootApplication
@RestController
public class Application {

    @RequestMapping("/")
    public String home() {
        return "Hello World!";
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
When it runs it will pick up the external configuration from the default local config server on port 8888 if it is running. To modify the startup behaviour you can change the location of the config server using bootstrap.properties (like application.properties but for the bootstrap phase of an application context), e.g.

当它运行的时候,它将默认从本地配置服务器8888端口获取外部配置,你可以改版配置服务的位置通过bootstrap.properties(类似于application.properties,但是是一个应用上下文启动的配置文件),例如:

spring.cloud.config.uri: http://myconfigserver.com
The bootstrap properties will show up in the /env endpoint as a high-priority property source, e.g.

bootstrap.properties将会在/env节点作为一个高优先级的property展示,例如:

$ curl localhost:8080/env
{
  "profiles":[],
  "configService:https://github.com/spring-cloud-samples/config-repo/bar.properties":{"foo":"bar"},
  "servletContextInitParams":{},
  "systemProperties":{...},
  ...
}
(a property source called "configService:<URL of remote repository>/<file name>" contains the property "foo" with value "bar" and is highest priority).

(一条叫做"configService:<URL of remote repository>/<file name>"的属性源包含了属性"foo"和值"bar",它是最高优先级的)

the URL in the property source name is the git repository not the config server URL.
属性源中的 URL是git仓库的地址而不是配置服务器的URL.
配置服务(Spring Cloud Config Server)
The Server provides an HTTP, resource-based API for external configuration (name-value pairs, or equivalent YAML content). The server is easily embeddable in a Spring Boot application using the @EnableConfigServer annotation. So this app is a config server:

针对系统外的配置项(如name-value对或相同功能的YAML内容),该服务器提供了基于资源的HTTP接口.使用@EnableConfigServer注解,该服务器可以很容易的被嵌入到Spring Boot 类型系统中.使用该注解之后该应用系统也是一个配置服务器:

ConfigServer.java
@SpringBootApplication
@EnableConfigServer
public class ConfigServer {
  public static void main(String[] args) {
    SpringApplication.run(ConfigServer.class, args);
  }
}
Like all Spring Boot apps it runs on port 8080 by default, but you can switch it to the conventional port 8888 in various ways. The easiest, which also sets a default configuration repository, is by launching it with spring.config.name=configserver (there is a configserver.yml in the Config Server jar). Another is to use your own application.properties, e.g.

像所有的Spring Boot应用系统默认运行在8080端口一样,你也可以通过各种方式切换这个端口为Config Server默认运行端口为8888.最简单的方法是启动Config Server 时,通过spring.config.name=configserver参数(在Config Server jar中必须有一个configserver.yml文件),这种方式也可以给整个Config Server设置一个默认配置参数集合.另一种方式是使用你自己的application.properties文件, e.g.

application.properties
server.port: 8888
spring.cloud.config.server.git.uri: file://${user.home}/config-repo
where ${user.home}/config-repo is a git repository containing YAML and properties files.

其中${user.home}/config-repo包含了YAML 和 properties文件的git库.

in Windows you need an extra "/" in the file URL if it is absolute with a drive prefix, e.g. file:///${user.home}/config-repo.
在Windows系统中,如果文件URL是绝对路径并前面有驱动符号,你需要多增加个'/'符号, e.g. file:///${user.home}/config-repo.
Here’s a recipe for creating the git repository in the example above:

创建上面例子使用的git库,使用如下简单方法:

$ cd $HOME
$ mkdir config-repo
$ cd config-repo
$ git init .
$ echo info.foo: bar > application.properties
$ git add -A .
$ git commit -m "Add application.properties"
using the local filesystem for your git repository is intended for testing only. Use a server to host your configuration repositories in production.
你的git库使用本地文件系统目的是为了测试.在生产环境中,你需要使用服务器来运行你的git库.
the initial clone of your configuration repository will be quick and efficient if you only keep text files in it. If you start to store binary files, especially large ones, you may experience delays on the first request for configuration and/or out of memory errors in the server.
在配置库中,你如果只存储文本文件,初始clone配置库是非常快捷和高效的.如果你开始存储二进制文件特别是大型的二进制文件,你可能会遇到第一次请求配置文件比较慢或遇到发生在配置服务器上的内存溢出.
资源库环境(Environment Repository)
Where do you want to store the configuration data for the Config Server? The strategy that governs this behaviour is the EnvironmentRepository, serving Environment objects. This Environment is a shallow copy of the domain from the Spring Environment (including propertySources as the main feature). The Environment resources are parametrized by three variables:

你想把Config Server的配置数据存储到哪里?解决这个问题的策略是EnvironmentRepository,并提供Environment对象.Environment对象是对Spring的Environment(其中包括 propertySources做为主要属性)的浅拷贝. Environment资源被参数化成了3个变量:

{application} maps to "spring.application.name" on the client side;

{profile} maps to "spring.profiles.active" on the client (comma separated list); and

{label} which is a server side feature labelling a "versioned" set of config files.

{application} 对应客户端的"spring.application.name"属性;

{profile} 对应客户端的 "spring.profiles.active"属性(逗号分隔的列表); 和

{label} 对应服务端属性,这个属性能标示一组配置文件的版本.

Repository implementations generally behave just like a Spring Boot application loading configuration files from a "spring.config.name" equal to the {application} parameter, and "spring.profiles.active" equal to the {profiles} parameter. Precedence rules for profiles are also the same as in a regular Boot application: active profiles take precedence over defaults, and if there are multiple profiles the last one wins (like adding entries to a Map).

Repository的通常实现特征,非常像SpringBoot系统加载配置文件,从"spring.config.name"加载配置相当于从{application}参数加载,从"spring.profiles.active"加载配置相当于从{profiles}参数加载. profiles的优先级规则也和通常的Spring Boot系统一样:激活的profiles的优先级高于defaults,有多个profiles,最后一个起作用 (像往Map中增加entries).

Example: a client application has this bootstrap configuration:

举例: 一个客户端应用系统有这样一个bootstrap 配置:

bootstrap.yml
spring:
  application:
    name: foo
  profiles:
    active: dev,mysql
(as usual with a Spring Boot application, these properties could also be set as environment variables or command line arguments).

(像通常的Spring Boot应用程序一样, 这些参数也可以通过环境变量或命令行参数进行设置).

If the repository is file-based, the server will create an Environment from application.yml (shared between all clients), and foo.yml (with foo.yml taking precedence). If the YAML files have documents inside them that point to Spring profiles, those are applied with higher precedence (in order of the profiles listed), and if there are profile-specific YAML (or properties) files these are also applied with higher precedence than the defaults. Higher precedence translates to a PropertySource listed earlier in the Environment. (These are the same rules as apply in a standalone Spring Boot application.)

如果配置库是基于文件的,服务器将从application.yml(所有的客户端共享)和 foo.yml(foo.yml拥有高优先级)中创建一个Environment对象. 如果这些 YAML文件中有指定Spring profiles,那么这些profiles将有较高优先级(按在profiles列出的顺序),同时如果存在指定profile的YAML(或properties)文件,这些文件就比default文件具有较高优先级.高优先级的配置优先转成Environment对象中的PropertySource.(这和单独的Spring Boot系统使用的规则是一样的.)

Git后端(Git Backend)
The default implementation of EnvironmentRepository uses a Git backend, which is very convenient for managing upgrades and physical environments, and also for auditing changes. To change the location of the repository you can set the "spring.cloud.config.server.git.uri" configuration property in the Config Server (e.g. in application.yml). If you set it with a file: prefix it should work from a local repository so you can get started quickly and easily without a server, but in that case the server operates directly on the local repository without cloning it (it doesn’t matter if it’s not bare because the Config Server never makes changes to the "remote" repository). To scale the Config Server up and make it highly available, you would need to have all instances of the server pointing to the same repository, so only a shared file system would work. Even in that case it is better to use the ssh: protocol for a shared filesystem repository, so that the server can clone it and use a local working copy as a cache.

EnvironmentRepository的默认实现是使用Git后端,Git后端对于管理升级和物理环境是很方便的,对审计配置变更也很方便.想改变配置库的位置,你可以在Config Server中设置"spring.cloud.config.server.git.uri"配置项的值(e.g. 如在 application.yml文件中).如果你设置这个属性使用 file:前缀,Config Server 是从本地配置库中取数据,这种方式可以不使用Git的情况下,快速和简单的运行起来,但是这种情况下,Config Server不通过Clone Git配置库直接操作本地库(如果本地配置库不为空,也不用担心,因为Config Server不会把变更推送到"remote"库中). 为了增强Config Server 的高可靠性,需要按比例增加Config Server的数量,这时候需要让所有的Config Server 实例指向相同的配置库,此种情况下只能有一个共享的文件系统才能很好的运行.即使在这种情况下,最好还是使用ssh: 协议来访问共享文件系统库,以便让服务器能够克隆Git库并可以让本地工作副本当作缓存来使用.

This repository implementation maps the {label} parameter of the HTTP resource to a git label (commit id, branch name or tag). If the git branch or tag name contains a slash ("/") then the label in the HTTP URL should be specified with the special string "(_)" instead (to avoid ambiguity with other URL paths). Be careful with the brackets in the URL if you are using a command line client like curl (e.g. escape them from the shell with quotes '').

这个配置库的实现通过映射HTTP资源的{label}参数为git label(提交id,分支名称或tag).如果git分支或tag的名称包含一个斜杠 ("/"),此时HTTP URL中的label需要使用特殊字符串"(_)"来替代(为了避免与其他URL路径相互混淆).如果使用了命令行客户端如 curl,请谨慎处理URL中的括号(例如：在shell下请使用引号''来转移他们).

Git URI占位符(Placeholders in Git URI)

Spring Cloud Config Server supports a git repository URL with placeholders for the {application} and {profile} (and {label} if you need it, but remember that the label is applied as a git label anyway). So you can easily support a "one repo per application" policy using (for example):

Spring Cloud Config Server支持git库URL中包含针对{application}和 {profile}的占位符(如果你需要,{label}也可包含占位符, 不过要牢记的是任何情况下label只指git的label).所以,你可以很容易的支持"一个应用系统一个配置库"策略(举例):

spring:
  cloud:
    config:
      server:
        git:
          uri: https://github.com/myorg/{application}
or a "one repo per profile" policy using a similar pattern but with {profile}.

或者"一个profile一个配置库"策略,这两种策略的使用模式是一样的,后者使用{profile}进行配置.

模式匹配和多资源库(Pattern Matching and Multiple Repositories)

There is also support for more complex requirements with pattern matching on the application and profile name. The pattern format is a comma-separated list of {application}/{profile} names with wildcards (where a pattern beginning with a wildcard may need to be quoted). Example:

针对application和profile名称的模式匹配,Config Server也支持更复杂的需求.匹配的表达式格式是用带有通配符号的{application}/{profile}名称列表,这些名称列表用逗号分隔. (如果这个表达式用通配符开始,则这个表达式需要用引号括住). 例如:

spring:
  cloud:
    config:
      server:
        git:
          uri: https://github.com/spring-cloud-samples/config-repo
          repos:
            simple: https://github.com/simple/config-repo
            special:
              pattern: special*/dev*,*special*/dev*
              uri: https://github.com/special/config-repo
            local:
              pattern: local*
              uri: file:/home/configsvc/config-repo
If {application}/{profile} does not match any of the patterns, it will use the default uri defined under "spring.cloud.config.server.git.uri". In the above example, for the "simple" repository, the pattern is simple/* (i.e. it only matches one application named "simple" in all profiles). The "local" repository matches all application names beginning with "local" in all profiles (the /* suffix is added automatically to any pattern that doesn’t have a profile matcher).

如果 {application}/{profile}不能匹配任何表达式, 那么将使用"spring.cloud.config.server.git.uri"对应的值. 在上例子中, 对于 "simple" 配置库, 匹配模式是simple/* (也就说,无论profile是什么，它只匹配application 名称为"simple"应用系统)."local"库匹配所有application名称以"local"开头任何应用系统,不管profiles是什么(因没有配置对profile的匹配规则,/*后缀会被自动的增加到任何的匹配表达式中 ).

the "one-liner" short cut used in the "simple" example above can only be used if the only property to be set is the URI. If you need to set anything else (credentials, pattern, etc.) you need to use the full form.
在上述"simple"例子中,如果设置的属性只是一个URI,只使用一行就能完整的表示清楚. 如果你需要设置其他属性(如credentials, pattern, etc.),你需要使用完整的格式才能表示清楚.
The pattern property in the repo is actually an array, so you can use a YAML array (or [0], [1], etc. suffixes in properties files) to bind to multiple patterns. You may need to do this if you are going to run apps with multiple profiles. Example:

在配置参数中pattern本质上讲,其实是个数组, 因此你可以使用一个YAML 数组(或在properties类型文件中使用 [0],[1],etc.为配置后缀)来绑定多个匹配表达式.当你想使用多个了profile 来运行应用程序时,你可能需要采用数组匹配模式. Example:

spring:
  cloud:
    config:
      server:
        git:
          uri: https://github.com/spring-cloud-samples/config-repo
          repos:
            development:
              pattern:
                - */development
                - */staging
              uri: https://github.com/development/config-repo
            staging:
              pattern:
                - */qa
                - */production
              uri: https://github.com/staging/config-repo
Spring Cloud will guess that a pattern containing a profile that doesn’t end in * implies that you actually want to match a list of profiles starting with this pattern (so */staging is a shortcut for ["*/staging", "*/staging,*"]). This is common where you need to run apps in the "development" profile locally but also the "cloud" profile remotely, for instance.
如果 profile 不是以* 结尾的,Spring Cloud 会猜想实际的profile值, 这意味着你实际上是匹配以这个表达式开头的一个profile列表(因此 */staging 只是 ["*/staging", "*/staging,*"]的 简单表示法)。举例，普遍****的一种方法是在本地环境中使用"development" profile 来运行系统,在远程环境中运行"cloud" profile.
Every repository can also optionally store config files in sub-directories, and patterns to search for those directories can be specified as searchPaths. For example at the top level:

每个配置库也可以选择存储配置文件到子目录中,用searchPaths属性来指定配置的子目录,搜索匹配的表达式就在此子目录中进行的. 大概的例子如下:

spring:
  cloud:
    config:
      server:
        git:
          uri: https://github.com/spring-cloud-samples/config-repo
          searchPaths: foo,bar*
In this example the server searches for config files in the top level and in the "foo/" sub-directory and also any sub-directory whose name begins with "bar".

在这个例子中,服务会在顶层目录、"foo/"子目录和以"bar"开头的子目录中查找文件.

By default the server clones remote repositories when configuration is first requested. The server can be configured to clone the repositories at startup. For example at the top level:

当配置文件第一次被请求时,默认地服务器会clone远程的配置库.服务器也可以配置在启动时clone远程的配置库.下面就是一个简单的例子:

spring:
  cloud:
    config:
      server:
        git:
          uri: https://git/common/config-repo.git
          repos:
            team-a:
                pattern: team-a-*
                cloneOnStart: true
                uri: http://git/team-a/config-repo.git
            team-b:
                pattern: team-b-*
                cloneOnStart: false
                uri: http://git/team-b/config-repo.git
            team-c:
                pattern: team-c-*
                uri: http://git/team-a/config-repo.git
In this example the server clones team-a’s config-repo on startup before it accepts any requests. All other repositories will not be cloned until configuration from the repository is requested.

在这个例子中, 服务器在启动未接收任何请求的过程中,会clone team-a的配置库.其他的配置库都只在请求时才会clone远程的配置.

To use HTTP basic authentication on the remote repository add the "username" and "password" properties separately (not in the URL), e.g.

对于远程配置库,为了使用HTTP的 basic authentication 方法进行认证,需要分开增加 "username" and "password" 属性 (不在URL中增加),

spring:
  cloud:
    config:
      server:
        git:
          uri: https://github.com/spring-cloud-samples/config-repo
          username: trolley
          password: strongpassword
If you don’t use HTTPS and user credentials, SSH should also work out of the box when you store keys in the default directories (~/.ssh) and the uri points to an SSH location, e.g. "git@github.com:configuration/cloud-configuration". The repository is accessed using JGit, so any documentation you find on that should be applicable. HTTPS proxy settings can be set in ~/.git/config or in the same way as for any other JVM process via system properties (-Dhttps.proxyHost and -Dhttps.proxyPort).

如果你没有使用HTTPS和用户凭证,当你在默认目录(~/.ssh)中存储了key,并且uri参数配置了SSH地址,如配置了"git@github.com:configuration/cloud-configuration",SSH应该是你非常容易使用的方式.这种方式下,是使用JGit来访问配置库的,比较适合查找任何文档.HTTPS proxy 可以在 ~/.git/config中进行设置, 通过JVM的系统属性(-Dhttps.proxyHost and -Dhttps.proxyPort)设置可以完成此功能.

If you don’t know where your ~/.git directory is us git config --global to manipulate the settings (e.g. git config --global http.sslVerify false).
如果你不清楚你的 ~/.git 目录在哪里,可以使用git config --global命令进行修改该设置(e.g. git config --global http.sslVerify false).
Git搜索路径中的占位符(Placeholders in Git Search Paths)

Spring Cloud Config Server also supports a search path with placeholders for the {application} and {profile} (and {label} if you need it). Example:

Spring Cloud Config Server也支持搜寻的{application} 和 {profile} (如果需要{label} 也可以）中存在占位符. 举例:

spring:
  cloud:
    config:
      server:
        git:
          uri: https://github.com/spring-cloud-samples/config-repo
          searchPaths: '{application}'
searches the repository for files in the same name as the directory (as well as the top level). Wildcards are also valid in a search path with placeholders (any matching directory is included in the search).

在和application相同的名字的路径中查找配置库中的文件. 占位符在查找路径中一样有效 (包括任何搜寻目录中的匹配路径).

版本控制后端文件系统使用(Version Control Backend Filesystem Use)
With VCS based backends (git, svn) files are checked out or cloned to the local filesystem. By default they are put in the system temporary directory with a prefix of config-repo-. On linux, for example it could be /tmp/config-repo-<randomid>. Some operating systems routinely clean out temporary directories. This can lead to unexpected behaviour such as missing properties. To avoid this problem, change the directory Config Server uses, by setting spring.cloud.config.server.git.basedir or spring.cloud.config.server.svn.basedir to a directory that does not reside in the system temp structure.
伴随着版本控制系统作为后端(git, svn)，文件都会被check out 或clone 到本地文件系统中. 默认这些文件会被放置到以config-repo-为前缀的系统临时目录中.在 linux 上, 譬如应该是 /tmp/config-repo-<randomid>目录. 有些操作系统http://serverfault.com/questions/377348/when-does-tmp-get-cleared/377349#377349[routinely clean out]放到临时目录中,这会导致不可预知的问题出现.为了避免这个问题,通过设置spring.cloud.config.server.git.basedir 或spring.cloud.config.server.svn.basedir参数值为非系统临时目录.
文件系统后端(File System Backend)
There is also a "native" profile in the Config Server that doesn’t use Git, but just loads the config files from the local classpath or file system (any static URL you want to point to with "spring.cloud.config.server.native.searchLocations"). To use the native profile just launch the Config Server with "spring.profiles.active=native".

在Config Server中,还有一种不使用Git的"native"的配置方式,这种方式是从本地classpath 或文件系统中加载配置文件(使用 "spring.cloud.config.server.native.searchLocations"配置项进行设置). 加载Config Server 的"spring.profiles.active=native"配置项可以开启native配置.

Remember to use the file: prefix for file resources (the default without a prefix is usually the classpath). Just as with any Spring Boot configuration you can embed ${}-style environment placeholders, but remember that absolute paths in Windows require an extra "/", e.g. file:///${user.home}/config-repo
牢记使用file:前缀来指示资源 (默认没有前缀是从classpath中去文件).就像任何 Spring Boot配置一样,你也可以 嵌入${}环境参数占位符,但是windows系统下使用绝对路径,前缀后面需要多加个"/", e.g. file:///${user.home}/config-repo
The default value of the searchLocations is identical to a local Spring Boot application (so [classpath:/, classpath:/config, file:./, file:./config]). This does not expose the application.properties from the server to all clients because any property sources present in the server are removed before being sent to the client.
默认的 searchLocations值和本地Spring Boot 应用系统是一样的(如 [classpath:/, classpath:/config,file:./, file:./config]). 这种方式下,并不暴露服务器上的application.properties文件给客户端,因为在把属性传给客户端之前,服务器中属性源信息会被删除掉.
A filesystem backend is great for getting started quickly and for testing. To use it in production you need to be sure that the file system is reliable, and shared across all instances of the Config Server.
对于测试来说,用文件系统做后端是非常便捷的.如果想生产环境中使用它,你需要确定文件系统的可靠性,并垮所有的Config Server 实例是能够共享这些本地文件.
The search locations can contain placeholders for {application}, {profile} and {label}. In this way you can segregate the directories in the path, and choose a strategy that makes sense for you (e.g. sub-directory per application, or sub-directory per profile).

对于{application},{profile} and {label}来说,搜寻位置可以包含占位符. 采用这种方式,你可以隔离目录,也可以选择某种策略来使你的应用程序更加清晰明了(如:一个application一个子目录,或一个profile一个子目录).

If you don’t use placeholders in the search locations, this repository also appends the {label} parameter of the HTTP resource to a suffix on the search path, so properties files are loaded from each search location and a subdirectory with the same name as the label (the labelled properties take precedence in the Spring Environment). Thus the default behaviour with no placeholders is the same as adding a search location ending with /{label}/. For example `file:/tmp/config is the same as file:/tmp/config,file:/tmp/config/{label}

如果你在在查找位置中不指定占位符,可以把在HTTP资源请求参数{label}追加到查询路径的后面,此时是从查询路径和用label名称一样的子目录中加载配置文件的(在Spring环境下,被打标记的属性具有较高优先级别).因此,没有占位符的默认特性和以/{label}/结尾的结果一样.举例`file:/tmp/config和 file:/tmp/config,file:/tmp/config/{label}请求效果等同.

Vault后端(Vault Backend)
Spring Cloud Config Server also supports Vault as a backend.

Vault is a tool for securely accessing secrets. A secret is anything that you want to tightly control access to, such as API keys, passwords, certificates, and more. Vault provides a unified interface to any secret, while providing tight access control and recording a detailed audit log.
For more information on Vault see the Vault quickstart guide.

To enable the config server to use a Vault backend you must run your config server with the vault profile. For example in your config server’s application.properties you can add spring.profiles.active=vault.

By default the config server will assume your Vault server is running at http://127.0.0.1:8200. It also will assume that the name of backend is secret and the key is application. All of these defaults can be configured in your config server’s application.properties. Below is a table of configurable Vault properties. All properties are prefixed with spring.cloud.config.server.vault.

Name	Default Value
host
127.0.0.1
port
8200
scheme
http
backend
secret
defaultKey
application
profileSeparator
,
All configurable properties can be found in org.springframework.cloud.config.server.environment.VaultEnvironmentRepository.

With your config server running you can make HTTP requests to the server to retrieve values from the Vault backend. To do this you will need a token for your Vault server.

First place some data in you Vault. For example

$ vault write secret/application foo=bar baz=bam
$ vault write secret/myapp foo=myappsbar
Now make the HTTP request to your config server to retrieve the values.

$ curl -X "GET" "http://localhost:8888/myapp/default" -H "X-Config-Token: yourtoken"

You should see a response similar to this after making the above request.

{
   "name":"myapp",
   "profiles":[
      "default"
   ],
   "label":null,
   "version":null,
   "state":null,
   "propertySources":[
      {
         "name":"vault:myapp",
         "source":{
            "foo":"myappsbar"
         }
      },
      {
         "name":"vault:application",
         "source":{
            "baz":"bam",
            "foo":"bar"
         }
      }
   ]
}
多配置源(Multiple Properties Sources)

When using Vault you can provide your applications with multiple properties sources. For example, assume you have written data to the following paths in Vault.

secret/myApp,dev
secret/myApp
secret/application,dev
secret/application
Properties written to secret/application are available to all applications using the Config Server. An application with the name myApp would have any properties written to secret/myApp and secret/application available to it. When myApp has the dev profile enabled than properties written to all of the above paths would be available to it, with properties in the first path in the list taking priority over the others.

共享配置给所有应用(Sharing Configuration With All Applications)
基于文件的资源库(File Based Repositories)

With file-based (i.e. git, svn and native) repositories, resources with file names in application* are shared between all client applications (so application.properties, application.yml, application-*.properties etc.). You can use resources with these file names to configure global defaults and have them overridden by application-specific files as necessary.

在基于文件的资源库中(i.e. git, svn and native), 这样的文件名 application* 命名的资源在所有的客户端都是共享的(如 application.properties, application.yml, application-*.properties,etc.).

The #_property_overrides[property overrides] feature can also be used for setting global defaults, and with placeholders applications are allowed to override them locally.

你可以使用这种文件名的资源进行全局默认值配置,当需要覆盖默认值时,可以使用指定application名称的文件名. 也可以使用#_property_overrides[property overrides]方式来设置全局默认值,通过带占位符的applications来进行本地化覆盖.

With the "native" profile (local file system backend) it is recommended that you use an explicit search location that isn’t part of the server’s own configuration. Otherwise the application* resources in the default search locations are removed because they are part of the server.
对于"native" profile(本地文件系统作为后端),推荐你使用指定查询路径进行配置，这个路径不是服务器自己配置的一部分.除此之外,默认查询路径中的application*资源都会被删除,因为这些资源都是服务器的一部分.
Vault服务端(Vault Server)

When using Vault as a backend you can share configuration with all applications by placing configuration in html5/application. For example, if you run this Vault command

$ vault write secret/application foo=bar baz=bam
All applications using the config server will have the properties foo and baz available to them.

属性覆盖(Property Overrides)
The Config Server has an "overrides" feature that allows the operator to provide configuration properties to all applications that cannot be accidentally changed by the application using the normal Spring Boot hooks. To declare overrides just add a map of name-value pairs to spring.cloud.config.server.overrides. For example

Config Server有一个“覆盖”功能,允许开发人员为所有的应用程序提供配置属性,这些配置属性不会被Spring Boot应用程序进行错误设置.定义覆盖只需要为“spring.cloud.config.server.overrides”添加一个Map类型的name-value对. 例如:

spring:
  cloud:
    config:
      server:
        overrides:
          foo: bar
will cause all applications that are config clients to read foo=bar independent of their own configuration. (Of course an application can use the data in the Config Server in any way it likes, so overrides are not enforceable, but they do provide useful default behaviour if they are Spring Cloud Config clients.)

会使所有的配置客户端应用程序读取foo=bar到他们自己配置参数中. ( 如果这个应用程序是Spring Cloud Config 客户端，当然,应用程序可以按任何喜欢的方式 使用配置服务器中的数据,所以覆盖并不是强制的,但覆盖提供了非常有用的默认行为.)

Normal, Spring environment placeholders with "${}" can be escaped (and resolved on the client) by using backslash ("\") to escape the "$" or the "{", e.g. \${app.foo:bar} resolves to "bar" unless the app provides its own "app.foo". Note that in YAML you don’t need to escape the backslash itself, but in properties files you do, when you configure the overrides on the server.
一般情况下,Spring环境变量占位符"${}"可以通过使用反斜杠(\)来被转义(或者在客户端被正常处理) 如:"\${app.foo:bar}"会被处理成"bar",除非应用程序提供了自己的“app.foo”值.注意,当你在服务器上配置覆盖参数时,在YAML类型文件中,你不需要转义反斜杠本身,而是在properties文件中,你需要对他进行转义.
You can change the priority of all overrides in the client to be more like default values, allowing applications to supply their own values in environment variables or System properties, by setting the flag `

你可以改变客户端所有的覆盖参数的优先级,而使这些值更像默认值,在环境变量或系统属性中提供自己的参数值来修改这些值.

健康指示器(Health Indicator)
Config Server comes with a Health Indicator that checks if the configured EnvironmentRepository is working. By default it asks the EnvironmentRepository for an application named app, the default profile and the default label provided by the EnvironmentRepository implementation.

Config Server也提供了健康指示器,通过这个指示器能够检查已经配置的EnvironmentRepository 是否正常运行.默认情况下,指示器通过 EnvironmentRepository提供的app应用程序名称、default`profile和默认label来请求`EnvironmentRepository.你也可以自定义更多的applications、profiles和labels进行检查,e.g.

You can configure the Health Indicator to check more applications along with custom profiles and custom labels, e.g.

spring:
  cloud:
    config:
      server:
        health:
          repositories:
            myservice:
              label: mylabel
            myservice-dev:
              name: myservice
              profiles: development
You can disable the Health Indicator by setting spring.cloud.config.server.health.enabled=false.

你也可以通过设置 spring.cloud.config.server.health.enabled=false参数来禁用健康指示器.

安全(Security)
You are free to secure your Config Server in any way that makes sense to you (from physical network security to OAuth2 bearer tokens), and Spring Security and Spring Boot make it easy to do pretty much anything.

你可以自由选择任何你觉得合理的方式来保护你的Config Server(从物理网络安全到OAuth2 令牌),同时使用Spring Security和Spring Boot 能使你做更多其他有用的事情.

To use the default Spring Boot configured HTTP Basic security, just include Spring Security on the classpath (e.g. through spring-boot-starter-security). The default is a username of "user" and a randomly generated password, which isn’t going to be very useful in practice, so we recommend you configure the password (via security.user.password) and encrypt it (see below for instructions on how to do that).

为了使用默认的Spring Boot HTTP Basic 安全,只需要把Spring Security 增加到classpath中(如通过spring-boot-starter-security).默认的用户名是"user",对应的会生成一个随机密码,这种情况在实际使用中并没有意义,我们建议你配置一个密码(通过 security.user.password属性进行配置)并对这个密码进行加密(下面章节有关于怎么加密的步骤).

加密与解密(Encryption and Decryption)
Prerequisites: to use the encryption and decryption features you need the full-strength JCE installed in your JVM (it’s not there by default). You can download the "Java Cryptography Extension (JCE) Unlimited Strength Jurisdiction Policy Files" from Oracle, and follow instructions for installation (essentially replace the 2 policy files in the JRE lib/security directory with the ones that you downloaded).
Prerequisites: 为了加密和解密功能,你需要在JVM中安装full-strength JCE(默认并没有安装).你可以在Oracle站点上下载"Java Cryptography Extension (JCE) Unlimited Strength Jurisdiction Policy Files",然后根据提示安装(一般来说就是使用你下载的2个策略文件替换JRE lib/security目录下的相同名称文件).
If the remote property sources contain encrypted content (values starting with {cipher}) they will be decrypted before sending to clients over HTTP. The main advantage of this set up is that the property values don’t have to be in plain text when they are "at rest" (e.g. in a git repository). If a value cannot be decrypted it is removed from the property source and an additional property is added with the same key, but prefixed with "invalid." and a value that means "not applicable" (usually "<n/a>"). This is largely to prevent cipher text being used as a password and accidentally leaking.

如果远程属性包含加密内容(以{cipher}开头),这些值将在通过HTTP传递到客户端之前被解密.这样设置的最大优点在于属性值不使用时(如在git库中时)，没有必要以明文的方式显示.如果属性值不能被解密,那么这个值将会被删除,之后增加一个和原来key一样的属性值,不过，key的前缀是"invalid."，值是"not applicable"(通常是"<n/a>").这种设计方法很大程度上是为了阻止使用密文当作密码和意外密文泄露.

If you are setting up a remote config repository for config client applications it might contain an application.yml like this, for instance:

如果你想为config client设置远程config库时使用密文，可能包含的文件application.yml像这样，举例:

application.yml
spring:
  datasource:
    username: dbuser
    password: '{cipher}FKSAJDFGYOS8F7GLHAKERGFHLSAJ'
Encrypted values in a .properties file must not be wrapped in quotes, otherwise the value will not be decrypted:

如果是.properties文件,加密的属性值一定不能用引号扩起来，不然值不能解密:

application.properties
spring.datasource.username: dbuser
spring.datasource.password: {cipher}FKSAJDFGYOS8F7GLHAKERGFHLSAJ
You can safely push this plain text to a shared git repository and the secret password is protected.

你可以设置安全的把纯文本推送到共享的git库中,密码也可以很好的得到保护.

The server also exposes /encrypt and /decrypt endpoints (on the assumption that these will be secured and only accessed by authorized agents). If you are editing a remote config file you can use the Config Server to encrypt values by POSTing to the /encrypt endpoint, e.g.

服务器也会可以暴露 /encrypt和/decrypt接口(假设这些会是安全的,只能被授权的客户端访问).如果你想编辑远程配置文件，你可以请求Config Server的/encrypt接口来加密,e.g.

$ curl localhost:8888/encrypt -d mysecret
682bc583f4641835fa2db009355293665d2647dade3375c0ee201de2a49f7bda
The inverse operation is also available via /decrypt (provided the server is configured with a symmetric key or a full key pair):

逆操作也可以通过/decrypt 来完成(服务器提供配置一个对称密钥或密钥对来实现此功能):

$ curl localhost:8888/decrypt -d 682bc583f4641835fa2db009355293665d2647dade3375c0ee201de2a49f7bda
mysecret
If you are testing like this with curl, then use --data-urlencode (instead of -d) or set an explicit Content-Type: text/plain to make sure curl encodes the data correctly when there are special characters ('+' is particularly tricky).
如果你想使用curl来测试,当有特殊字符时('+'尤其为最棘手的特殊字符),你可以使用--data-urlencode (取代 -d)或 明确指定Content-Type:text/plain 参数来对这些数据正确编码.
Take the encrypted value and add the {cipher} prefix before you put it in the YAML or properties file, and before you commit and push it to a remote, potentially insecure store.

你加密的值增加{cipher}前缀后,放到YAML或properties文件之前，或者在递交或推送到远程服务器之前,这些加密的值都面临着潜在的不安全存储风险.

The /encrypt and /decrypt endpoints also both accept paths of the form /*/{name}/{profiles} which can be used to control cryptography per application (name) and profile when clients call into the main Environment resource.

当客户端程序对主环境资源不能确定时,/encrypt 和/decrypt接口也接受带有路径形式的请求 /*/{name}/{profiles},这样可以针对每个application(name)和profile进行详细的控制加解密.

to control the cryptography in this granular way you must also provide a @Bean of type TextEncryptorLocator that creates a different encryptor per name and profiles. The one that is provided by default does not do this (so all encryptions use the same key).
如果以这种细粒度的方式来控制加解密，你必须提供一个TextEncryptorLocator类型的@Bean,使用这个Bean 可以为每个名称或profile创建不同的加密方法.默认提供的并没有这个功能(所有加密使用相同的密钥).
The spring command line client (with Spring Cloud CLI extensions installed) can also be used to encrypt and decrypt, e.g.

spring命令行客户端(和 Spring Cloud CLI扩展一起安装)也可以用于加密和解密, e.g.

$ spring encrypt mysecret --key foo
682bc583f4641835fa2db009355293665d2647dade3375c0ee201de2a49f7bda
$ spring decrypt --key foo 682bc583f4641835fa2db009355293665d2647dade3375c0ee201de2a49f7bda
mysecret
To use a key in a file (e.g. an RSA public key for encryption) prepend the key value with "@" and provide the file path, e.g.

为了使用文件中的密钥 (如使用 RSA 公钥加密),需要在提供的文件前增加一个"@"符号,e.g.

$ spring encrypt mysecret --key @${HOME}/.ssh/id_rsa.pub
AQAjPgt3eFZQXwt8tsHAVv/QHiY5sI2dRcR+...
The key argument is mandatory (despite having a -- prefix).

key 参数是必填的(尽管有--前缀).

密钥管理(Key Management)
The Config Server can use a symmetric (shared) key or an asymmetric one (RSA key pair). The asymmetric choice is superior in terms of security, but it is often more convenient to use a symmetric key since it is just a single property value to configure.

Config Server 可以使用对称(共享)密钥或者非对称密钥(RSA密钥对).就安全性来讲,非对称密钥是优先选择,但是，选择对称密钥进行加解密处理显得非常方便，因为,只需要简单的配置一个属性值.

To configure a symmetric key you just need to set encrypt.key to a secret String (or use an enviroment variable ENCRYPT_KEY to keep it out of plain text configuration files).

为了配置对称密钥,你只需要把encrypt.key设置成你的密码字符串(或者使用环境变量ENCRYPT_KEY进行配置,这样可以让密码不在明文配置文件中显示).

To configure an asymmetric key you can either set the key as a PEM-encoded text value (in encrypt.key), or via a keystore (e.g. as created by the keytool utility that comes with the JDK). The keystore properties are encrypt.keyStore.* with * equal to

为了配置非对称密钥，你可以使用PEM编码格式的文本字符串(对encrypt.key配置项设置值),或者使用keystore(例如,可以使用JDK携带的keytool工具创建).keystore的配置属性是encrypt.keyStore.*,*可以是

location (a Resource location),

password (to unlock the keystore) and

alias (to identify which key in the store is to be used).

location ( Resource 位置),

password (用来打开keystore) and

alias (确定使用keystore 中的那个密钥).

The encryption is done with the public key, and a private key is needed for decryption. Thus in principle you can configure only the public key in the server if you only want to do encryption (and are prepared to decrypt the values yourself locally with the private key). In practice you might not want to do that because it spreads the key management process around all the clients, instead of concentrating it in the server. On the other hand it’s a useful option if your config server really is relatively insecure and only a handful of clients need the encrypted properties.

加密使用公钥完成,私钥用来解密.原则上，如果你只想加密，服务器上只配置公钥(用私钥在本地自行解密).实际中,你可能不想这样做,原因是这种方式会把密钥管理过程让所有的客户端都知道，而不是把焦点聚集在服务器上.从另外一个方面讲,一个非常有益的建议是如果你的配置服务器 相对不安全,只能有少数客户端需要加密配置属性.

创建一个测试密钥库(Creating a Key Store for Testing)
To create a keystore for testing you can do something like this:

为了创建用于测试的keystore,你可以按照以下步骤来做:

$ keytool -genkeypair -alias mytestkey -keyalg RSA \
  -dname "CN=Web Server,OU=Unit,O=Organization,L=City,S=State,C=US" \
  -keypass changeme -keystore server.jks -storepass letmein
Put the server.jks file in the classpath (for instance) and then in your application.yml for the Config Server:

把 server.jks文件放到你的classpath中(配置服务器的系统中),然后再application.yml文件中对Config Server 进行配置:

encrypt:
  keyStore:
    location: classpath:/server.jks
    password: letmein
    alias: mytestkey
    secret: changeme
使用多密钥和循环密钥(Using Multiple Keys and Key Rotation)
In addition to the {cipher} prefix in encrypted property values, the Config Server looks for {name:value} prefixes (zero or many) before the start of the (Base64 encoded) cipher text. The keys are passed to a TextEncryptorLocator which can do whatever logic it needs to locate a TextEncryptor for the cipher. If you have configured a keystore (encrypt.keystore.location) the default locator will look for keys in the store with aliases as supplied by the "key" prefix, i.e. with a cipher text like this:

除了在加密属性前增加{cipher}前缀外,在开始使用密文前,Config Server也查找{name:value}这种前缀的属性(0次或多次).系统会把密钥传递给可以做任何逻辑处理的TextEncryptorLocator类,然后使用该类定位到一个用于加密的TextEncryptor类.如果你配置了一个keystore(通过encrypt.keystore.location配置),默认的定位器将定位密钥库中的密钥,定位时使用"key" 前缀定义的别名.举例,配置的密钥文本如下:

foo:
  bar: `{cipher}{key:testkey}...`
the locator will look for a key named "testkey". A secret can also be supplied via a {secret:…​} value in the prefix, but if it is not the default is to use the keystore password (which is what you get when you build a keytore and don’t specify a secret). If you do supply a secret it is recommended that you also encrypt the secrets using a custom SecretLocator.

定位器将查找一个名字为"testkey"的密钥.密码也可以直接通过{secret:…​}这种方式来指定，但是这种方式不是默认使用keystore密码的方式(目的是为了让你明白当使用keystore时，不要特定指定密码).如果你提供了一个密码,建议你自定义一个SecretLocator对密码也进行加密.

Key rotation is hardly ever necessary on cryptographic grounds if the keys are only being used to encrypt a few bytes of configuration data (i.e. they are not being used elsewhere), but occasionally you might need to change the keys if there is a security breach for instance. In that case all the clients would need to change their source config files (e.g. in git) and use a new {key:…​} prefix in all the ciphers, checking beforehand of course that the key alias is available in the Config Server keystore.

在整个加解密使用过程中,如果只使用密钥用来加密一些配置属性值,密钥更换几乎不需要(密钥不需要在其他系统使用的情况)，但是在某些情况下如安全信息泄露,可能需要更换密钥.在这种情况下,所有的客户端都需要更改他们连接配置属性源文件(e.g.如在git中的文件)，还需要使用新的{key:…​}前缀加到密文信息中,同时检查在Config Server keystore 中密钥的别名是否可用.

the {name:value} prefixes can also be added to plaintext posted to the /encrypt endpoint, if you want to let the Config Server handle all encryption as well as decryption.
如果你想让 Config Server处理所有的加密和解密,{name:value}前缀也可以放到明文前面,然后通过/encrypt接口进行加密.
加密属性服务(Serving Encrypted Properties)
Sometimes you want the clients to decrypt the configuration locally, instead of doing it in the server. In that case you can still have /encrypt and /decrypt endpoints (if you provide the encrypt.* configuration to locate a key), but you need to explicitly switch off the decryption of outgoing properties using spring.cloud.config.server.encrypt.enabled=false. If you don’t care about the endpoints, then it should work if you configure neither the key nor the enabled flag.

有些时候,你可能想让客户端在本地解密配置文件,而不是在服务器上解密.在这种情况下,你依然可以使用/encrypt 和/decrypt接口(如果你配置了 encrypt.*来定位密钥),但是，你需要明确的使用spring.cloud.config.server.encrypt.enabled=false来关闭解密属性功能.如果你不想使用这些接口,即使没有配置密钥或也没有启用该功能，系统还是能正常运行.

可替换格式服务(Serving Alternative Formats)
The default JSON format from the environment endpoints is perfect for consumption by Spring applications because it maps directly onto the Environment abstraction. If you prefer you can consume the same data as YAML or Java properties by adding a suffix to the resource path (".yml", ".yaml" or ".properties"). This can be useful for consumption by applications that do not care about the structure of the JSON endpoints, or the extra metadata they provide, for example an application that is not using Spring might benefit from the simplicity of this approach.

The YAML and properties representations have an additional flag (provided as a boolean query parameter resolvePlaceholders) to signal that placeholders in the source documents, in the standard Spring ${…​} form, should be resolved in the output where possible before rendering. This is a useful feature for consumers that don’t know about the Spring placeholder conventions.

there are limitations in using the YAML or properties formats, mainly in relation to the loss of metadata. The JSON is structured as an ordered list of property sources, for example, with names that correlate with the source. The YAML and properties forms are coalesced into a single map, even if the origin of the values has multiple sources, and the names of the original source files are lost. The YAML representation is not necessarily a faithful representation of the YAML source in a backing repository either: it is constructed from a list of flat property sources, and assumptions have to be made about the form of the keys.
文本解释服务(Serving Plain Text)
Instead of using the Environment abstraction (or one of the alternative representations of it in YAML or properties format) your applications might need generic plain text configuration files, tailored to their environment. The Config Server provides these through an additional endpoint at /{name}/{profile}/{label}/{path} where "name", "profile" and "label" have the same meaning as the regular environment endpoint, but "path" is a file name (e.g. log.xml). The source files for this endpoint are located in the same way as for the environment endpoints: the same search path is used as for properties or YAML files, but instead of aggregating all matching resources, only the first one to match is returned.

After a resource is located, placeholders in the normal format (${…​}) are resolved using the effective Environment for the application name, profile and label supplied. In this way the resource endpoint is tightly integrated with the environment endpoints. Example, if you have this layout for a GIT (or SVN) repository:

application.yml
nginx.conf
where nginx.conf looks like this:

server {
    listen              80;
    server_name         ${nginx.server.name};
}
and application.yml like this:

nginx:
  server:
    name: example.com
---
spring:
  profiles: development
nginx:
  server:
    name: develop.com
then the /foo/default/master/nginx.conf resource looks like this:

server {
    listen              80;
    server_name         example.com;
}
and /foo/development/master/nginx.conf like this:

server {
    listen              80;
    server_name         develop.com;
}
just like the source files for environment configuration, the "profile" is used to resolve the file name, so if you want a profile-specific file then /*/development/*/logback.xml will be resolved by a file called logback-development.xml (in preference to logback.xml).
嵌入配置服务器(Embedding the Config Server)
The Config Server runs best as a standalone application, but if you need to you can embed it in another application. Just use the @EnableConfigServer annotation. An optional property that can be useful in this case is spring.cloud.config.server.bootstrap which is a flag to indicate that the server should configure itself from its own remote repository. The flag is off by default because it can delay startup, but when embedded in another application it makes sense to initialize the same way as any other application.

It should be obvious, but remember that if you use the bootstrap flag the config server will need to have its name and repository URI configured in bootstrap.yml.
To change the location of the server endpoints you can (optionally) set spring.cloud.config.server.prefix, e.g. "/config", to serve the resources under a prefix. The prefix should start but not end with a "/". It is applied to the @RequestMappings in the Config Server (i.e. underneath the Spring Boot prefixes server.servletPath and server.contextPath).

If you want to read the configuration for an application directly from the backend repository (instead of from the config server) that’s basically an embedded config server with no endpoints. You can switch off the endpoints entirely if you don’t use the @EnableConfigServer annotation (just set spring.cloud.config.server.bootstrap=true).

推送通知和总线(Push Notifications and Spring Cloud Bus)
Many source code repository providers (like Github, Gitlab or Bitbucket for instance) will notify you of changes in a repository through a webhook. You can configure the webhook via the provider’s user interface as a URL and a set of events in which you are interested. For instance Github will POST to the webhook with a JSON body containing a list of commits, and a header "X-Github-Event" equal to "push". If you add a dependency on the spring-cloud-config-monitor library and activate the Spring Cloud Bus in your Config Server, then a "/monitor" endpoint is enabled.

When the webhook is activated the Config Server will send a RefreshRemoteApplicationEvent targeted at the applications it thinks might have changed. The change detection can be strategized, but by default it just looks for changes in files that match the application name (e.g. "foo.properties" is targeted at the "foo" application, and "application.properties" is targeted at all applications). The strategy if you want to override the behaviour is PropertyPathNotificationExtractor which accepts the request headers and body as parameters and returns a list of file paths that changed.

The default configuration works out of the box with Github, Gitlab or Bitbucket. In addition to the JSON notifications from Github, Gitlab or Bitbucket you can trigger a change notification by POSTing to "/monitor" with a form-encoded body parameters path={name}. This will broadcast to applications matching the "{name}" pattern (can contain wildcards).

the RefreshRemoteApplicationEvent will only be transmitted if the spring-cloud-bus is activated in the Config Server and in the client application.
the default configuration also detects filesystem changes in local git repositories (the webhook is not used in that case but as soon as you edit a config file a refresh will be broadcast).
客户端配置(Spring Cloud Config Client)
A Spring Boot application can take immediate advantage of the Spring Config Server (or other external property sources provided by the application developer), and it will also pick up some additional useful features related to Environment change events.

配置第一次引导(Config First Bootstrap)
This is the default behaviour for any application which has the Spring Cloud Config Client on the classpath. When a config client starts up it binds to the Config Server (via the bootstrap configuration property spring.cloud.config.uri) and initializes Spring Environment with remote property sources.

The net result of this is that all client apps that want to consume the Config Server need a bootstrap.yml (or an environment variable) with the server address in spring.cloud.config.uri (defaults to "http://localhost:8888").

发现第一次引导(Discovery First Bootstrap)
If you are using a `DiscoveryClient implementation, such as Spring Cloud Netflix and Eureka Service Discovery or Spring Cloud Consul (Spring Cloud Zookeeper does not support this yet), then you can have the Config Server register with the Discovery Service if you want to, but in the default "Config First" mode, clients won’t be able to take advantage of the registration.

If you prefer to use DiscoveryClient to locate the Config Server, you can do that by setting spring.cloud.config.discovery.enabled=true (default "false"). The net result of that is that client apps all need a bootstrap.yml (or an environment variable) with the appropriate discovery configuration. For example, with Spring Cloud Netflix, you need to define the Eureka server address, e.g. in eureka.client.serviceUrl.defaultZone. The price for using this option is an extra network round trip on start up to locate the service registration. The benefit is that the Config Server can change its co-ordinates, as long as the Discovery Service is a fixed point. The default service id is "configserver" but you can change that on the client with spring.cloud.config.discovery.serviceId (and on the server in the usual way for a service, e.g. by setting spring.application.name).

The discovery client implementations all support some kind of metadata map (e.g. for Eureka we have eureka.instance.metadataMap). Some additional properties of the Config Server may need to be configured in its service registration metadata so that clients can connect correctly. If the Config Server is secured with HTTP Basic you can configure the credentials as "username" and "password". And if the Config Server has a context path you can set "configPath". Example, for a Config Server that is a Eureka client:

bootstrap.yml
eureka:
  instance:
    ...
    metadataMap:
      user: osufhalskjrtl
      password: lviuhlszvaorhvlo5847
      configPath: /config
Config Client Fail Fast
In some cases, it may be desirable to fail startup of a service if it cannot connect to the Config Server. If this is the desired behavior, set the bootstrap configuration property spring.cloud.config.failFast=true and the client will halt with an Exception.

配置客户端重试(Config Client Retry)
If you expect that the config server may occasionally be unavailable when your app starts, you can ask it to keep trying after a failure. First you need to set spring.cloud.config.failFast=true, and then you need to add spring-retry and spring-boot-starter-aop to your classpath. The default behaviour is to retry 6 times with an initial backoff interval of 1000ms and an exponential multiplier of 1.1 for subsequent backoffs. You can configure these properties (and others) using spring.cloud.config.retry.* configuration properties.

To take full control of the retry add a @Bean of type RetryOperationsInterceptor with id "configServerRetryInterceptor". Spring Retry has a RetryInterceptorBuilder that makes it easy to create one.
定位远程配置资源(Locating Remote Configuration Resources)
The Config Service serves property sources from /{name}/{profile}/{label}, where the default bindings in the client app are

"name" = ${spring.application.name}

"profile" = ${spring.profiles.active} (actually Environment.getActiveProfiles())

"label" = "master"

All of them can be overridden by setting spring.cloud.config.* (where * is "name", "profile" or "label"). The "label" is useful for rolling back to previous versions of configuration; with the default Config Server implementation it can be a git label, branch name or commit id. Label can also be provided as a comma-separated list, in which case the items in the list are tried on-by-one until one succeeds. This can be useful when working on a feature branch, for instance, when you might want to align the config label with your branch, but make it optional (e.g. spring.cloud.config.label=myfeature,develop).

安全(Security)
If you use HTTP Basic security on the server then clients just need to know the password (and username if it isn’t the default). You can do that via the config server URI, or via separate username and password properties, e.g.

bootstrap.yml
spring:
  cloud:
    config:
     uri: https://user:secret@myconfig.mycompany.com
or

bootstrap.yml
spring:
  cloud:
    config:
     uri: https://myconfig.mycompany.com
     username: user
     password: secret
The spring.cloud.config.password and spring.cloud.config.username values override anything that is provided in the URI.

If you deploy your apps on Cloud Foundry then the best way to provide the password is through service credentials, e.g. in the URI, since then it doesn’t even need to be in a config file. An example which works locally and for a user-provided service on Cloud Foundry named "configserver":

bootstrap.yml
spring:
  cloud:
    config:
     uri: ${vcap.services.configserver.credentials.uri:http://user:password@localhost:8888}
If you use another form of security you might need to provide a RestTemplate to the ConfigServicePropertySourceLocator (e.g. by grabbing it in the bootstrap context and injecting one).

Vault
When using Vault as a backend to your config server the client will need to supply a token for the server to retrieve values from Vault. This token can be provided within the client by setting spring.cloud.config.token in bootstrap.yml.

bootstrap.yml
spring:
  cloud:
    config:
      token: YourVaultToken
Vault
在Vault中嵌入密钥(Nested Keys In Vault)
Vault supports the ability to nest keys in a value stored in Vault. For example

echo -n '{"appA": {"secret": "appAsecret"}, "bar": "baz"}' | vault write secret/myapp -

This command will write a JSON object to your Vault. To access these values in Spring you would use the traditional dot(.) annotation. For example

@Value("${appA.secret}")
String name = "World";
The above code would set the name variable to appAsecret.

Last updated 2016-08-01 20:40:22
