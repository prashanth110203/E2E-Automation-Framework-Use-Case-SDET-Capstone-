package tests;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.time.Duration;

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
import org.testng.annotations.*;

import utils.ExcelReader;

public class ExcelLoginTestData {

    WebDriver driver;
    WebDriverWait wait;
    JavascriptExecutor js;
    
    String excelPath = System.getProperty("user.dir") 
                       + "/src/test/resources/testdata/LoginTestData.xlsx";
    
    String baseUrl = "https://www.saucedemo.com/";
    String invalidUrl = "https://www.invalidwebsite12345.com/";
    
    String screenshotPath = System.getProperty("user.dir") + "/screenshots/";

    @BeforeClass
    public void createScreenshotFolder() {
        try {
            Files.createDirectories(Paths.get(screenshotPath));
            System.out.println("✅ Screenshot folder created: " + screenshotPath);
        } catch (IOException e) {
            System.out.println("⚠️ Could not create screenshot folder: " + e.getMessage());
        }
    }

    @BeforeMethod
    public void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--start-maximized");
        options.addArguments("--disable-gpu");
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-popup-blocking");
        
        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(3));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(60));
        
        wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        js = (JavascriptExecutor) driver;
    }

    @DataProvider(name = "loginData")
    public Object[][] getData() {
        return ExcelReader.getExcelDataAsArray(excelPath, "Sheet1");
    }

    @Test(dataProvider = "loginData")
    public void ecommerceTest(Map<String, String> testData) {
        
        String testCase = testData.get("TestCase");
        String username = testData.get("Username");
        String password = testData.get("Password");
        String expectedResult = testData.get("ExpectedResult");
        String testType = testData.get("TestType");
        String firstName = testData.get("FirstName");
        String lastName = testData.get("LastName");
        String postalCode = testData.get("PostalCode");
        String description = testData.get("Description");
        
        System.out.println("\n==================================================");
        System.out.println("🧪 TEST CASE: " + testCase);
        System.out.println("📝 Description: " + description);
        System.out.println("👤 Username: " + username);
        System.out.println("🔑 Password: " + password);
        System.out.println("🎯 Expected Result: " + expectedResult.toUpperCase());
        System.out.println("📋 Test Type: " + testType);
        System.out.println("==================================================\n");
        
        try {
            switch (testType.toLowerCase()) {
                case "login":
                    executeSimpleLoginTest(testCase, username, password);
                    break;
                case "addtocart":
                    executeAddToCartTest(testCase, username, password);
                    break;
                case "checkout":
                    executeCheckoutTest(testCase, username, password, firstName, lastName, postalCode);
                    break;
                case "fullorder":
                    executeFullOrderTest(testCase, username, password, firstName, lastName, postalCode);
                    break;
                case "loginfail":
                    executeLoginFailTest(testCase, username, password);
                    break;
                case "invalidurl":
                    executeInvalidUrlFailTest(testCase);
                    break;
                case "invaliddetails":
                    executeInvalidDetailsFailTest(testCase, username, password);
                    break;
                default:
                    System.out.println("⚠️ Unknown test type: " + testType);
            }
        } catch (Exception e) {
            System.out.println("❌ Unexpected error: " + e.getMessage());
            throw e;
        }
    }
    
    // ========== 1. SIMPLE LOGIN TEST (PASS) ==========
    private void executeSimpleLoginTest(String testCase, String username, String password) {
        System.out.println("📍 EXECUTING: LOGIN TEST (Expected: PASS)");
        
        driver.get(baseUrl);
        waitForPageReady();
        System.out.println("✅ Opened URL: " + baseUrl);
        
        WebElement usernameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("user-name")));
        usernameField.clear();
        usernameField.sendKeys(username);
        System.out.println("✅ Entered username: " + username);
        
        driver.findElement(By.id("password")).sendKeys(password);
        System.out.println("✅ Entered password");
        
        jsClick(driver.findElement(By.id("login-button")));
        System.out.println("✅ Clicked Login button");
        
        waitForPageReady();
        wait.until(ExpectedConditions.urlContains("inventory"));
        
        WebElement title = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("title")));
        Assert.assertEquals(title.getText(), "Products");
        System.out.println("✅ LOGIN SUCCESSFUL - Products page displayed");
        
        takeScreenshot("PASS_" + testCase);
    }
    
    // ========== 2. ADD TO CART TEST (PASS) ==========
    private void executeAddToCartTest(String testCase, String username, String password) {
        System.out.println("📍 EXECUTING: ADD TO CART TEST (Expected: PASS)");
        
        driver.get(baseUrl);
        waitForPageReady();
        
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("user-name"))).sendKeys(username);
        driver.findElement(By.id("password")).sendKeys(password);
        jsClick(driver.findElement(By.id("login-button")));
        waitForPageReady();
        System.out.println("✅ Logged in");
        
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("inventory_list")));
        sleep(1000);
        
        jsClick(wait.until(ExpectedConditions.elementToBeClickable(By.id("add-to-cart-sauce-labs-backpack"))));
        System.out.println("✅ Added Backpack");
        sleep(1500);
        
        jsClick(wait.until(ExpectedConditions.elementToBeClickable(By.id("add-to-cart-sauce-labs-bike-light"))));
        System.out.println("✅ Added Bike Light");
        sleep(1500);
        
        WebElement cartBadge = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("shopping_cart_badge")));
        Assert.assertEquals(cartBadge.getText(), "2");
        System.out.println("✅ ADD TO CART PASSED - Cart has 2 items");
        
        takeScreenshot("PASS_" + testCase);
    }
    
    // ========== 3. CHECKOUT TEST (PASS) - FIXED ==========
    private void executeCheckoutTest(String testCase, String username, String password, 
                                      String firstName, String lastName, String postalCode) {
        System.out.println("📍 EXECUTING: CHECKOUT TEST (Expected: PASS)");
        
        driver.get(baseUrl);
        waitForPageReady();
        
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("user-name"))).sendKeys(username);
        driver.findElement(By.id("password")).sendKeys(password);
        jsClick(driver.findElement(By.id("login-button")));
        waitForPageReady();
        System.out.println("✅ Logged in");
        
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("inventory_list")));
        sleep(1000);
        
        jsClick(wait.until(ExpectedConditions.elementToBeClickable(By.id("add-to-cart-sauce-labs-backpack"))));
        System.out.println("✅ Added product");
        sleep(1500);
        
        jsClick(wait.until(ExpectedConditions.elementToBeClickable(By.className("shopping_cart_link"))));
        waitForPageReady();
        System.out.println("✅ Opened cart");
        
        jsClick(wait.until(ExpectedConditions.elementToBeClickable(By.id("checkout"))));
        waitForPageReady();
        System.out.println("✅ Clicked checkout");
        
        // Fill form with explicit waits
        WebElement firstNameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("first-name")));
        firstNameField.clear();
        firstNameField.sendKeys(firstName);
        sleep(500);
        
        WebElement lastNameField = driver.findElement(By.id("last-name"));
        lastNameField.clear();
        lastNameField.sendKeys(lastName);
        sleep(500);
        
        WebElement postalCodeField = driver.findElement(By.id("postal-code"));
        postalCodeField.clear();
        postalCodeField.sendKeys(postalCode);
        sleep(500);
        
        System.out.println("✅ Filled checkout form");
        
        jsClick(wait.until(ExpectedConditions.elementToBeClickable(By.id("continue"))));
        waitForPageReady();
        sleep(2000); // Extra wait for overview page
        
        WebElement overviewTitle = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("title")));
        Assert.assertEquals(overviewTitle.getText(), "Checkout: Overview");
        System.out.println("✅ CHECKOUT TEST PASSED - Overview displayed");
        
        takeScreenshot("PASS_" + testCase);
    }
    
    // ========== 4. FULL ORDER TEST (PASS) - FIXED ==========
    private void executeFullOrderTest(String testCase, String username, String password,
                                       String firstName, String lastName, String postalCode) {
        System.out.println("📍 EXECUTING: FULL ORDER TEST (Expected: PASS)");
        
        driver.get(baseUrl);
        waitForPageReady();
        
        // Login
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("user-name"))).sendKeys(username);
        driver.findElement(By.id("password")).sendKeys(password);
        jsClick(driver.findElement(By.id("login-button")));
        waitForPageReady();
        System.out.println("✅ Step 1: Logged in");
        
        // Add products
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("inventory_list")));
        sleep(1000);
        
        jsClick(wait.until(ExpectedConditions.elementToBeClickable(By.id("add-to-cart-sauce-labs-backpack"))));
        sleep(1500);
        
        jsClick(wait.until(ExpectedConditions.elementToBeClickable(By.id("add-to-cart-sauce-labs-bike-light"))));
        sleep(1500);
        System.out.println("✅ Step 2: Added products");
        
        // Cart
        jsClick(wait.until(ExpectedConditions.elementToBeClickable(By.className("shopping_cart_link"))));
        waitForPageReady();
        System.out.println("✅ Step 3: Opened cart");
        
        // Checkout
        jsClick(wait.until(ExpectedConditions.elementToBeClickable(By.id("checkout"))));
        waitForPageReady();
        System.out.println("✅ Step 4: Started checkout");
        
        // Fill form
        WebElement firstNameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("first-name")));
        firstNameField.clear();
        firstNameField.sendKeys(firstName);
        sleep(500);
        
        driver.findElement(By.id("last-name")).sendKeys(lastName);
        sleep(500);
        
        driver.findElement(By.id("postal-code")).sendKeys(postalCode);
        sleep(500);
        
        jsClick(wait.until(ExpectedConditions.elementToBeClickable(By.id("continue"))));
        waitForPageReady();
        sleep(2000);
        System.out.println("✅ Step 5: Filled information");
        
        // Verify overview page is loaded
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("summary_info")));
        System.out.println("✅ Step 6: Overview page loaded");
        
        // Click Finish with extra waits
        WebElement finishButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("finish")));
        scrollToElement(finishButton);
        sleep(1000);
        jsClick(finishButton);
        waitForPageReady();
        sleep(2000);
        System.out.println("✅ Step 7: Clicked finish");
        
        // Verify order complete
        WebElement thankYou = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("complete-header")));
        Assert.assertEquals(thankYou.getText(), "Thank you for your order!");
        System.out.println("✅ Step 8: ORDER COMPLETE!");
        System.out.println("🎉🎉🎉 FULL ORDER TEST PASSED! 🎉🎉🎉");
        
        takeScreenshot("PASS_" + testCase);
    }
    
    // ========== 5. LOGIN FAIL TEST (FAIL) ==========
    private void executeLoginFailTest(String testCase, String username, String password) {
        System.out.println("📍 EXECUTING: LOGIN FAIL TEST (Expected: FAIL)");
        System.out.println("⚠️ This test will INTENTIONALLY FAIL\n");
        
        driver.get(baseUrl);
        waitForPageReady();
        System.out.println("✅ Opened URL");
        
        WebElement usernameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("user-name")));
        usernameField.clear();
        if (username != null && !username.isEmpty()) {
            usernameField.sendKeys(username);
            System.out.println("✅ Entered username: " + username);
        } else {
            System.out.println("⚠️ Username is empty");
        }
        
        WebElement passwordField = driver.findElement(By.id("password"));
        passwordField.clear();
        if (password != null && !password.isEmpty()) {
            passwordField.sendKeys(password);
            System.out.println("✅ Entered password");
        } else {
            System.out.println("⚠️ Password is empty");
        }
        
        jsClick(driver.findElement(By.id("login-button")));
        sleep(2000);
        
        String currentUrl = driver.getCurrentUrl();
        System.out.println("❌ Current URL: " + currentUrl);
        System.out.println("❌ Login should have failed!");
        
        Assert.assertTrue(currentUrl.contains("inventory"), 
            "LOGIN SHOULD FAIL! User: " + username);
    }
    
    // ========== 6. INVALID URL TEST (FAIL) ==========
    private void executeInvalidUrlFailTest(String testCase) {
        System.out.println("📍 EXECUTING: INVALID URL TEST (Expected: FAIL)");
        System.out.println("⚠️ This test will INTENTIONALLY FAIL\n");
        
        try {
            driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(10));
            driver.get(invalidUrl);
            sleep(2000);
            System.out.println("❌ Invalid URL should not load!");
            Assert.fail("INVALID URL SHOULD FAIL!");
        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage().split("\n")[0]);
            Assert.fail("INVALID URL TEST FAILED! Error: " + e.getMessage().split("\n")[0]);
        }
    }
    
    // ========== 7. INVALID DETAILS TEST (FAIL) ==========
    private void executeInvalidDetailsFailTest(String testCase, String username, String password) {
        System.out.println("📍 EXECUTING: INVALID DETAILS TEST (Expected: FAIL)");
        System.out.println("⚠️ This test will INTENTIONALLY FAIL\n");
        
        driver.get(baseUrl);
        waitForPageReady();
        
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("user-name"))).sendKeys(username);
        driver.findElement(By.id("password")).sendKeys(password);
        jsClick(driver.findElement(By.id("login-button")));
        waitForPageReady();
        System.out.println("✅ Logged in");
        
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("inventory_list")));
        sleep(1000);
        
        jsClick(wait.until(ExpectedConditions.elementToBeClickable(By.id("add-to-cart-sauce-labs-backpack"))));
        sleep(1500);
        System.out.println("✅ Added product");
        
        jsClick(wait.until(ExpectedConditions.elementToBeClickable(By.className("shopping_cart_link"))));
        waitForPageReady();
        System.out.println("✅ Opened cart");
        
        jsClick(wait.until(ExpectedConditions.elementToBeClickable(By.id("checkout"))));
        waitForPageReady();
        System.out.println("✅ Checkout page opened");
        
        System.out.println("❌ Attempting checkout with EMPTY details");
        jsClick(driver.findElement(By.id("continue")));
        sleep(1000);
        
        String currentUrl = driver.getCurrentUrl();
        System.out.println("❌ Current URL: " + currentUrl);
        System.out.println("❌ Should NOT proceed with empty details!");
        
        Assert.assertTrue(currentUrl.contains("checkout-step-two"), 
            "CHECKOUT SHOULD FAIL! Empty details not allowed!");
    }
    
    // ========== HELPER METHODS ==========
    private void waitForPageReady() {
        try {
            wait.until(driver -> js.executeScript("return document.readyState").equals("complete"));
            sleep(500);
        } catch (Exception e) {
            // Ignore
        }
    }
    
    private void jsClick(WebElement element) {
        try {
            js.executeScript("arguments[0].scrollIntoView(true);", element);
            sleep(300);
            js.executeScript("arguments[0].click();", element);
        } catch (Exception e) {
            element.click();
        }
    }
    
    private void scrollToElement(WebElement element) {
        js.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", element);
    }
    
    private void sleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    public String takeScreenshot(String testName) {
        try {
            TakesScreenshot ts = (TakesScreenshot) driver;
            File source = ts.getScreenshotAs(OutputType.FILE);
            
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String fileName = testName.replaceAll(" ", "_") + "_" + timestamp + ".png";
            String destination = screenshotPath + fileName;
            
            File finalDestination = new File(destination);
            FileUtils.copyFile(source, finalDestination);
            
            System.out.println("📸 Screenshot saved: " + destination);
            return destination;
            
        } catch (IOException e) {
            System.out.println("❌ Failed to take screenshot: " + e.getMessage());
            return null;
        }
    }

    @AfterMethod
    public void tearDown(ITestResult result) {
        String testName = "Test";
        
        Object[] parameters = result.getParameters();
        if (parameters != null && parameters.length > 0) {
            @SuppressWarnings("unchecked")
            Map<String, String> testData = (Map<String, String>) parameters[0];
            testName = testData.get("TestCase");
        }
        
        if (result.getStatus() == ITestResult.FAILURE) {
            System.out.println("\n❌❌❌ TEST FAILED: " + testName + " ❌❌❌");
            takeScreenshot("FAILED_" + testName);
        } else if (result.getStatus() == ITestResult.SUCCESS) {
            System.out.println("\n✅✅✅ TEST PASSED: " + testName + " ✅✅✅");
        }
        
        if (driver != null) {
            driver.quit();
            System.out.println("✅ Browser closed");
            System.out.println("==================================================\n");
        }
    }
}