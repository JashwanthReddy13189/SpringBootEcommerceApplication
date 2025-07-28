#!/bin/sh
echo "Waiting for dependencies to be ready ..."
sleep 25
echo "Starting order-service"
exec java -jar order-service.jar