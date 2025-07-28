#!/bin/sh
echo "Waiting for dependencies to be ready ..."
sleep 10
echo "Starting eureka service"
exec java -jar eureka-service.jar