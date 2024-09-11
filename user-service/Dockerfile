FROM maven:3.9-eclipse-temurin-21-jammy AS build
WORKDIR /opt/user-service
COPY pom.xml .
RUN mvn dependency:go-offline
COPY ./src ./src
RUN mvn clean package -Dmaven.test.skip=true


FROM eclipse-temurin:21
WORKDIR /opt/user-service
COPY --from=build /opt/user-service/target/user-service-0.0.1-SNAPSHOT.jar ./user-service.jar
EXPOSE 8081
CMD ["java", "-jar", "user-service.jar"]