#!/bin/sh
echo "Waiting for dependencies to be ready ..."
sleep 25
echo "Starting product-service"
exec java -jar product-service.jar