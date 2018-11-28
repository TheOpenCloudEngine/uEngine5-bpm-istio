FROM openjdk:8u111-jdk-alpine
VOLUME /tmp
ADD /target/*.jar app.jar
ARG CI_PROJECT_NAME
ARG CI_COMMIT_SHA
ENV CI_PROJECT_NAME=$CI_PROJECT_NAME
ENV CI_COMMIT_SHA=$CI_COMMIT_SHA
EXPOSE 8080
ENTRYPOINT ["java","-Xmx4480M","-Djava.security.egd=file:/dev/./urandom","-Dserver.port=9095","-jar","/app.jar"]