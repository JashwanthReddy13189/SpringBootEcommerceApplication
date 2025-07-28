#!/bin/sh
echo "Waiting for dependencies to be ready ..."
sleep 25
echo "Starting api-gateway"
exec java -jar api-gateway.jar