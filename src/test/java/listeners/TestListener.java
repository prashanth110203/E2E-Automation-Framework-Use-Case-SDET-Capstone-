package listeners;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;

import utils.ExtentReportManager;

public class TestListener implements ITestListener {
    
    private ExtentReports extent;
    
    @Override
    public void onStart(ITestContext context) {
        System.out.println("========================================");
        System.out.println("🚀 Test Suite Started: " + context.getName());
        System.out.println("========================================");
        
        extent = ExtentReportManager.getInstance();
    }
    
    @Override
    public void onTestStart(ITestResult result) {
        System.out.println("\n🧪 Starting Test: " + result.getMethod().getMethodName());
        
        ExtentTest test = ExtentReportManager.createTest(
            result.getMethod().getMethodName(),
            result.getMethod().getDescription()
        );
        
        test.info("Test execution started");
    }
    
    @Override
    public void onTestSuccess(ITestResult result) {
        System.out.println("✅ Test Passed: " + result.getMethod().getMethodName());
        
        ExtentTest test = ExtentReportManager.getTest();
        if (test != null) {
            test.log(Status.PASS, "✅ Test Passed: " + result.getMethod().getMethodName());
        }
        
        ExtentReportManager.removeTest();
    }
    
    @Override
    public void onTestFailure(ITestResult result) {
        System.out.println("❌ Test Failed: " + result.getMethod().getMethodName());
        System.out.println("❌ Reason: " + result.getThrowable().getMessage());
        
        ExtentTest test = ExtentReportManager.getTest();
        if (test != null) {
            test.log(Status.FAIL, "❌ Test Failed: " + result.getMethod().getMethodName());
            test.log(Status.FAIL, result.getThrowable());
        }
        
        ExtentReportManager.removeTest();
    }
    
    @Override
    public void onTestSkipped(ITestResult result) {
        System.out.println("⏭️ Test Skipped: " + result.getMethod().getMethodName());
        
        ExtentTest test = ExtentReportManager.getTest();
        if (test != null) {
            test.log(Status.SKIP, "⏭️ Test Skipped: " + result.getMethod().getMethodName());
        }
        
        ExtentReportManager.removeTest();
    }
    
    @Override
    public void onFinish(ITestContext context) {
        System.out.println("\n========================================");
        System.out.println("📊 Test Suite Completed: " + context.getName());
        System.out.println("✅ Passed: " + context.getPassedTests().size());
        System.out.println("❌ Failed: " + context.getFailedTests().size());
        System.out.println("⏭️ Skipped: " + context.getSkippedTests().size());
        System.out.println("========================================");
        
        ExtentReportManager.flush();
    }
}