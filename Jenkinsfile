pipeline {
    agent any
    
    tools {
        maven 'Maven3'
        jdk 'JDK17'
    }
    
    parameters {
        choice(name: 'BROWSER', choices: ['chrome', 'firefox', 'edge'], description: 'Select browser to run tests')
        choice(name: 'ENVIRONMENT', choices: ['qa', 'staging', 'prod'], description: 'Select environment')
    }
    
    stages {
        stage('Checkout') {
            steps {
                echo '========================================='
                echo '📥 Checking out code from Git'
                echo '========================================='
                checkout scm
            }
        }
        
        stage('Build') {
            steps {
                echo '========================================='
                echo '🔨 Building the project'
                echo '========================================='
                bat 'mvn clean compile -DskipTests'
            }
        }
        
        stage('Run Tests') {
            steps {
                echo '========================================='
                echo '🧪 Running Test Suite'
                echo "Browser: ${params.BROWSER}"
                echo "Environment: ${params.ENVIRONMENT}"
                echo '========================================='
                
                // Run tests and capture exit code
                script {
                    def exitCode = bat(script: 'mvn test', returnStatus: true)
                    
                    if (exitCode != 0) {
                        echo '⚠️ Some tests failed (this is expected)'
                        // Don't fail the build - we want to see reports
                        currentBuild.result = 'UNSTABLE'
                    }
                }
            }
        }
        
        stage('Publish Reports') {
            steps {
                echo '========================================='
                echo '📊 Publishing Test Reports'
                echo '========================================='
                
                // Publish TestNG HTML Reports
                publishHTML([
                    allowMissing: false,
                    alwaysLinkToLastBuild: true,
                    keepAll: true,
                    reportDir: 'target/surefire-reports',
                    reportFiles: 'index.html',
                    reportName: 'TestNG HTML Report',
                    reportTitles: 'E2E Test Report'
                ])
                
                // Publish Screenshots
                publishHTML([
                    allowMissing: true,
                    alwaysLinkToLastBuild: true,
                    keepAll: true,
                    reportDir: 'screenshots',
                    reportFiles: '*.png',
                    reportName: 'Test Screenshots',
                    reportTitles: 'Screenshots'
                ])
                
                // Archive artifacts
                archiveArtifacts artifacts: 'target/surefire-reports/**, screenshots/**', 
                                 allowEmptyArchive: true
                
                // Publish TestNG Results
                step([$class: 'Publisher', 
                      reportFilenamePattern: 'target/surefire-reports/testng-results.xml'])
            }
        }
        
        stage('Generate Summary') {
            steps {
                script {
                    echo '========================================='
                    echo '📋 TEST EXECUTION SUMMARY'
                    echo '========================================='
                    
                    // Read TestNG results
                    def testngResults = readFile('target/surefire-reports/testng-results.xml')
                    
                    echo '✅ Tests Execution Completed'
                    echo '📸 Screenshots saved in: screenshots/'
                    echo '📊 Reports available in Jenkins'
                    echo '========================================='
                }
            }
        }
    }
    
    post {
        always {
            echo '========================================='
            echo 'Pipeline execution completed'
            echo '========================================='
            
            // Clean workspace (optional)
            // cleanWs()
        }
        
        success {
            echo '========================================='
            echo '✅ BUILD SUCCESSFUL!'
            echo '========================================='
        }
        
        unstable {
            echo '========================================='
            echo '⚠️ BUILD UNSTABLE - Some tests failed'
            echo '========================================='
        }
        
        failure {
            echo '========================================='
            echo '❌ BUILD FAILED!'
            echo '========================================='
        }
    }
}