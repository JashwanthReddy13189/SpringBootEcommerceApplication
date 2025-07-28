#!/bin/bash

cd ../../

#Build all services jar files
cd config-service && ./mvnw clean package -DskipTests && cd ..
cd eureka-service && ./mvnw clean package -DskipTests && cd ..
cd api-gateway && ./mvnw clean package -DskipTests && cd ..
cd user-service && ./mvnw clean package -DskipTests && cd ..
cd order-service && ./mvnw clean package -DskipTests && cd ..
cd product-service && ./mvnw clean package -DskipTests && cd ..
cd notification-service && ./mvnw clean package -DskipTests && cd ..