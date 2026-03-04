package tests;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

import utils.ExcelReader;

public class ExcelLoginTestData {

    private WebDriver driver;
    private WebDriverWait wait;
    private WebDriverWait extendedWait;
    private static ExtentReports extent;
    private ExtentTest test;
    private String screenshotFolder = "screenshots/";
    
    // Jenkins-optimized timeouts
    private static final int EXPLICIT_WAIT = 90;  // 90 seconds
    private static final int EXTENDED_WAIT = 120; // 120 seconds
    
    @BeforeSuite
    public void setUpSuite() {
        System.out.println("========================================");
        System.out.println(" Test Suite Started: E2E Data Driven Tests");
        System.out.println("========================================");

        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String reportPath = "test-output/reports/TestReport_" + timestamp + ".html";

        ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportPath);
        extent = new ExtentReports();
        extent.attachReporter(sparkReporter);
        System.out.println(" Extent Report initialized");

        new File(screenshotFolder).mkdirs();
    }

    @BeforeMethod
    public void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-extensions");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--disable-gpu");
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-logging"});

        driver = new ChromeDriver(options);
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(120));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(15));
        
        wait = new WebDriverWait(driver, Duration.ofSeconds(EXPLICIT_WAIT));
        extendedWait = new WebDriverWait(driver, Duration.ofSeconds(EXTENDED_WAIT));
    }

    @DataProvider(name = "excelData")
    public Object[][] getExcelData() throws IOException {
        List<Map<String, String>> testData = ExcelReader.readExcelData("testdata/TestData.xlsx", "TestData");
        System.out.println("✅ Successfully read " + testData.size() + " rows from Excel");
        
        Object[][] data = new Object[testData.size()][1];
        for (int i = 0; i < testData.size(); i++) {
            data[i][0] = testData.get(i);
        }
        return data;
    }

    @Test(dataProvider = "excelData")
    public void ecommerceTest(Map<String, String> data) {
        System.out.println("\n Starting Test: " + data.get("TestCase") + "\n");
        test = extent.createTest(data.get("TestCase"));
        printTestCaseHeader(data);

        String testType = data.get("TestType").toLowerCase();

        try {
            switch (testType) {
                case "login":
                    executeLoginTest(data);
                    break;
                case "addtocart":
                    executeAddToCartTest(data);
                    break;
                case "checkout":
                    executeCheckoutTest(data);
                    break;
                case "fullorder":
                    executeFullOrderTest(data);
                    break;
                case "loginfail":
                    executeLoginFailTest(data);
                    break;
                case "invalidurl":
                    executeInvalidUrlFailTest(data);
                    break;
                case "invaliddetails":
                    executeInvalidDetailsFailTest(data);
                    break;
            }
            test.log(Status.PASS, " Test Passed");
            System.out.println(" TEST PASSED: " + data.get("TestCase") + " ✅✅✅");
        } catch (Exception e) {
            test.log(Status.FAIL, " Test Failed: " + e.getMessage());
            System.out.println(" TEST FAILED: " + data.get("TestCase") + " ❌❌❌");
            System.out.println(" Reason: " + e.getMessage());
            Assert.fail("Test failed: " + e.getMessage());
        }
    }

    // ==================== LOGIN TEST ====================
    private void executeLoginTest(Map<String, String> data) throws InterruptedException {
        System.out.println(" EXECUTING: LOGIN TEST");
        
        driver.get("https://www.saucedemo.com/");
        Thread.sleep(3000); // Critical wait for page load
        
        WebElement usernameField = extendedWait.until(
            ExpectedConditions.presenceOfElementLocated(By.id("user-name"))
        );
        Thread.sleep(500);
        
        usernameField.clear();
        usernameField.sendKeys(data.get("Username"));
        System.out.println(" Entered username");
        
        WebElement passwordField = driver.findElement(By.id("password"));
        passwordField.clear();
        passwordField.sendKeys(data.get("Password"));
        System.out.println(" Entered password");
        
        Thread.sleep(1000);
        driver.findElement(By.id("login-button")).click();
        
        extendedWait.until(ExpectedConditions.urlContains("inventory"));
        Thread.sleep(2000);
        
        System.out.println(" LOGIN SUCCESSFUL");
    }

    // ==================== ADD TO CART TEST ====================
    private void executeAddToCartTest(Map<String, String> data) throws InterruptedException {
        System.out.println("📍 EXECUTING: ADD TO CART TEST");
        
        performLogin(data.get("Username"), data.get("Password"));
        System.out.println(" Logged in");
        Thread.sleep(2000);

        wait.until(ExpectedConditions.elementToBeClickable(
            By.id("add-to-cart-sauce-labs-backpack"))).click();
        System.out.println(" Added Backpack");
        Thread.sleep(1000);

        wait.until(ExpectedConditions.elementToBeClickable(
            By.id("add-to-cart-sauce-labs-bike-light"))).click();
        System.out.println(" Added Bike Light");
        Thread.sleep(1000);

        String cartCount = driver.findElement(By.className("shopping_cart_badge")).getText();
        Assert.assertEquals(cartCount, "2");
        System.out.println(" ADD TO CART PASSED");
    }

    // ==================== CHECKOUT TEST (FIXED) ====================
    private void executeCheckoutTest(Map<String, String> data) throws InterruptedException {
        System.out.println("📍 EXECUTING: CHECKOUT TEST");

        performLogin(data.get("Username"), data.get("Password"));
        Thread.sleep(2000);

        wait.until(ExpectedConditions.elementToBeClickable(
            By.id("add-to-cart-sauce-labs-backpack"))).click();
        Thread.sleep(1000);

        wait.until(ExpectedConditions.elementToBeClickable(
            By.className("shopping_cart_link"))).click();
        extendedWait.until(ExpectedConditions.urlContains("cart"));
        Thread.sleep(2000);

        wait.until(ExpectedConditions.elementToBeClickable(By.id("checkout"))).click();
        extendedWait.until(ExpectedConditions.urlContains("checkout-step-one"));
        Thread.sleep(3000); // Critical wait

        // Fill form with JavaScript execution for reliability
        WebElement firstName = extendedWait.until(
            ExpectedConditions.presenceOfElementLocated(By.id("first-name"))
        );
        Thread.sleep(500);
        ((JavascriptExecutor) driver).executeScript("arguments[0].value='';", firstName);
        firstName.sendKeys(data.get("FirstName"));
        Thread.sleep(300);

        WebElement lastName = driver.findElement(By.id("last-name"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].value='';", lastName);
        lastName.sendKeys(data.get("LastName"));
        Thread.sleep(300);

        WebElement postalCode = driver.findElement(By.id("postal-code"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].value='';", postalCode);
        postalCode.sendKeys(data.get("PostalCode"));
        Thread.sleep(2000);

        System.out.println(" Filled form");

        // Click continue with JavaScript
        WebElement continueButton = driver.findElement(By.id("continue"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", continueButton);
        
        // Extended wait for overview page
        extendedWait.until(ExpectedConditions.urlContains("checkout-step-two"));
        Thread.sleep(5000); // CRITICAL: Wait for page to fully render

        // Wait for summary with multiple attempts
        boolean summaryFound = false;
        for (int i = 0; i < 3; i++) {
            try {
                extendedWait.until(ExpectedConditions.presenceOfElementLocated(
                    By.className("summary_info")
                ));
                summaryFound = true;
                break;
            } catch (Exception e) {
                Thread.sleep(3000);
            }
        }

        if (!summaryFound) {
            Thread.sleep(5000); // Last attempt
        }

        String currentUrl = driver.getCurrentUrl();
        Assert.assertTrue(currentUrl.contains("checkout-step-two"),
            "Not on overview page. URL: " + currentUrl);

        System.out.println("CHECKOUT TEST PASSED");
    }

    // ==================== FULL ORDER TEST (FIXED) ====================
    private void executeFullOrderTest(Map<String, String> data) throws InterruptedException {
        System.out.println(" EXECUTING: FULL ORDER TEST");

        performLogin(data.get("Username"), data.get("Password"));
        Thread.sleep(2000);

        driver.findElement(By.id("add-to-cart-sauce-labs-backpack")).click();
        Thread.sleep(500);
        driver.findElement(By.id("add-to-cart-sauce-labs-bike-light")).click();
        Thread.sleep(1000);
        System.out.println(" Step 2: Added products");

        driver.findElement(By.className("shopping_cart_link")).click();
        extendedWait.until(ExpectedConditions.urlContains("cart"));
        Thread.sleep(2000);
        System.out.println(" Step 3: Opened cart");

        driver.findElement(By.id("checkout")).click();
        extendedWait.until(ExpectedConditions.urlContains("checkout-step-one"));
        Thread.sleep(3000);
        System.out.println(" Step 4: Started checkout");

        WebElement fn = extendedWait.until(ExpectedConditions.presenceOfElementLocated(By.id("first-name")));
        Thread.sleep(500);
        fn.clear();
        fn.sendKeys(data.get("FirstName"));
        Thread.sleep(300);

        WebElement ln = driver.findElement(By.id("last-name"));
        ln.clear();
        ln.sendKeys(data.get("LastName"));
        Thread.sleep(300);

        WebElement pc = driver.findElement(By.id("postal-code"));
        pc.clear();
        pc.sendKeys(data.get("PostalCode"));
        Thread.sleep(2000);
        System.out.println(" Step 5: Filled information");

        WebElement cont = driver.findElement(By.id("continue"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", cont);
        
        extendedWait.until(ExpectedConditions.urlContains("checkout-step-two"));
        Thread.sleep(5000);

        // Multiple attempts to find summary
        boolean found = false;
        for (int i = 0; i < 5; i++) {
            try {
                extendedWait.until(ExpectedConditions.presenceOfElementLocated(
                    By.className("summary_info")));
                found = true;
                break;
            } catch (Exception e) {
                Thread.sleep(2000);
            }
        }

        Thread.sleep(3000);
        System.out.println(" Step 6: Overview loaded");

        driver.findElement(By.id("finish")).click();
        extendedWait.until(ExpectedConditions.urlContains("checkout-complete"));
        Thread.sleep(2000);

        WebElement confirm = extendedWait.until(
            ExpectedConditions.visibilityOfElementLocated(By.className("complete-header"))
        );
        Assert.assertTrue(confirm.getText().contains("Thank you"));

        System.out.println("  Step 8: ORDER COMPLETE!");
        System.out.println(" FULL ORDER TEST PASSED! ");
    }

    // ==================== NEGATIVE TESTS ====================
    private void executeLoginFailTest(Map<String, String> data) throws InterruptedException {
        System.out.println(" EXECUTING: LOGIN FAIL TEST (Expected: FAIL)");
        System.out.println(" This test will INTENTIONALLY FAIL\n");

        driver.get("https://www.saucedemo.com/");
        Thread.sleep(3000);

        String username = data.get("Username");
        if (username != null && !username.isEmpty()) {
            driver.findElement(By.id("user-name")).sendKeys(username);
        }

        String password = data.get("Password");
        if (password != null && !password.isEmpty()) {
            driver.findElement(By.id("password")).sendKeys(password);
        }

        Thread.sleep(1000);
        System.out.println(" Login should have failed!");
        
        Assert.assertTrue(driver.getCurrentUrl().contains("inventory"),
            "LOGIN SHOULD FAIL! User: " + username);
    }

    private void executeInvalidUrlFailTest(Map<String, String> data) {
        System.out.println(" EXECUTING: INVALID URL TEST (Expected: FAIL)");
        try {
            driver.get("https://invalid-website-that-does-not-exist.com");
            Assert.fail("INVALID URL TEST FAILED!");
        } catch (Exception e) {
            System.out.println(" Error: " + e.getMessage());
            Assert.fail("INVALID URL TEST FAILED! Error: " + e.getMessage());
        }
    }

    private void executeInvalidDetailsFailTest(Map<String, String> data) throws InterruptedException {
        System.out.println("📍 EXECUTING: INVALID DETAILS TEST (Expected: FAIL)");

        performLogin(data.get("Username"), data.get("Password"));
        Thread.sleep(2000);
        driver.findElement(By.id("add-to-cart-sauce-labs-backpack")).click();
        Thread.sleep(1000);
        driver.findElement(By.className("shopping_cart_link")).click();
        Thread.sleep(2000);
        driver.findElement(By.id("checkout")).click();
        extendedWait.until(ExpectedConditions.urlContains("checkout-step-one"));
        Thread.sleep(2000);

        driver.findElement(By.id("continue")).click();
        Thread.sleep(2000);

        Assert.assertTrue(driver.getCurrentUrl().contains("checkout-step-two"),
            "CHECKOUT SHOULD FAIL! Empty details not allowed!");
    }

    // ==================== HELPER METHODS ====================
    private void performLogin(String username, String password) throws InterruptedException {
        driver.get("https://www.saucedemo.com/");
        Thread.sleep(3000);
        
        WebElement user = extendedWait.until(ExpectedConditions.presenceOfElementLocated(By.id("user-name")));
        user.clear();
        user.sendKeys(username);
        Thread.sleep(500);
        
        driver.findElement(By.id("password")).sendKeys(password);
        Thread.sleep(1000);
        driver.findElement(By.id("login-button")).click();
        
        extendedWait.until(ExpectedConditions.urlContains("inventory"));
        Thread.sleep(2000);
    }

    private void printTestCaseHeader(Map<String, String> data) {
        System.out.println("==================================================");
        System.out.println(" TEST CASE: " + data.get("TestCase"));
        System.out.println(" Description: " + data.get("Description"));
        System.out.println(" Username: " + data.get("Username"));
        System.out.println(" Expected Result: " + data.get("ExpectedResult"));
        System.out.println(" Test Type: " + data.get("TestType"));
        System.out.println("==================================================\n");
    }

    @AfterMethod
    public void tearDown(ITestResult result) {
        if (result.getStatus() == ITestResult.FAILURE) {
            captureScreenshot("FAILED_" + result.getName());
        } else if (result.getStatus() == ITestResult.SUCCESS) {
            captureScreenshot("PASS_" + result.getName());
        }

        if (driver != null) {
            driver.quit();
            System.out.println(" Browser closed");
            System.out.println("==================================================\n");
        }
    }

    private void captureScreenshot(String name) {
        try {
            File source = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String destination = screenshotFolder + name + "_" + timestamp + ".png";
            FileUtils.copyFile(source, new File(destination));
            System.out.println(" Screenshot saved: " + new File(destination).getAbsolutePath());
        } catch (Exception e) {
            System.out.println(" Screenshot failed: " + e.getMessage());
        }
    }

    @AfterSuite
    public void tearDownSuite() {
        if (extent != null) {
            extent.flush();
        }
        System.out.println("\n========================================");
        System.out.println(" TEST SUITE COMPLETED");
        System.out.println("========================================");
    }
}