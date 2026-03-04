package listeners;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

// Make sure these imports are correct
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

public class ExtentReportManager {
    
    private static ExtentReports extent;
    private static ThreadLocal<ExtentTest> extentTest = new ThreadLocal<>();
    private static String reportPath;

    public static ExtentReports createInstance() {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String reportFolder = "test-output/reports";
        String reportName = "TestReport";

        // Create directory if not exists
        File directory = new File(reportFolder);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        reportPath = reportFolder + File.separator + reportName + "_" + timestamp + ".html";

        ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportPath);

        sparkReporter.config().setTheme(Theme.STANDARD);
        sparkReporter.config().setDocumentTitle("E2E Automation Test Report");
        sparkReporter.config().setReportName("SauceDemo Test Execution Report");
        sparkReporter.config().setEncoding("utf-8");

        extent = new ExtentReports();
        extent.attachReporter(sparkReporter);

        extent.setSystemInfo("Application", "SauceDemo");
        extent.setSystemInfo("Environment", "QA");
        extent.setSystemInfo("Browser", "Chrome");
        extent.setSystemInfo("OS", System.getProperty("os.name"));
        extent.setSystemInfo("User", System.getProperty("user.name"));

        System.out.println("📊 Extent Report initialized at: " + reportPath);
        
        return extent;
    }

    public static ExtentReports getInstance() {
        if (extent == null) {
            createInstance();
        }
        return extent;
    }

    public static void setTest(ExtentTest test) {
        extentTest.set(test);
    }

    public static ExtentTest getTest() {
        return extentTest.get();
    }

    public static void removeTest() {
        extentTest.remove();
    }

    public static String getReportPath() {
        return reportPath;
    }
    
    public static void flushReports() {
        if (extent != null) {
            extent.flush();
            System.out.println("📊 Extent Report generated at: " + reportPath);
        }
    }
}