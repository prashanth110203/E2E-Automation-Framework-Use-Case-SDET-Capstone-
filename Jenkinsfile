pipeline {
    agent any
    
    tools {
        maven 'Maven'
        jdk 'JDK11'
    }
    
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        
        stage('Build') {
            steps {
                bat 'mvn clean compile -DskipTests'
            }
        }
        
        stage('Run Tests') {
            steps {
                bat 'mvn test'
            }
        }
        
        stage('Publish Reports') {
            steps {
                // Publish TestNG reports
                publishHTML([
                    allowMissing: false,
                    alwaysLinkToLastBuild: true,
                    keepAll: true,
                    reportDir: 'target/surefire-reports',
                    reportFiles: 'index.html',
                    reportName: 'TestNG Report'
                ])
            }
        }
    }
    
    post {
        always {
            echo '========================================='
            echo 'Pipeline execution completed'
            echo '========================================='
        }
        success {
            echo '========================================='
            echo '✅ BUILD SUCCESSFUL!'
            echo '========================================='
        }
        failure {
            echo '========================================='
            echo '❌ BUILD FAILED!'
            echo '========================================='
        }
    }
}