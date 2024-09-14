FROM maven:3.9-eclipse-temurin-21-jammy AS build
WORKDIR /opt/course-service
COPY pom.xml .
RUN mvn dependency:go-offline
COPY ./src ./src
RUN mvn clean package -Dmaven.test.skip=true


FROM eclipse-temurin:21
WORKDIR /opt/course-service
COPY --from=build /opt/course-service/target/course-service-0.0.1-SNAPSHOT.jar ./course-service.jar
CMD ["java", "-jar", "course-service.jar"]