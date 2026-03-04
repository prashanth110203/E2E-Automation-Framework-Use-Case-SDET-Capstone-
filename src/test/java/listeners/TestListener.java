package listeners;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;

public class TestListener implements ITestListener {

    @Override
    public void onStart(ITestContext context) {
        System.out.println("========================================");
        System.out.println("🚀 Test Suite Started: " + context.getName());
        System.out.println("========================================");
        ExtentReportManager.getInstance();
    }

    @Override
    public void onTestStart(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        ExtentTest test = ExtentReportManager.getInstance().createTest(testName);
        ExtentReportManager.setTest(test);
        System.out.println("\n🧪 Starting Test: " + testName);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        ExtentReportManager.getTest().log(Status.PASS, "TEST PASSED: " + testName);
        System.out.println("✅ Test Passed: " + testName);
    }

    @Override
    public void onTestFailure(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        ExtentReportManager.getTest().log(Status.FAIL, "TEST FAILED: " + testName);
        
        if (result.getThrowable() != null) {
            ExtentReportManager.getTest().fail(result.getThrowable());
        }
        
        System.out.println("❌ Test Failed: " + testName);
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        ExtentReportManager.getTest().log(Status.SKIP, "TEST SKIPPED: " + testName);
        System.out.println("⏭️ Test Skipped: " + testName);
    }

    @Override
    public void onFinish(ITestContext context) {
        ExtentReportManager.flushReports();
        
        System.out.println("\n========================================");
        System.out.println("📊 TEST SUITE COMPLETED");
        System.out.println("========================================");
        System.out.println("✅ Passed: " + context.getPassedTests().size());
        System.out.println("❌ Failed: " + context.getFailedTests().size());
        System.out.println("⏭️ Skipped: " + context.getSkippedTests().size());
        System.out.println("📊 Report: " + ExtentReportManager.getReportPath());
        System.out.println("========================================\n");
    }
}