# Version tag
$VERSION="v1.29"

# Docker Hub username
$DOCKER_USER="shekhar1914"

Write-Host "Building Docker Images..."

docker build -t $DOCKER_USER/auth-service:$VERSION ../services/auth-service
docker build -t $DOCKER_USER/order-service:$VERSION ../services/order-service
docker build -t $DOCKER_USER/inventory-service:$VERSION ../services/inventory-service
docker build -t $DOCKER_USER/payment-service:$VERSION ../services/payment-service
docker build -t $DOCKER_USER/api-gateway:$VERSION ../services/api-gateway

Write-Host "Pushing Docker Images..."

docker push $DOCKER_USER/auth-service:$VERSION
docker push $DOCKER_USER/order-service:$VERSION
docker push $DOCKER_USER/inventory-service:$VERSION
docker push $DOCKER_USER/payment-service:$VERSION
docker push $DOCKER_USER/api-gateway:$VERSION

Write-Host "Docker images pushed successfully!"