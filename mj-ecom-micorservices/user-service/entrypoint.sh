#!/bin/sh
echo "Waiting for dependencies to be ready ..."
sleep 25
echo "Starting user-service"
exec java -jar user-service.jar