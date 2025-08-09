#!/bin/bash

# Navigate to project root
cd ../../

# Check if we're in the right directory
if [ ! -d "config-service" ]; then
    echo "❌ Error: Cannot find config-service directory. Check if you're in the right location."
    exit 1
fi

services=("config-service" "eureka-service" "api-gateway" "user-service" "order-service" "product-service" "notification-service")

for service in "${services[@]}"; do
    echo "🔨 Building $service..."
    if [ -d "$service" ]; then
        cd "$service"
        mvn clean package -DskipTests
        if [ $? -eq 0 ]; then
            echo "✅ $service built successfully"
        else
            echo "❌ Failed to build $service"
            exit 1
        fi
        cd ..
    else
        echo "❌ Directory $service not found"
        exit 1
    fi
done

echo "🎉 All services built successfully!"