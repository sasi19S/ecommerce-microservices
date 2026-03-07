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
            steps {

                echo "Building Docker images..."

                sh """
                docker build -t ${DOCKERHUB_USER}/auth-service:${VERSION} ./auth-service
                docker build -t ${DOCKERHUB_USER}/order-service:${VERSION} ./order-service
                docker build -t ${DOCKERHUB_USER}/inventory-service:${VERSION} ./inventory-service
                docker build -t ${DOCKERHUB_USER}/payment-service:${VERSION} ./payment-service
                docker build -t ${DOCKERHUB_USER}/api-gateway:${VERSION} ./api-gateway
                """
            }
        }

        stage('Tag Docker Images') {
            steps {

                echo "Tagging images with latest..."

                sh """
                docker tag ${DOCKERHUB_USER}/auth-service:${VERSION} ${DOCKERHUB_USER}/auth-service:latest
                docker tag ${DOCKERHUB_USER}/order-service:${VERSION} ${DOCKERHUB_USER}/order-service:latest
                docker tag ${DOCKERHUB_USER}/inventory-service:${VERSION} ${DOCKERHUB_USER}/inventory-service:latest
                docker tag ${DOCKERHUB_USER}/payment-service:${VERSION} ${DOCKERHUB_USER}/payment-service:latest
                docker tag ${DOCKERHUB_USER}/api-gateway:${VERSION} ${DOCKERHUB_USER}/api-gateway:latest
                """
            }
        }

        stage('Push Images To DockerHub') {

            steps {

                echo "Pushing images to DockerHub..."

                sh """

                docker push ${DOCKERHUB_USER}/auth-service:${VERSION}
                docker push ${DOCKERHUB_USER}/auth-service:latest

                docker push ${DOCKERHUB_USER}/order-service:${VERSION}
                docker push ${DOCKERHUB_USER}/order-service:latest

                docker push ${DOCKERHUB_USER}/inventory-service:${VERSION}
                docker push ${DOCKERHUB_USER}/inventory-service:latest

                docker push ${DOCKERHUB_USER}/payment-service:${VERSION}
                docker push ${DOCKERHUB_USER}/payment-service:latest

                docker push ${DOCKERHUB_USER}/api-gateway:${VERSION}
                docker push ${DOCKERHUB_USER}/api-gateway:latest
                """
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