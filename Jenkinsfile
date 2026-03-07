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

                withCredentials([string(credentialsId: 'sonar-token', variable: 'SONAR_TOKEN')]) {
                    sh """
                    mvn org.sonarsource.scanner.maven:sonar-maven-plugin:4.0.0.4121:sonar \
                    -Dsonar.projectKey=ecommerce-microservices \
                    -Dsonar.host.url=http://sonarqube:9000 \
                    -Dsonar.login=$SONAR_TOKEN
                    """
                }
            }
        }

        stage('Quality Gate') {
            steps {
                timeout(time: 2, unit: 'MINUTES') {
                    script {
                        def qg = waitForQualityGate()
                        echo "Quality Gate status: ${qg.status}"

                        if (qg.status != 'OK') {
                            echo "Quality Gate failed — marking build UNSTABLE"
                            currentBuild.result = 'UNSTABLE'
                        }
                    }
                }
            }
        }

        stage('Build Docker Images') {
            steps {
                echo "Building Docker images..."
                sh 'docker compose build'
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