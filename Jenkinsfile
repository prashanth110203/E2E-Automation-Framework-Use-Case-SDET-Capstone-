pipeline {
    agent any
    
    tools {
        maven 'Maven3'
        jdk 'JDK17'
    }
    
    parameters {
        choice(
            name: 'BROWSER', 
            choices: ['chrome', 'firefox', 'edge'], 
            description: 'Select browser to run tests'
        )
        choice(
            name: 'ENVIRONMENT', 
            choices: ['qa', 'staging', 'prod'], 
            description: 'Select environment'
        )
        string(
            name: 'TEST_SUITE', 
            defaultValue: 'testng.xml', 
            description: 'TestNG Suite XML file'
        )
        booleanParam(
            name: 'HEADLESS_MODE', 
            defaultValue: true, 
            description: 'Run tests in headless mode'
        )
    }
    
    environment {
        EXCEL_FILE = 'testdata/TestData.xlsx'
        SCREENSHOT_DIR = 'screenshots'
        REPORTS_DIR = 'test-output/reports'
    }
    
    stages {
        stage('Pre-Build Checks') {
            steps {
                echo '========================================='
                echo 'PRE-BUILD VALIDATION'
                echo '========================================='
                echo "Browser: ${params.BROWSER}"
                echo "Environment: ${params.ENVIRONMENT}"
                echo "Test Suite: ${params.TEST_SUITE}"
                echo "Headless Mode: ${params.HEADLESS_MODE}"
            }
        }
        
        stage('Checkout') {
            steps {
                echo '========================================='
                echo 'CHECKING OUT CODE FROM REPOSITORY'
                echo '========================================='
                checkout scm
            }
        }
        
        stage('Clean Workspace') {
            steps {
                echo '========================================='
                echo 'CLEANING OLD ARTIFACTS'
                echo '========================================='
                
                bat '''
                    if exist screenshots rd /s /q screenshots
                    if exist test-output rd /s /q test-output
                    echo Cleanup completed
                '''
            }
        }
        
        stage('Build') {
            steps {
                echo '========================================='
                echo 'BUILDING THE PROJECT'
                echo '========================================='
                
                bat 'mvn clean compile -DskipTests'
                
                echo 'Build completed successfully'
            }
        }
        
        stage('Run Tests') {
            steps {
                echo '========================================='
                echo 'EXECUTING TEST SUITE'
                echo '========================================='
                echo "Browser: ${params.BROWSER}"
                echo "Environment: ${params.ENVIRONMENT}"
                echo '========================================='
                
                script {
                    def exitCode = bat(
                        script: "mvn test -Dbrowser=${params.BROWSER} -Denvironment=${params.ENVIRONMENT} -Dheadless=${params.HEADLESS_MODE}",
                        returnStatus: true
                    )
                    
                    if (exitCode != 0) {
                        echo 'Some tests failed (including expected negative tests)'
                        currentBuild.result = 'UNSTABLE'
                    } else {
                        echo 'All tests executed successfully'
                    }
                }
            }
        }
        
        stage('Test Results Analysis') {
            steps {
                echo '========================================='
                echo 'ANALYZING TEST RESULTS'
                echo '========================================='
                
                script {
                    def testngXml = 'target/surefire-reports/testng-results.xml'
                    
                    if (fileExists(testngXml)) {
                        def results = readFile(testngXml)
                        echo 'TestNG Results file found'
                        echo 'Test execution completed'
                    } else {
                        echo 'TestNG results file not found'
                    }
                }
            }
        }
        
        stage('Collect Screenshots') {
            steps {
                echo '========================================='
                echo 'COLLECTING SCREENSHOTS'
                echo '========================================='
                
                script {
                    if (fileExists(SCREENSHOT_DIR)) {
                        echo 'Screenshots directory found'
                    } else {
                        echo 'No screenshots directory found'
                    }
                }
            }
        }
        
        stage('Publish Reports') {
            steps {
                echo '========================================='
                echo 'PUBLISHING TEST REPORTS'
                echo '========================================='
                
                // Publish TestNG HTML Report
                publishHTML([
                    allowMissing: true,
                    alwaysLinkToLastBuild: true,
                    keepAll: true,
                    reportDir: 'test-output',
                    reportFiles: 'index.html',
                    reportName: 'TestNG HTML Report',
                    reportTitles: 'E2E Test Execution Report'
                ])
                
                // Publish Extent Report
                publishHTML([
                    allowMissing: true,
                    alwaysLinkToLastBuild: true,
                    keepAll: true,
                    reportDir: 'test-output/reports',
                    reportFiles: '*.html',
                    reportName: 'Extent Report',
                    reportTitles: 'Detailed Extent Report'
                ])
                
                // Publish Screenshots
                publishHTML([
                    allowMissing: true,
                    alwaysLinkToLastBuild: true,
                    keepAll: true,
                    reportDir: 'screenshots',
                    reportFiles: '*.png',
                    reportName: 'Test Screenshots',
                    reportTitles: 'Test Execution Screenshots'
                ])
                
                echo 'Reports published successfully'
            }
        }
        
        stage('Archive Artifacts') {
            steps {
                echo '========================================='
                echo 'ARCHIVING ARTIFACTS'
                echo '========================================='
                
                archiveArtifacts artifacts: 'target/surefire-reports/**', allowEmptyArchive: true
                archiveArtifacts artifacts: 'test-output/**', allowEmptyArchive: true
                archiveArtifacts artifacts: 'screenshots/**', allowEmptyArchive: true
                
                echo 'Artifacts archived successfully'
            }
        }
        
        stage('Publish TestNG Results') {
            steps {
                echo '========================================='
                echo 'PUBLISHING TESTNG RESULTS'
                echo '========================================='
                
                step([
                    $class: 'Publisher',
                    reportFilenamePattern: 'target/surefire-reports/testng-results.xml'
                ])
                
                echo 'TestNG results published'
            }
        }
    }
    
    post {
        always {
            echo '========================================='
            echo 'PIPELINE EXECUTION COMPLETED'
            echo '========================================='
            echo "Build Number: ${env.BUILD_NUMBER}"
            echo "Build Result: ${currentBuild.result ?: 'SUCCESS'}"
            echo '========================================='
        }
        
        success {
            echo '========================================='
            echo 'BUILD SUCCESSFUL!'
            echo '========================================='
        }
        
        unstable {
            echo '========================================='
            echo 'BUILD UNSTABLE - Some tests failed'
            echo 'Check the test reports for details'
            echo '========================================='
        }
        
        failure {
            echo '========================================='
            echo 'BUILD FAILED! Please check logs'
            echo '========================================='
        }
    }
}