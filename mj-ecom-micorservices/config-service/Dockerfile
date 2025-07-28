FROM eclipse-temurin:17-jdk-focal
LABEL authors="NoMercy"
WORKDIR /app
COPY target/*.jar config-service.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar" , "config-service.jar"]