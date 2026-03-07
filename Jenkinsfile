pipeline {
    agent any

    options {
        timestamps()
    }

    tools {
        maven 'Maven-3.9.6'
        jdk 'JDK11'
    }

    environment {
        COMPOSE_PROJECT_NAME = "ecommerce"
        DOCKERHUB_USER = "shekhar1914"
        VERSION = "v1.${BUILD_NUMBER}"
    }

    stages {

        stage('Clean Workspace') {
            steps {
                echo "Cleaning workspace..."
                deleteDir()
            }
        }

        stage('Checkout Code') {
            steps {
                echo "Cloning repository..."
                git branch: 'main', url: 'https://github.com/sasi19S/ecommerce-microservices.git'
            }
        }
        stage('Detect Changed Services') {
            steps {
                script {

                    def changedFiles = sh(
                        script: "git diff --name-only HEAD~1 HEAD || true",
                        returnStdout: true
                    ).trim()

                    echo "Changed files: ${changedFiles}"

                    env.AUTH_CHANGED = changedFiles.contains("auth-service") ? "true" : "false"
                    env.ORDER_CHANGED = changedFiles.contains("order-service") ? "true" : "false"
                    env.INVENTORY_CHANGED = changedFiles.contains("inventory-service") ? "true" : "false"
                    env.PAYMENT_CHANGED = changedFiles.contains("payment-service") ? "true" : "false"
                    env.GATEWAY_CHANGED = changedFiles.contains("api-gateway") ? "true" : "false"

                    // If nothing detected (first build) build all
                    if (!changedFiles) {
                        env.AUTH_CHANGED = "true"
                        env.ORDER_CHANGED = "true"
                        env.INVENTORY_CHANGED = "true"
                        env.PAYMENT_CHANGED = "true"
                        env.GATEWAY_CHANGED = "true"
                    }

                }
            }
        }

        stage('Build Microservices') {
            steps {
                echo "Running Maven build..."
                sh 'mvn clean install -DskipTests'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                echo "Running SonarQube analysis..."

                withSonarQubeEnv('SonarQube') {
                    sh '''
                        mvn clean verify sonar:sonar \
                        -Dsonar.projectKey=ecommerce-microservices
                    '''
                }
            }
        }

        stage('Quality Gate') {
            steps {
                timeout(time: 5, unit: 'MINUTES') {
                    script {
                        def qg = waitForQualityGate()
                        echo "Quality Gate Status: ${qg.status}"

                        if (qg.status != 'OK') {
                            echo "Quality gate failed but continuing pipeline"
                            currentBuild.result = 'UNSTABLE'
                        }
                    }
                }
            }
        }

       stage('Docker Login') {
           steps {
               echo "Logging into DockerHub..."

               withCredentials([usernamePassword(
                   credentialsId: 'dockerhub-creds',
                   usernameVariable: 'DOCKER_USER',
                   passwordVariable: 'DOCKER_PASS'
               )]) {

                   sh '''
                   echo $DOCKER_PASS | docker login -u $DOCKER_USER --password-stdin
                   '''
               }
           }
       }

        stage('Build Docker Images') {

            parallel {

                stage('Auth Service') {

                    when {
                        expression { env.AUTH_CHANGED == "true" }
                    }

                    steps {

                        sh """
                        docker build -t ${DOCKERHUB_USER}/auth-service:${VERSION} ./auth-service
                        docker tag ${DOCKERHUB_USER}/auth-service:${VERSION} ${DOCKERHUB_USER}/auth-service:latest
                        """

                    }

                }

                stage('Order Service') {

                    when {
                        expression { env.ORDER_CHANGED == "true" }
                    }

                    steps {

                        sh """
                        docker build -t ${DOCKERHUB_USER}/order-service:${VERSION} ./order-service
                        docker tag ${DOCKERHUB_USER}/order-service:${VERSION} ${DOCKERHUB_USER}/order-service:latest
                        """

                    }

                }

                stage('Inventory Service') {

                    when {
                        expression { env.INVENTORY_CHANGED == "true" }
                    }

                    steps {

                        sh """
                        docker build -t ${DOCKERHUB_USER}/inventory-service:${VERSION} ./inventory-service
                        docker tag ${DOCKERHUB_USER}/inventory-service:${VERSION} ${DOCKERHUB_USER}/inventory-service:latest
                        """

                    }

                }

                stage('Payment Service') {

                    when {
                        expression { env.PAYMENT_CHANGED == "true" }
                    }

                    steps {

                        sh """
                        docker build -t ${DOCKERHUB_USER}/payment-service:${VERSION} ./payment-service
                        docker tag ${DOCKERHUB_USER}/payment-service:${VERSION} ${DOCKERHUB_USER}/payment-service:latest
                        """

                    }

                }

                stage('API Gateway') {

                    when {
                        expression { env.GATEWAY_CHANGED == "true" }
                    }

                    steps {

                        sh """
                        docker build -t ${DOCKERHUB_USER}/api-gateway:${VERSION} ./api-gateway
                        docker tag ${DOCKERHUB_USER}/api-gateway:${VERSION} ${DOCKERHUB_USER}/api-gateway:latest
                        """

                    }

                }

            }

        }



        stage('Push Images') {

            steps {

                script {

                    if (env.AUTH_CHANGED == "true") {

                        sh """
                        docker push ${DOCKERHUB_USER}/auth-service:${VERSION}
                        docker push ${DOCKERHUB_USER}/auth-service:latest
                        """

                    }

                    if (env.ORDER_CHANGED == "true") {

                        sh """
                        docker push ${DOCKERHUB_USER}/order-service:${VERSION}
                        docker push ${DOCKERHUB_USER}/order-service:latest
                        """

                    }

                    if (env.INVENTORY_CHANGED == "true") {

                        sh """
                        docker push ${DOCKERHUB_USER}/inventory-service:${VERSION}
                        docker push ${DOCKERHUB_USER}/inventory-service:latest
                        """

                    }

                    if (env.PAYMENT_CHANGED == "true") {

                        sh """
                        docker push ${DOCKERHUB_USER}/payment-service:${VERSION}
                        docker push ${DOCKERHUB_USER}/payment-service:latest
                        """

                    }

                    if (env.GATEWAY_CHANGED == "true") {

                        sh """
                        docker push ${DOCKERHUB_USER}/api-gateway:${VERSION}
                        docker push ${DOCKERHUB_USER}/api-gateway:latest
                        """

                    }

                }

            }

        }


        stage('Run Containers') {
            steps {
                echo "Restarting containers..."
                sh 'docker compose down || true'
                sh 'docker compose up -d'
            }
        }

        stage('Verify Containers') {
            steps {
                echo "Listing running containers..."
                sh 'docker ps'
            }
        }
    }

    post {
        always {
            echo "Cleaning Jenkins workspace after build..."
            cleanWs()
        }
    }
}