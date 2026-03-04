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
            description: '🌐 Select browser to run tests'
        )
        choice(
            name: 'ENVIRONMENT', 
            choices: ['qa', 'staging', 'prod'], 
            description: '🏢 Select environment'
        )
        string(
            name: 'TEST_SUITE', 
            defaultValue: 'testng.xml', 
            description: '📋 TestNG Suite XML file'
        )
        booleanParam(
            name: 'HEADLESS_MODE', 
            defaultValue: true, 
            description: '👁️ Run tests in headless mode'
        )
    }
    
    environment {
        EXCEL_FILE = 'testdata/TestData.xlsx'
        SCREENSHOT_DIR = 'screenshots'
        REPORTS_DIR = 'test-output/reports'
        TIMESTAMP = "${new Date().format('yyyyMMdd_HHmmss')}"
    }
    
    stages {
        stage('🔍 Pre-Build Checks') {
            steps {
                echo '========================================='
                echo '🔍 PRE-BUILD VALIDATION'
                echo '========================================='
                
                script {
                    // Check if test data exists
                    if (!fileExists("${EXCEL_FILE}")) {
                        error "❌ Test data file not found: ${EXCEL_FILE}"
                    }
                    
                    echo "✅ Test Data File: ${EXCEL_FILE} - EXISTS"
                    echo "✅ Browser: ${params.BROWSER}"
                    echo "✅ Environment: ${params.ENVIRONMENT}"
                    echo "✅ Test Suite: ${params.TEST_SUITE}"
                    echo "✅ Headless Mode: ${params.HEADLESS_MODE}"
                    echo "✅ Timestamp: ${TIMESTAMP}"
                }
            }
        }
        
        stage('📥 Checkout') {
            steps {
                echo '========================================='
                echo '📥 CHECKING OUT CODE FROM REPOSITORY'
                echo '========================================='
                
                checkout scm
                
                script {
                    // Display Git information
                    def gitCommit = bat(returnStdout: true, script: 'git rev-parse HEAD').trim()
                    def gitBranch = bat(returnStdout: true, script: 'git rev-parse --abbrev-ref HEAD').trim()
                    
                    echo "📌 Git Commit: ${gitCommit}"
                    echo "🌿 Git Branch: ${gitBranch}"
                }
            }
        }
        
        stage('🧹 Clean Workspace') {
            steps {
                echo '========================================='
                echo '🧹 CLEANING OLD ARTIFACTS'
                echo '========================================='
                
                script {
                    // Clean old screenshots
                    if (fileExists(SCREENSHOT_DIR)) {
                        bat "if exist ${SCREENSHOT_DIR} rd /s /q ${SCREENSHOT_DIR}"
                    }
                    
                    // Clean old reports
                    if (fileExists(REPORTS_DIR)) {
                        bat "if exist ${REPORTS_DIR} rd /s /q ${REPORTS_DIR}"
                    }
                    
                    echo '✅ Cleanup completed'
                }
            }
        }
        
        stage('🔨 Build') {
            steps {
                echo '========================================='
                echo '🔨 BUILDING THE PROJECT'
                echo '========================================='
                
                bat 'mvn clean compile -DskipTests -q'
                
                echo '✅ Build completed successfully'
            }
        }
        
        stage('📦 Dependency Check') {
            steps {
                echo '========================================='
                echo '📦 CHECKING DEPENDENCIES'
                echo '========================================='
                
                bat 'mvn dependency:tree -q'
                
                echo '✅ Dependencies verified'
            }
        }
        
        stage('🧪 Run Tests') {
            steps {
                echo '========================================='
                echo '🧪 EXECUTING TEST SUITE'
                echo '========================================='
                echo "🌐 Browser: ${params.BROWSER}"
                echo "🏢 Environment: ${params.ENVIRONMENT}"
                echo "📋 Suite: ${params.TEST_SUITE}"
                echo "👁️ Headless: ${params.HEADLESS_MODE}"
                echo '========================================='
                
                script {
                    try {
                        // Run Maven tests with parameters
                        def mvnCommand = """
                            mvn test 
                            -Dbrowser=${params.BROWSER} 
                            -Denvironment=${params.ENVIRONMENT}
                            -Dheadless=${params.HEADLESS_MODE}
                            -DsuiteXmlFile=${params.TEST_SUITE}
                        """.replaceAll("\\s+", " ").trim()
                        
                        def exitCode = bat(script: mvnCommand, returnStatus: true)
                        
                        if (exitCode != 0) {
                            echo '⚠️ Some tests failed (including expected negative tests)'
                            currentBuild.result = 'UNSTABLE'
                        } else {
                            echo '✅ All tests executed successfully'
                        }
                        
                    } catch (Exception e) {
                        echo "❌ Test execution error: ${e.message}"
                        currentBuild.result = 'UNSTABLE'
                    }
                }
            }
        }
        
        stage('📊 Test Results Analysis') {
            steps {
                echo '========================================='
                echo '📊 ANALYZING TEST RESULTS'
                echo '========================================='
                
                script {
                    try {
                        // Parse TestNG results
                        def testngXml = 'target/surefire-reports/testng-results.xml'
                        
                        if (fileExists(testngXml)) {
                            def results = readFile(testngXml)
                            
                            // Extract test counts (simple parsing)
                            def totalTests = (results =~ /total="(\d+)"/)
                            def passedTests = (results =~ /passed="(\d+)"/)
                            def failedTests = (results =~ /failed="(\d+)"/)
                            def skippedTests = (results =~ /skipped="(\d+)"/)
                            
                            echo '┌─────────────────────────────────────┐'
                            echo '│      TEST EXECUTION SUMMARY         │'
                            echo '├─────────────────────────────────────┤'
                            
                            if (totalTests) {
                                echo "│ 📊 Total Tests:   ${totalTests[0][1].padRight(16)}│"
                            }
                            if (passedTests) {
                                echo "│ ✅ Passed:        ${passedTests[0][1].padRight(16)}│"
                            }
                            if (failedTests) {
                                echo "│ ❌ Failed:        ${failedTests[0][1].padRight(16)}│"
                            }
                            if (skippedTests) {
                                echo "│ ⏭️ Skipped:       ${skippedTests[0][1].padRight(16)}│"
                            }
                            
                            echo '└─────────────────────────────────────┘'
                            
                        } else {
                            echo '⚠️ TestNG results file not found'
                        }
                        
                    } catch (Exception e) {
                        echo "⚠️ Could not parse test results: ${e.message}"
                    }
                }
            }
        }
        
        stage('📸 Collect Screenshots') {
            steps {
                echo '========================================='
                echo '📸 COLLECTING SCREENSHOTS'
                echo '========================================='
                
                script {
                    if (fileExists(SCREENSHOT_DIR)) {
                        def screenshots = findFiles(glob: "${SCREENSHOT_DIR}/**/*.png")
                        
                        echo "📸 Found ${screenshots.length} screenshot(s)"
                        
                        screenshots.each { screenshot ->
                            echo "  📷 ${screenshot.name}"
                        }
                        
                    } else {
                        echo '⚠️ No screenshots directory found'
                    }
                }
            }
        }
        
        stage('📑 Publish Reports') {
            steps {
                echo '========================================='
                echo '📑 PUBLISHING TEST REPORTS'
                echo '========================================='
                
                script {
                    // Publish TestNG HTML Report
                    try {
                        publishHTML([
                            allowMissing: false,
                            alwaysLinkToLastBuild: true,
                            keepAll: true,
                            reportDir: 'test-output',
                            reportFiles: 'index.html',
                            reportName: '📊 TestNG HTML Report',
                            reportTitles: 'E2E Test Execution Report'
                        ])
                        echo '✅ TestNG report published'
                    } catch (Exception e) {
                        echo "⚠️ TestNG report not available: ${e.message}"
                    }
                    
                    // Publish Extent Report
                    try {
                        publishHTML([
                            allowMissing: false,
                            alwaysLinkToLastBuild: true,
                            keepAll: true,
                            reportDir: 'test-output/reports',
                            reportFiles: 'TestReport_*.html',
                            reportName: '📈 Extent Report',
                            reportTitles: 'Detailed Extent Report'
                        ])
                        echo '✅ Extent report published'
                    } catch (Exception e) {
                        echo "⚠️ Extent report not available: ${e.message}"
                    }
                    
                    // Publish Screenshots
                    try {
                        publishHTML([
                            allowMissing: true,
                            alwaysLinkToLastBuild: true,
                            keepAll: true,
                            reportDir: SCREENSHOT_DIR,
                            reportFiles: '*.png',
                            reportName: '📸 Test Screenshots',
                            reportTitles: 'Test Execution Screenshots'
                        ])
                        echo '✅ Screenshots published'
                    } catch (Exception e) {
                        echo "⚠️ Screenshots not available: ${e.message}"
                    }
                }
            }
        }
        
        stage('💾 Archive Artifacts') {
            steps {
                echo '========================================='
                echo '💾 ARCHIVING ARTIFACTS'
                echo '========================================='
                
                script {
                    try {
                        archiveArtifacts artifacts: '''
                            target/surefire-reports/**,
                            test-output/**,
                            screenshots/**,
                            logs/**
                        ''', allowEmptyArchive: true
                        
                        echo '✅ Artifacts archived successfully'
                    } catch (Exception e) {
                        echo "⚠️ Error archiving artifacts: ${e.message}"
                    }
                }
            }
        }
        
        stage('📈 Publish TestNG Results') {
            steps {
                echo '========================================='
                echo '📈 PUBLISHING TESTNG RESULTS'
                echo '========================================='
                
                script {
                    try {
                        step([
                            $class: 'Publisher',
                            reportFilenamePattern: 'target/surefire-reports/testng-results.xml'
                        ])
                        echo '✅ TestNG results published'
                    } catch (Exception e) {
                        echo "⚠️ Could not publish TestNG results: ${e.message}"
                    }
                }
            }
        }
        
        stage('📧 Generate Email Report') {
            steps {
                echo '========================================='
                echo '📧 GENERATING EMAIL REPORT'
                echo '========================================='
                
                script {
                    def reportContent = """
                    <html>
                    <body style="font-family: Arial, sans-serif;">
                        <h2 style="color: #2c3e50;">🧪 E2E Test Execution Report</h2>
                        
                        <table style="border-collapse: collapse; width: 100%; margin: 20px 0;">
                            <tr style="background-color: #3498db; color: white;">
                                <th style="padding: 10px; border: 1px solid #ddd;">Parameter</th>
                                <th style="padding: 10px; border: 1px solid #ddd;">Value</th>
                            </tr>
                            <tr>
                                <td style="padding: 8px; border: 1px solid #ddd;">Build Number</td>
                                <td style="padding: 8px; border: 1px solid #ddd;">${env.BUILD_NUMBER}</td>
                            </tr>
                            <tr>
                                <td style="padding: 8px; border: 1px solid #ddd;">Browser</td>
                                <td style="padding: 8px; border: 1px solid #ddd;">${params.BROWSER}</td>
                            </tr>
                            <tr>
                                <td style="padding: 8px; border: 1px solid #ddd;">Environment</td>
                                <td style="padding: 8px; border: 1px solid #ddd;">${params.ENVIRONMENT}</td>
                            </tr>
                            <tr>
                                <td style="padding: 8px; border: 1px solid #ddd;">Build Status</td>
                                <td style="padding: 8px; border: 1px solid #ddd;">${currentBuild.result ?: 'SUCCESS'}</td>
                            </tr>
                            <tr>
                                <td style="padding: 8px; border: 1px solid #ddd;">Timestamp</td>
                                <td style="padding: 8px; border: 1px solid #ddd;">${TIMESTAMP}</td>
                            </tr>
                        </table>
                        
                        <p><a href="${env.BUILD_URL}" style="background-color: #3498db; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px;">View Full Report</a></p>
                    </body>
                    </html>
                    """
                    
                    // Save report to file
                    writeFile file: 'email-report.html', text: reportContent
                    
                    echo '✅ Email report generated'
                }
            }
        }
    }
    
    post {
        always {
            echo '========================================='
            echo '🏁 PIPELINE EXECUTION COMPLETED'
            echo '========================================='
            echo "⏱️ Duration: ${currentBuild.durationString}"
            echo "🔢 Build Number: ${env.BUILD_NUMBER}"
            echo "📅 Timestamp: ${TIMESTAMP}"
            echo '========================================='
            
            // Optional: Clean workspace (uncomment if needed)
            // cleanWs()
        }
        
        success {
            echo '╔════════════════════════════════════════╗'
            echo '║   ✅ BUILD SUCCESSFUL! 🎉             ║'
            echo '╚════════════════════════════════════════╝'
            
            script {
                // Optional: Send success email
                // emailext (
                //     subject: "✅ Test Build #${env.BUILD_NUMBER} - SUCCESS",
                //     body: readFile('email-report.html'),
                //     mimeType: 'text/html',
                //     to: 'your-email@example.com'
                // )
            }
        }
        
        unstable {
            echo '╔════════════════════════════════════════╗'
            echo '║   ⚠️ BUILD UNSTABLE - Review Reports  ║'
            echo '╚════════════════════════════════════════╝'
            
            script {
                // Optional: Send unstable build email
                // emailext (
                //     subject: "⚠️ Test Build #${env.BUILD_NUMBER} - UNSTABLE",
                //     body: readFile('email-report.html'),
                //     mimeType: 'text/html',
                //     to: 'your-email@example.com'
                // )
            }
        }
        
        failure {
            echo '╔════════════════════════════════════════╗'
            echo '║   ❌ BUILD FAILED! Please Check Logs  ║'
            echo '╚════════════════════════════════════════╝'
            
            script {
                // Optional: Send failure email
                // emailext (
                //     subject: "❌ Test Build #${env.BUILD_NUMBER} - FAILED",
                //     body: readFile('email-report.html'),
                //     mimeType: 'text/html',
                //     to: 'your-email@example.com'
                // )
            }
        }
    }
}