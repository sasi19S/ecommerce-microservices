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

        stage('Build Docker Images') {
            steps {
                echo "Building Docker images..."
                sh 'docker-compose build'
            }
        }

        stage('Run Containers') {
            steps {
                echo "Restarting containers..."
                sh 'docker-compose down || true'
                sh 'docker-compose up -d'
            }
        }

        stage('Verify Containers') {
            steps {
                echo "Listing running containers..."
                sh 'docker ps'
            }
        }

        post {
            always {
                cleanWs()
            }
        }
    }
}