package utils;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

public class ExtentReportManager {
    
    private static ExtentReports extent;
    private static ExtentSparkReporter sparkReporter;
    private static ThreadLocal<ExtentTest> extentTest = new ThreadLocal<>();
    
    /**
     * Initialize Extent Reports
     */
    public static ExtentReports getInstance() {
        if (extent == null) {
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String reportPath = "test-output/reports/ExtentReport_" + timestamp + ".html";
            
            sparkReporter = new ExtentSparkReporter(reportPath);
            
            // Configure report
            sparkReporter.config().setDocumentTitle("E2E Automation Test Report");
            sparkReporter.config().setReportName("SauceDemo Test Execution Report");
            sparkReporter.config().setTheme(Theme.STANDARD);
            sparkReporter.config().setTimeStampFormat("yyyy-MM-dd HH:mm:ss");
            
            extent = new ExtentReports();
            extent.attachReporter(sparkReporter);
            
            // System information
            extent.setSystemInfo("Application", "SauceDemo");
            extent.setSystemInfo("Environment", "QA");
            extent.setSystemInfo("Browser", "Chrome");
            extent.setSystemInfo("OS", System.getProperty("os.name"));
            extent.setSystemInfo("Java Version", System.getProperty("java.version"));
            extent.setSystemInfo("User", System.getProperty("user.name"));
            
            System.out.println("✅ Extent Report initialized at: " + reportPath);
        }
        return extent;
    }
    
    /**
     * Create a new test
     */
    public static ExtentTest createTest(String testName) {
        ExtentTest test = getInstance().createTest(testName);
        extentTest.set(test);
        return test;
    }
    
    /**
     * Create a new test with description
     */
    public static ExtentTest createTest(String testName, String description) {
        ExtentTest test = getInstance().createTest(testName, description);
        extentTest.set(test);
        return test;
    }
    
    /**
     * Get current test
     */
    public static ExtentTest getTest() {
        return extentTest.get();
    }
    
    /**
     * Remove test from thread
     */
    public static void removeTest() {
        extentTest.remove();
    }
    
    /**
     * Flush report
     */
    public static void flush() {
        if (extent != null) {
            extent.flush();
            System.out.println("📊 Extent Report flushed successfully");
        }
    }
}