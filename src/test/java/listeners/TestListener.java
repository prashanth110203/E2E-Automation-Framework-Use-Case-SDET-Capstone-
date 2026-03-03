package listeners;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;

import config.ConfigReader;
import utils.ScreenshotUtil;

public class TestListener implements ITestListener {
    private static final Logger logger = LogManager.getLogger(TestListener.class);

    @Override
    public void onStart(ITestContext context) {
        logger.info("========================================");
        logger.info("Test Suite Started: " + context.getName());
        logger.info("========================================");
        ExtentReportManager.createInstance();
    }

    @Override
    public void onFinish(ITestContext context) {
        logger.info("========================================");
        logger.info("Test Suite Finished: " + context.getName());
        logger.info("========================================");
        ExtentReportManager.getInstance().flush();
        logger.info("Extent Report generated at: " + ExtentReportManager.getReportPath());
    }

    @Override
    public void onTestStart(ITestResult result) {
        logger.info("----------------------------------------");
        logger.info("Test Started: " + result.getMethod().getMethodName());
        logger.info("----------------------------------------");

        ExtentTest test = ExtentReportManager.getInstance()
                .createTest(result.getMethod().getMethodName());

        if (result.getMethod().getDescription() != null) {
            test.info(result.getMethod().getDescription());
        }

        ExtentReportManager.setTest(test);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        logger.info("Test Passed: " + result.getMethod().getMethodName());

        ExtentTest test = ExtentReportManager.getTest();
        test.log(Status.PASS, "Test Passed: " + result.getMethod().getMethodName());

        ExtentReportManager.removeTest();
    }

    @Override
    public void onTestFailure(ITestResult result) {
        logger.error("Test Failed: " + result.getMethod().getMethodName());
        logger.error("Failure Reason: " + result.getThrowable());

        ExtentTest test = ExtentReportManager.getTest();

        test.log(Status.FAIL, "Test Failed: " + result.getMethod().getMethodName());
        test.log(Status.FAIL, result.getThrowable());

        if (ConfigReader.isScreenshotOnFailure()) {
            try {
                String base64Screenshot = ScreenshotUtil.getBase64Screenshot();
                if (base64Screenshot != null) {
                    test.fail("Screenshot on Failure",
                            MediaEntityBuilder.createScreenCaptureFromBase64String(base64Screenshot).build());
                }

                String screenshotPath = ScreenshotUtil.captureScreenshot(
                        result.getMethod().getMethodName()
                );
                if (screenshotPath != null) {
                    logger.info("Screenshot saved at: " + screenshotPath);
                }
            } catch (Exception e) {
                logger.error("Error capturing screenshot: " + e.getMessage());
            }
        }

        ExtentReportManager.removeTest();
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        logger.warn("Test Skipped: " + result.getMethod().getMethodName());

        ExtentTest test = ExtentReportManager.getTest();
        test.log(Status.SKIP, "Test Skipped: " + result.getMethod().getMethodName());

        if (result.getThrowable() != null) {
            test.log(Status.SKIP, result.getThrowable());
        }

        ExtentReportManager.removeTest();
    }
}