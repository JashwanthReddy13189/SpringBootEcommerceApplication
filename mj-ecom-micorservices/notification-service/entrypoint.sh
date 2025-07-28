#!/bin/sh
echo "Waiting for dependencies to be ready ..."
sleep 25
echo "Starting notification-service"
exec java -jar notification-service.jar