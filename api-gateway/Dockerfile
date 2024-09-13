FROM maven:3.9-eclipse-temurin-21-jammy AS build
WORKDIR /opt/api-gateway
COPY pom.xml .
RUN mvn dependency:go-offline
COPY ./src ./src
RUN mvn clean package -Dmaven.test.skip=true


FROM eclipse-temurin:21
WORKDIR /opt/api-gateway
COPY --from=build /opt/api-gateway/target/api-gateway-0.0.1-SNAPSHOT.jar ./api-gateway.jar
CMD ["java", "-jar", "api-gateway.jar"]