compile("org.springframework.cloud:spring-cloud-starter-config:${springBootVersion}")
compile("org.springframework.cloud:spring-cloud-config-server:${springBootVersion}")
// spring cloud config-server依赖不能同时注入，否则client无法注入