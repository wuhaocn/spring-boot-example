FROM frolvlad/alpine-oraclejdk8:slim
MAINTAINER wuhaocn "wuhaocn@126.com"
VOLUME /tmp
ADD spring-cloud-starter-1.0-SNAPSHOT.jar app.jar
CMD 'touch /app.jar'
ENV JAVA_OPTS=""
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app.jar" ]
