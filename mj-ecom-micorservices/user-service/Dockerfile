FROM eclipse-temurin:17-jdk-focal
LABEL authors="NoMercy"
WORKDIR /app
COPY target/*.jar user-service.jar
#COPY entrypoint.sh /app/entrypoint.sh
#RUN chmod +x /app/entrypoint.sh
EXPOSE 8082
ENTRYPOINT ["java", "-jar" , "user-service.jar"]