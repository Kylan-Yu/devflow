FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /workspace

COPY pom.xml .
COPY src ./src
RUN mvn -q -DskipTests package

FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /workspace/target/devflow-api-0.1.0-SNAPSHOT.jar /app/devflow-api.jar
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/devflow-api.jar"]
