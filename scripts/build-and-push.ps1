param(
    [string]$VERSION
)

$DOCKER_USER="shekhar1914"
$NAMESPACE="ecommerce"

Write-Host "Building Docker Images with version $VERSION..."

docker build -t $DOCKER_USER/auth-service:$VERSION ../services/auth-service
docker build -t $DOCKER_USER/order-service:$VERSION ../services/order-service
docker build -t $DOCKER_USER/inventory-service:$VERSION ../services/inventory-service
docker build -t $DOCKER_USER/payment-service:$VERSION ../services/payment-service
docker build -t $DOCKER_USER/api-gateway:$VERSION ../services/api-gateway


Write-Host "Pushing Docker Images to Docker Hub..."

docker push $DOCKER_USER/auth-service:$VERSION
docker push $DOCKER_USER/order-service:$VERSION
docker push $DOCKER_USER/inventory-service:$VERSION
docker push $DOCKER_USER/payment-service:$VERSION
docker push $DOCKER_USER/api-gateway:$VERSION


Write-Host "Updating Kubernetes Deployments..."

kubectl set image deployment/auth-service auth-service=$DOCKER_USER/auth-service:$VERSION -n $NAMESPACE
kubectl set image deployment/order-service order-service=$DOCKER_USER/order-service:$VERSION -n $NAMESPACE
kubectl set image deployment/inventory-service inventory-service=$DOCKER_USER/inventory-service:$VERSION -n $NAMESPACE
kubectl set image deployment/payment-service payment-service=$DOCKER_USER/payment-service:$VERSION -n $NAMESPACE
kubectl set image deployment/api-gateway api-gateway=$DOCKER_USER/api-gateway:$VERSION -n $NAMESPACE


Write-Host "Restarting deployments..."

kubectl rollout restart deployment auth-service -n $NAMESPACE
kubectl rollout restart deployment order-service -n $NAMESPACE
kubectl rollout restart deployment inventory-service -n $NAMESPACE
kubectl rollout restart deployment payment-service -n $NAMESPACE
kubectl rollout restart deployment api-gateway -n $NAMESPACE


Write-Host "Waiting for rollout to complete..."

kubectl rollout status deployment/auth-service -n $NAMESPACE
kubectl rollout status deployment/order-service -n $NAMESPACE
kubectl rollout status deployment/inventory-service -n $NAMESPACE
kubectl rollout status deployment/payment-service -n $NAMESPACE
kubectl rollout status deployment/api-gateway -n $NAMESPACE


Write-Host "Deployment completed successfully 🚀"