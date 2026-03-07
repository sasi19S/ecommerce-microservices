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
        DOCKER_BUILDKIT = "1"
        SERVICES = "auth-service order-service inventory-service payment-service api-gateway"
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

                    // default values
                    env.AUTH_CHANGED = "false"
                    env.ORDER_CHANGED = "false"
                    env.INVENTORY_CHANGED = "false"
                    env.PAYMENT_CHANGED = "false"
                    env.GATEWAY_CHANGED = "false"

                    // if first build or empty result
                    if (!changedFiles) {

                        echo "First build detected → building ALL services"

                        env.AUTH_CHANGED = "true"
                        env.ORDER_CHANGED = "true"
                        env.INVENTORY_CHANGED = "true"
                        env.PAYMENT_CHANGED = "true"
                        env.GATEWAY_CHANGED = "true"

                    }

                    // detect global changes
                    else if (
                        changedFiles.contains("pom.xml") ||
                        changedFiles.contains("docker-compose") ||
                        changedFiles.contains("Jenkinsfile")
                    ) {

                        echo "Global change detected → building ALL services"

                        env.AUTH_CHANGED = "true"
                        env.ORDER_CHANGED = "true"
                        env.INVENTORY_CHANGED = "true"
                        env.PAYMENT_CHANGED = "true"
                        env.GATEWAY_CHANGED = "true"

                    }

                    else {

                        if (changedFiles.contains("auth-service"))
                            env.AUTH_CHANGED = "true"

                        if (changedFiles.contains("order-service"))
                            env.ORDER_CHANGED = "true"

                        if (changedFiles.contains("inventory-service"))
                            env.INVENTORY_CHANGED = "true"

                        if (changedFiles.contains("payment-service"))
                            env.PAYMENT_CHANGED = "true"

                        if (changedFiles.contains("api-gateway"))
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

            steps {

                script {

                    def services = [
                        "auth-service": env.AUTH_CHANGED,
                        "order-service": env.ORDER_CHANGED,
                        "inventory-service": env.INVENTORY_CHANGED,
                        "payment-service": env.PAYMENT_CHANGED,
                        "api-gateway": env.GATEWAY_CHANGED
                    ]

                    services.each { svc, changed ->

                        if (changed == "true") {

                            echo "Building ${svc}..."

                            sh """
                            docker build -t ${DOCKERHUB_USER}/${svc}:${VERSION} ./${svc}
                            docker tag ${DOCKERHUB_USER}/${svc}:${VERSION} ${DOCKERHUB_USER}/${svc}:latest
                            """

                        } else {

                            echo "Skipping ${svc} (no changes)"

                        }

                    }

                }

            }

        }



        stage('Push Images') {

            steps {

                script {

                    def services = [
                        "auth-service": env.AUTH_CHANGED,
                        "order-service": env.ORDER_CHANGED,
                        "inventory-service": env.INVENTORY_CHANGED,
                        "payment-service": env.PAYMENT_CHANGED,
                        "api-gateway": env.GATEWAY_CHANGED
                    ]

                    services.each { svc, changed ->

                        if (changed == "true") {

                            echo "Pushing ${svc}..."

                            sh """
                            docker push ${DOCKERHUB_USER}/${svc}:${VERSION}
                            docker push ${DOCKERHUB_USER}/${svc}:latest
                            """

                        }

                    }

                }

            }

        }

        stage('Cleanup Docker Images') {

            steps {

                echo "Cleaning unused docker images..."

                sh '''
                docker image prune -f
                '''

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