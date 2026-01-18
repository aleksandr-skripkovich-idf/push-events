FROM maven:3.9.4-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY generator-service ./generator-service
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=build /app/generator-service/target/generator-service-*.jar generatorApp.jar
EXPOSE 8888
ENTRYPOINT ["java", "-jar", "generatorApp.jar"]
