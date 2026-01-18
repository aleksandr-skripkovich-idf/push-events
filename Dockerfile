FROM maven:3.9.4-eclipse-temurin-21 AS build
ARG SERVICE_NAME
WORKDIR /app
COPY pom.xml .
COPY push-events-api ./push-events-api
COPY generator-service ./generator-service
COPY registry-service ./registry-service
RUN mvn clean package -DskipTests -pl ${SERVICE_NAME} -am

FROM eclipse-temurin:21-jre-jammy
ARG SERVICE_NAME
WORKDIR /app
COPY --from=build /app/${SERVICE_NAME}/target/${SERVICE_NAME}-*.jar app.jar
EXPOSE 8888
ENTRYPOINT ["java", "-jar", "app.jar"]
