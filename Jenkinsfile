pipeline {
    agent any

    tools {
        maven 'Maven3'
        jdk 'JDK17'
    }

    parameters {
        choice(name: 'BROWSER', choices: ['chrome', 'firefox', 'edge'], description: 'Select browser')
        choice(name: 'ENVIRONMENT', choices: ['qa', 'uat', 'prod'], description: 'Select environment')
    }

    stages {
        stage('Initialize') {
            steps {
                echo '========================================='
                echo '    E2E AUTOMATION PIPELINE STARTED      '
                echo '========================================='
                echo "Browser: ${params.BROWSER}"
                echo "Environment: ${params.ENVIRONMENT}"
                echo '========================================='
            }
        }

        stage('Checkout') {
            steps {
                echo '========================================='
                echo 'Stage 1: Checking out code from GitHub'
                echo '========================================='
                checkout scm
                bat 'dir'
            }
        }

        stage('Build') {
            steps {
                echo '========================================='
                echo 'Stage 2: Compiling the project'
                echo '========================================='
                bat 'mvn clean compile'
            }
        }

        stage('Run Tests') {
            steps {
                echo '========================================='
                echo "Stage 3: Running tests on ${params.BROWSER}"
                echo '========================================='
                script {
                    try {
                        bat "mvn test -Dbrowser=${params.BROWSER} -Denvironment=${params.ENVIRONMENT}"
                    } catch (Exception e) {
                        echo "Some tests failed: ${e.message}"
                        currentBuild.result = 'UNSTABLE'
                    }
                }
            }
        }

        stage('Publish Reports') {
            steps {
                echo '========================================='
                echo 'Stage 4: Publishing test reports'
                echo '========================================='
                
                // Publish JUnit Test Results (built-in)
                junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'
                
                // Publish HTML Report (Extent Report)
                publishHTML([
                    allowMissing: true,
                    alwaysLinkToLastBuild: true,
                    keepAll: true,
                    reportDir: 'reports',
                    reportFiles: '*.html',
                    reportName: 'Extent Test Report'
                ])
                
                // Archive Artifacts
                archiveArtifacts artifacts: 'reports/**,screenshots/**,logs/**,target/surefire-reports/**', allowEmptyArchive: true
            }
        }
    }

    post {
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
        unstable {
            echo '========================================='
            echo '⚠️ BUILD UNSTABLE - Some tests failed'
            echo '========================================='
        }
        always {
            echo '========================================='
            echo 'Pipeline execution completed'
            echo '========================================='
        }
    }
}