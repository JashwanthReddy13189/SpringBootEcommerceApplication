#!/bin/bash

# Docker configuration to push latest image builds
DOCKER_USERNAME="jashwanthreddymr"
TAG="version-1.0"

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Array of services with their directory paths
declare -A SERVICES=(
    ["config-service"]="../../config-service"
    ["eureka-service"]="../../eureka-service"
    ["api-gateway"]="../../api-gateway"
    ["user-service"]="../../user-service"
    ["order-service"]="../../order-service"
    ["product-service"]="../../product-service"
    ["notification-service"]="../../notification-service"
)

# Function to print colored output
print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

# Function to check if Docker is running
check_docker() {
    print_status "Checking Docker status..."
    if ! docker info >/dev/null 2>&1; then
        print_error "Docker is not running. Please start Docker Desktop."
        exit 1
    fi
    print_success "Docker is running"
}

# Function to login to Docker Hub
docker_login() {
    print_status "Logging into Docker Hub..."
    if docker login; then
        print_success "Successfully logged into Docker Hub"
    else
        print_error "Failed to login to Docker Hub"
        exit 1
    fi
}

# Function to build and push a single service
build_and_push_service() {
    local service_name=$1
    local service_path=$2
    local image_name="${DOCKER_USERNAME}/${service_name}:${TAG}"

    print_status "Building $service_name..."

    # Check if service directory exists
    if [ ! -d "$service_path" ]; then
        print_error "Directory $service_path not found for $service_name"
        return 1
    fi

    # Check if Dockerfile exists
    if [ ! -f "$service_path/Dockerfile" ]; then
        print_error "Dockerfile not found in $service_path"
        return 1
    fi

    # Build the Docker image
    print_status "Building image: $image_name"
    if docker build -t "$image_name" "$service_path"; then
        print_success "Successfully built $image_name"

        # Push the image to Docker Hub
        print_status "Pushing $image_name to Docker Hub..."
        if docker push "$image_name"; then
            print_success "Successfully pushed $image_name"

            # Also tag and push as 'latest' if tag is not 'latest'
            if [ "$TAG" != "latest" ]; then
                local latest_image="${DOCKER_USERNAME}/${service_name}:latest"
                docker tag "$image_name" "$latest_image"
                docker push "$latest_image"
                print_success "Also pushed $latest_image"
            fi

            return 0
        else
            print_error "Failed to push $image_name"
            return 1
        fi
    else
        print_error "Failed to build $image_name"
        return 1
    fi
}

# Function to clean up old images (optional)
#cleanup_old_images() {
#    print_status "Cleaning up dangling images..."
#    docker image prune -f
#    print_success "Cleanup completed"
#}

# Function to show final summary of image builds
show_summary() {
    echo
    print_status "============ BUILD SUMMARY ============"
    echo -e "${BLUE}Docker Hub Username:${NC} $DOCKER_USERNAME"
    echo -e "${BLUE}Tag:${NC} $TAG"
    echo -e "${BLUE}Total Services:${NC} ${#SERVICES[@]}"
    echo -e "${BLUE}Successful Builds:${NC} $successful_builds"
    echo -e "${BLUE}Failed Builds:${NC} $failed_builds"

    if [ $failed_builds -eq 0 ]; then
        print_success "🎉 All services built and pushed successfully!"
        echo
        print_status "You can now use these images in your docker-compose.yml:"
        for service in "${!SERVICES[@]}"; do
            echo "  - ${DOCKER_USERNAME}/${service}:${TAG}"
        done
    else
        print_warning "⚠️  Some services failed to build. Check the logs above."
    fi
    echo "========================================"
}

# Main execution
main() {
    echo -e "${BLUE}"
    echo "🐳 Docker Build & Push Script for Microservices"
    echo "================================================"
    echo -e "${NC}"

    # Validate configuration
    if [ "$DOCKER_USERNAME" = "your-dockerhub-username" ]; then
        print_error "Please update DOCKER_USERNAME in the script with your actual Docker Hub username"
        exit 1
    fi

    # Initialize counters
    successful_builds=0
    failed_builds=0

    # Check prerequisites
    check_docker

    # Ask for Docker Hub login
    echo
    read -p "Do you want to login to Docker Hub? (y/n): " -r
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        docker_login
    fi

    echo
    print_status "Starting build process for ${#SERVICES[@]} services..."
    echo

    # Build and push each service
    for service in "${!SERVICES[@]}"; do
        echo -e "${YELLOW}----------------------------------------${NC}"
        if build_and_push_service "$service" "${SERVICES[$service]}"; then
            ((successful_builds++))
        else
            ((failed_builds++))
        fi
        echo
    done

    # Optional cleanup
    echo
    read -p "Do you want to clean up dangling Docker images? (y/n): " -r
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        cleanup_old_images
    fi

    # Show summary
    show_summary
}

# Run the script
main "$@"