pipeline {

    agent any
    environment {
        IMAGE = 'weibeld/ktest-core'
        DOCKER_HUB_PW = credentials('docker-hub-weibeld-password')
    }

    stages {
        stage('Build') {
            steps {
                sh './gradlew installDist'
            }
        }

        stage('Create Docker Image') {
            steps {
                sh 'docker build -t "$IMAGE" .'
            }
        }

        stage('Publish Docker Image') {
            steps {
                sh 'docker login -u weibeld -p "$DOCKER_HUB_PW"'
                sh 'docker push "$IMAGE"'
            }
        }
    }
}
