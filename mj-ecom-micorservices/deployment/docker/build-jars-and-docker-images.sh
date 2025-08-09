#!/bin/bash

# =============================================================================
# Unified Microservices Build & Deploy Script
# This script builds JAR files and Docker images, then pushes to Docker Hub
# =============================================================================

# Configuration
DOCKER_USERNAME="jashwanthreddymr"
TAG="version-1.0"

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

# Array of services (order matters - config and eureka should be first)
SERVICES=("config-service" "eureka-service" "api-gateway" "user-service" "order-service" "product-service" "notification-service")

# Initialize counters
successful_jar_builds=0
failed_jar_builds=0
successful_docker_builds=0
failed_docker_builds=0

# Function to print colored output
print_header() {
    echo -e "${CYAN}============================================${NC}"
    echo -e "${CYAN}$1${NC}"
    echo -e "${CYAN}============================================${NC}"
}

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

# Function to check prerequisites
check_prerequisites() {
    print_header "CHECKING PREREQUISITES"

    cd ../../

    # Check if we're in the right directory
    if [ ! -d "config-service" ]; then
        print_error "Cannot find config-service directory. Please run this script from the project root."
        exit 1
    fi

    # Check if Maven is installed
    if ! command -v mvn &> /dev/null; then
        print_error "Maven is not installed or not in PATH"
        exit 1
    fi
    print_success "Maven is available"

    # Check if Docker is running
    if ! docker info >/dev/null 2>&1; then
        print_error "Docker is not running. Please start Docker Desktop."
        exit 1
    fi
    print_success "Docker is running"

    # Validate Docker username
    if [ "$DOCKER_USERNAME" = "your-dockerhub-username" ]; then
        print_error "Please update DOCKER_USERNAME in the script with your actual Docker Hub username"
        exit 1
    fi
    print_success "Configuration validated"
}

# Function to build JAR files
build_jars() {
    print_header "BUILDING JAR FILES"

    for service in "${SERVICES[@]}"; do
        echo -e "${YELLOW}----------------------------------------${NC}"
        print_status "Building JAR for $service..."

        if [ -d "$service" ]; then
            # shellcheck disable=SC2164
            cd "$service"

            # Clean and build with Maven
            if mvn clean package -DskipTests -q; then
                print_success "$service JAR built successfully"
                ((successful_jar_builds++))
            else
                print_error "Failed to build $service JAR"
                ((failed_jar_builds++))
                cd ..
                continue
            fi

            # shellcheck disable=SC2103
            cd ..
        else
            print_error "Directory $service not found"
            ((failed_jar_builds++))
        fi
    done

    echo
    print_status "JAR Build Summary: ✅ $successful_jar_builds successful, ❌ $failed_jar_builds failed"

    if [ $failed_jar_builds -gt 0 ]; then
        echo
        read -p "Some JAR builds failed. Do you want to continue with Docker builds? (y/n): " -r
        if [[ ! $REPLY =~ ^[Yy]$ ]]; then
            print_warning "Build process stopped by user"
            exit 1
        fi
    fi
}

# Function to login to Docker Hub
docker_login() {
    print_status "Logging into Docker Hub..."
    if docker login; then
        print_success "Successfully logged into Docker Hub"
        return 0
    else
        print_error "Failed to login to Docker Hub"
        return 1
    fi
}

# Function to build and push Docker images
build_docker_images() {
    print_header "BUILDING & PUSHING DOCKER IMAGES"

    # Ask for Docker Hub login
    echo
    read -p "Do you want to login to Docker Hub? (y/n): " -r
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        if ! docker_login; then
            print_warning "Continuing without Docker Hub login. Push operations may fail."
        fi
    fi

    echo
    print_status "Starting Docker build process for services with successful JAR builds..."

    for service in "${SERVICES[@]}"; do
        echo -e "${YELLOW}----------------------------------------${NC}"
        print_status "Processing $service..."

        # Skip if JAR build failed for this service
        # shellcheck disable=SC2144
        if [ ! -f "$service/target/"*.jar ]; then
            print_warning "Skipping $service - no JAR file found"
            continue
        fi

        # Check if Dockerfile exists
        if [ ! -f "$service/Dockerfile" ]; then
            print_error "Dockerfile not found in $service directory"
            ((failed_docker_builds++))
            continue
        fi

        local image_name="${DOCKER_USERNAME}/${service}:${TAG}"

        # Build Docker image
        print_status "Building Docker image: $image_name"
        if docker build -t "$image_name" "$service" -q; then
            print_success "Successfully built $image_name"

            # Push to Docker Hub
            print_status "Pushing $image_name to Docker Hub..."
            if docker push "$image_name" >/dev/null 2>&1; then
                print_success "Successfully pushed $image_name"

                # Also tag and push as 'latest' if tag is not 'latest'
                if [ "$TAG" != "latest" ]; then
                    local latest_image="${DOCKER_USERNAME}/${service}:latest"
                    docker tag "$image_name" "$latest_image"
                    if docker push "$latest_image" >/dev/null 2>&1; then
                        print_success "Also pushed $latest_image"
                    fi
                fi

                ((successful_docker_builds++))
            else
                print_error "Failed to push $image_name"
                ((failed_docker_builds++))
            fi
        else
            print_error "Failed to build Docker image for $service"
            ((failed_docker_builds++))
        fi

        echo
    done
}

# Function to clean up Docker resources
cleanup_docker() {
    echo
    read -p "Do you want to clean up dangling Docker images? (y/n): " -r
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        print_status "Cleaning up dangling images..."
        docker image prune -f >/dev/null 2>&1
        print_success "Docker cleanup completed"
    fi
}

# Function to show final summary
show_final_summary() {
    print_header "BUILD & DEPLOY SUMMARY"

    echo -e "${BLUE}Project:${NC} Spring Boot Microservices"
    echo -e "${BLUE}Docker Hub Username:${NC} $DOCKER_USERNAME"
    echo -e "${BLUE}Tag:${NC} $TAG"
    echo -e "${BLUE}Total Services:${NC} ${#SERVICES[@]}"
    echo
    echo -e "${BLUE}JAR Builds:${NC}"
    echo -e "  ✅ Successful: $successful_jar_builds"
    echo -e "  ❌ Failed: $failed_jar_builds"
    echo
    echo -e "${BLUE}Docker Builds:${NC}"
    echo -e "  ✅ Successful: $successful_docker_builds"
    echo -e "  ❌ Failed: $failed_docker_builds"
    echo

    if [ $failed_jar_builds -eq 0 ] && [ $failed_docker_builds -eq 0 ]; then
        print_success "🎉 All services built and deployed successfully!"
        echo
        print_status "Available Docker images:"
        for service in "${SERVICES[@]}"; do
            echo "  📦 ${DOCKER_USERNAME}/${service}:${TAG}"
            if [ "$TAG" != "latest" ]; then
                echo "  📦 ${DOCKER_USERNAME}/${service}:latest"
            fi
        done
    else
        print_warning "⚠️  Some builds failed. Check the logs above for details."
    fi

    echo -e "${CYAN}============================================${NC}"
}

# Function to show usage
show_usage() {
    echo "Usage: $0 [OPTIONS]"
    echo ""
    echo "Options:"
    echo "  -h, --help     Show this help message"
    echo "  --jars-only    Build only JAR files"
    echo "  --docker-only  Build only Docker images (assumes JARs exist)"
    echo "  --skip-tests   Skip Maven tests (default behavior)"
    echo ""
    echo "Examples:"
    echo "  $0                # Build JARs and Docker images"
    echo "  $0 --jars-only    # Build only JAR files"
    echo "  $0 --docker-only  # Build only Docker images"
}

# Main execution function
main() {
    # Parse command line arguments
    JARS_ONLY=false
    DOCKER_ONLY=false

    while [[ $# -gt 0 ]]; do
        case $1 in
            -h|--help)
                show_usage
                exit 0
                ;;
            --jars-only)
                JARS_ONLY=true
                shift
                ;;
            --docker-only)
                DOCKER_ONLY=true
                shift
                ;;
            --skip-tests)
                # Already default behavior
                shift
                ;;
            *)
                print_error "Unknown option: $1"
                show_usage
                exit 1
                ;;
        esac
    done

    # Show header
    echo -e "${CYAN}"
    echo "🚀 Spring Boot Microservices Build & Deploy Script"
    echo "=================================================="
    echo -e "${NC}"

    # Check prerequisites
    check_prerequisites

    # Execute based on options
    if [ "$DOCKER_ONLY" = true ]; then
        build_docker_images
    elif [ "$JARS_ONLY" = true ]; then
        build_jars
    else
        build_jars
        if [ $failed_jar_builds -eq 0 ] || [ $successful_jar_builds -gt 0 ]; then
            build_docker_images
        fi
    fi

    # Optional cleanup
    if [ "$JARS_ONLY" = false ]; then
        cleanup_docker
    fi

    # Show final summary
    show_final_summary

    echo
    print_status "Script execution completed!"
}

# Trap to handle script interruption
trap 'echo -e "\n${RED}Script interrupted by user${NC}"; exit 1' INT

# Run the script
main "$@"