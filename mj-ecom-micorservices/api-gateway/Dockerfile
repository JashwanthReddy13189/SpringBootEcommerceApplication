FROM eclipse-temurin:17-jdk-focal
LABEL authors="NoMercy"
WORKDIR /app
COPY target/*.jar api-gateway.jar
COPY entrypoint.sh /app/entrypoint.sh
RUN chmod +x /app/entrypoint.sh
EXPOSE 8085
ENTRYPOINT ["/app/entrypoint.sh"]