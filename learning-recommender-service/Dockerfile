FROM maven:3.9-eclipse-temurin-21-jammy AS build
WORKDIR /opt/learning-recommender-service
COPY pom.xml .
RUN mvn dependency:go-offline
COPY ./src ./src
RUN mvn clean package -Dmaven.test.skip=true


FROM eclipse-temurin:21
WORKDIR /opt/user-service
COPY --from=build /opt/learning-recommender-service/target/learning-recommender-service-0.0.1-SNAPSHOT.jar ./learning-recommender-service.jar
CMD ["java", "-jar", "learning-recommender-service.jar"]