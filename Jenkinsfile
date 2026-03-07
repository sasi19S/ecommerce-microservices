pipeline {
    agent any

    tools {
        maven 'Maven-3.9.6'
        jdk 'JDK11'
    }

    stages {

        stage('Checkout Code') {
            steps {
                git 'https://github.com/sasi19S/ecommerce-microservices.git'
            }
        }

        stage('Build Microservices') {
            steps {
                sh 'mvn clean install -DskipTests'
            }
        }

        stage('Build Docker Images') {
            steps {
                sh 'docker-compose build'
            }
        }

        stage('Run Containers') {
            steps {
                sh 'docker-compose up -d'
            }
        }

        stage('Verify Containers') {
            steps {
                sh 'docker ps'
            }
        }

    }
}