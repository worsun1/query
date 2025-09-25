# syntax=docker/dockerfile:1

FROM eclipse-temurin:17-jre-alpine AS runtime

ARG JAR_FILE=target/query-0.0.1-SNAPSHOT.jar

WORKDIR /opt/app

COPY ${JAR_FILE} app.jar

EXPOSE 8080 9090

ENTRYPOINT ["java", "-jar", "app.jar"]
