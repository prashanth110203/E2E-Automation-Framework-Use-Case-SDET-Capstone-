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
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--start-maximized");
        options.setPageLoadStrategy(org.openqa.selenium.PageLoadStrategy.NORMAL);
        
        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(60));
        driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(30));
        
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
        
        switch (testType.toLowerCase()) {
            case "login":
                executeLoginTest(testCase, username, password);
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
                // This test MUST FAIL - expects login to succeed but it won't
                executeLoginFailTest(testCase, username, password);
                break;
            case "invalidurl":
                // This test MUST FAIL - expects page to load but it won't
                executeInvalidUrlFailTest(testCase);
                break;
            case "invaliddetails":
                // This test MUST FAIL - expects checkout to succeed with empty details
                executeInvalidDetailsFailTest(testCase, username, password);
                break;
            default:
                System.out.println("⚠️ Unknown test type: " + testType);
        }
    }
    
    // ========== HELPER METHODS ==========
    private void clickElement(WebElement element) {
        try {
            element.click();
        } catch (Exception e) {
            js.executeScript("arguments[0].click();", element);
        }
    }
    
    private void waitForPageLoad() {
        wait.until(driver -> js.executeScript("return document.readyState").equals("complete"));
        sleep(500);
    }
    
    private void sleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    // ========== PASS: LOGIN TEST ==========
    private void executeLoginTest(String testCase, String username, String password) {
        System.out.println("📍 EXECUTING: LOGIN TEST (Expected: PASS)");
        
        driver.get(baseUrl);
        waitForPageLoad();
        System.out.println("✅ Opened URL: " + baseUrl);
        
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("user-name"))).sendKeys(username);
        System.out.println("✅ Entered username: " + username);
        
        driver.findElement(By.id("password")).sendKeys(password);
        System.out.println("✅ Entered password: " + password);
        
        clickElement(driver.findElement(By.id("login-button")));
        System.out.println("✅ Clicked Login button");
        
        waitForPageLoad();
        
        wait.until(ExpectedConditions.urlContains("inventory"));
        WebElement productsTitle = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("title")));
        Assert.assertEquals(productsTitle.getText(), "Products");
        System.out.println("✅ LOGIN SUCCESSFUL - Products page displayed");
        
        takeScreenshot("PASS_" + testCase);
    }
    
    // ========== PASS: ADD TO CART TEST ==========
    private void executeAddToCartTest(String testCase, String username, String password) {
        System.out.println("📍 EXECUTING: ADD TO CART TEST (Expected: PASS)");
        
        driver.get(baseUrl);
        waitForPageLoad();
        System.out.println("✅ Opened URL: " + baseUrl);
        
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("user-name"))).sendKeys(username);
        driver.findElement(By.id("password")).sendKeys(password);
        clickElement(driver.findElement(By.id("login-button")));
        waitForPageLoad();
        System.out.println("✅ LOGIN SUCCESSFUL");
        
        System.out.println("\n📍 STEP: ADD PRODUCTS TO CART");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("inventory_list")));
        
        clickElement(wait.until(ExpectedConditions.elementToBeClickable(
            By.id("add-to-cart-sauce-labs-backpack"))));
        System.out.println("✅ Added 'Sauce Labs Backpack' to cart");
        sleep(1000);
        
        clickElement(wait.until(ExpectedConditions.elementToBeClickable(
            By.id("add-to-cart-sauce-labs-bike-light"))));
        System.out.println("✅ Added 'Sauce Labs Bike Light' to cart");
        sleep(1000);
        
        WebElement cartBadge = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.className("shopping_cart_badge")));
        Assert.assertEquals(cartBadge.getText(), "2", "Cart should have 2 items");
        System.out.println("✅ Cart badge shows: " + cartBadge.getText() + " items");
        System.out.println("✅ ADD TO CART TEST PASSED");
        
        takeScreenshot("PASS_" + testCase);
    }
    
    // ========== PASS: CHECKOUT TEST ==========
    private void executeCheckoutTest(String testCase, String username, String password, 
                                      String firstName, String lastName, String postalCode) {
        System.out.println("📍 EXECUTING: CHECKOUT TEST (Expected: PASS)");
        
        driver.get(baseUrl);
        waitForPageLoad();
        
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("user-name"))).sendKeys(username);
        driver.findElement(By.id("password")).sendKeys(password);
        clickElement(driver.findElement(By.id("login-button")));
        waitForPageLoad();
        System.out.println("✅ LOGIN SUCCESSFUL");
        
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("inventory_list")));
        clickElement(wait.until(ExpectedConditions.elementToBeClickable(
            By.id("add-to-cart-sauce-labs-backpack"))));
        System.out.println("✅ Added product to cart");
        sleep(1000);
        
        clickElement(wait.until(ExpectedConditions.elementToBeClickable(By.className("shopping_cart_link"))));
        waitForPageLoad();
        System.out.println("✅ Cart page displayed");
        
        clickElement(wait.until(ExpectedConditions.elementToBeClickable(By.id("checkout"))));
        waitForPageLoad();
        System.out.println("✅ Checkout page displayed");
        
        driver.findElement(By.id("first-name")).sendKeys(firstName);
        driver.findElement(By.id("last-name")).sendKeys(lastName);
        driver.findElement(By.id("postal-code")).sendKeys(postalCode);
        System.out.println("✅ Filled checkout information");
        
        clickElement(wait.until(ExpectedConditions.elementToBeClickable(By.id("continue"))));
        waitForPageLoad();
        
        WebElement overviewTitle = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("title")));
        Assert.assertEquals(overviewTitle.getText(), "Checkout: Overview");
        System.out.println("✅ CHECKOUT TEST PASSED - Overview page displayed");
        
        takeScreenshot("PASS_" + testCase);
    }
    
    // ========== PASS: FULL ORDER TEST ==========
    private void executeFullOrderTest(String testCase, String username, String password,
                                       String firstName, String lastName, String postalCode) {
        System.out.println("📍 EXECUTING: FULL ORDER TEST (Expected: PASS)");
        System.out.println("🛒 Complete E2E Flow: Login → Add to Cart → Cart → Checkout → Order Complete\n");
        
        driver.get(baseUrl);
        waitForPageLoad();
        
        // LOGIN
        System.out.println("📍 STEP 1: LOGIN");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("user-name"))).sendKeys(username);
        driver.findElement(By.id("password")).sendKeys(password);
        clickElement(driver.findElement(By.id("login-button")));
        waitForPageLoad();
        System.out.println("✅ Login successful");
        
        // ADD TO CART
        System.out.println("\n📍 STEP 2: ADD PRODUCTS TO CART");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("inventory_list")));
        clickElement(wait.until(ExpectedConditions.elementToBeClickable(
            By.id("add-to-cart-sauce-labs-backpack"))));
        System.out.println("✅ Added 'Sauce Labs Backpack'");
        sleep(1000);
        
        clickElement(wait.until(ExpectedConditions.elementToBeClickable(
            By.id("add-to-cart-sauce-labs-bike-light"))));
        System.out.println("✅ Added 'Sauce Labs Bike Light'");
        sleep(1000);
        
        // GO TO CART
        System.out.println("\n📍 STEP 3: GO TO CART");
        clickElement(wait.until(ExpectedConditions.elementToBeClickable(By.className("shopping_cart_link"))));
        waitForPageLoad();
        System.out.println("✅ Cart page displayed");
        
        // CHECKOUT
        System.out.println("\n📍 STEP 4: PROCEED TO CHECKOUT");
        clickElement(wait.until(ExpectedConditions.elementToBeClickable(By.id("checkout"))));
        waitForPageLoad();
        System.out.println("✅ Checkout page displayed");
        
        // FILL INFO
        System.out.println("\n📍 STEP 5: FILL CHECKOUT INFORMATION");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("first-name"))).sendKeys(firstName);
        driver.findElement(By.id("last-name")).sendKeys(lastName);
        driver.findElement(By.id("postal-code")).sendKeys(postalCode);
        clickElement(wait.until(ExpectedConditions.elementToBeClickable(By.id("continue"))));
        waitForPageLoad();
        System.out.println("✅ Information filled");
        
        // OVERVIEW
        System.out.println("\n📍 STEP 6: CHECKOUT OVERVIEW");
        WebElement subtotal = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.className("summary_subtotal_label")));
        System.out.println("   " + subtotal.getText());
        WebElement total = driver.findElement(By.className("summary_total_label"));
        System.out.println("   " + total.getText());
        
        // FINISH
        System.out.println("\n📍 STEP 7: FINISH ORDER");
        clickElement(wait.until(ExpectedConditions.elementToBeClickable(By.id("finish"))));
        waitForPageLoad();
        
        // VERIFY ORDER COMPLETE
        System.out.println("\n📍 STEP 8: VERIFY ORDER CONFIRMATION");
        WebElement thankYou = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("complete-header")));
        Assert.assertEquals(thankYou.getText(), "Thank you for your order!");
        System.out.println("✅ " + thankYou.getText());
        
        takeScreenshot("PASS_Order_Complete_" + testCase);
        System.out.println("\n🎉🎉🎉 ORDER PLACED SUCCESSFULLY! 🎉🎉🎉\n");
    }
    
    // ========== FAIL: LOGIN FAIL TEST (Invalid Credentials) ==========
    private void executeLoginFailTest(String testCase, String username, String password) {
        System.out.println("📍 EXECUTING: LOGIN FAIL TEST (Expected: FAIL)");
        System.out.println("⚠️ This test will INTENTIONALLY FAIL to demonstrate failure handling\n");
        
        driver.get(baseUrl);
        waitForPageLoad();
        System.out.println("✅ Opened URL: " + baseUrl);
        
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
            System.out.println("✅ Entered password: " + password);
        } else {
            System.out.println("⚠️ Password is empty");
        }
        
        clickElement(driver.findElement(By.id("login-button")));
        System.out.println("✅ Clicked Login button");
        
        sleep(1000);
        
        // This assertion will FAIL because login will fail with invalid credentials
        // We're asserting that URL contains "inventory" but it won't!
        String currentUrl = driver.getCurrentUrl();
        System.out.println("❌ Current URL: " + currentUrl);
        System.out.println("❌ Expected URL to contain 'inventory' but login failed!");
        
        // This WILL FAIL - causing the test to fail
        Assert.assertTrue(currentUrl.contains("inventory"), 
            "LOGIN FAILED! Expected to reach inventory page but got error. User: " + username);
    }
    
    // ========== FAIL: INVALID URL TEST ==========
    private void executeInvalidUrlFailTest(String testCase) {
        System.out.println("📍 EXECUTING: INVALID URL TEST (Expected: FAIL)");
        System.out.println("⚠️ This test will INTENTIONALLY FAIL - trying to load invalid URL\n");
        
        try {
            driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(10));
            driver.get(invalidUrl);
            System.out.println("⏳ Trying to load: " + invalidUrl);
            
            // Wait a bit
            sleep(2000);
            
            // Try to find an element that won't exist
            // This assertion WILL FAIL because the page won't load
            WebElement body = driver.findElement(By.tagName("body"));
            String pageSource = body.getText();
            
            System.out.println("❌ Page should not have loaded!");
            
            // This WILL FAIL - the page won't have "Welcome" text
            Assert.assertTrue(pageSource.contains("Welcome to valid website"), 
                "INVALID URL TEST FAILED! Page did not load properly as expected.");
            
        } catch (Exception e) {
            System.out.println("❌ Error occurred: " + e.getMessage().split("\n")[0]);
            // Re-throw to make test fail
            Assert.fail("INVALID URL TEST FAILED! Could not load: " + invalidUrl + ". Error: " + e.getMessage().split("\n")[0]);
        }
    }
    
    // ========== FAIL: INVALID DETAILS TEST ==========
    private void executeInvalidDetailsFailTest(String testCase, String username, String password) {
        System.out.println("📍 EXECUTING: INVALID DETAILS TEST (Expected: FAIL)");
        System.out.println("⚠️ This test will INTENTIONALLY FAIL - trying checkout with empty details\n");
        
        driver.get(baseUrl);
        waitForPageLoad();
        
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("user-name"))).sendKeys(username);
        driver.findElement(By.id("password")).sendKeys(password);
        clickElement(driver.findElement(By.id("login-button")));
        waitForPageLoad();
        System.out.println("✅ Login successful");
        
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("inventory_list")));
        clickElement(wait.until(ExpectedConditions.elementToBeClickable(
            By.id("add-to-cart-sauce-labs-backpack"))));
        System.out.println("✅ Added product to cart");
        sleep(1000);
        
        clickElement(wait.until(ExpectedConditions.elementToBeClickable(By.className("shopping_cart_link"))));
        waitForPageLoad();
        System.out.println("✅ Cart page displayed");
        
        clickElement(wait.until(ExpectedConditions.elementToBeClickable(By.id("checkout"))));
        waitForPageLoad();
        System.out.println("✅ Checkout page displayed");
        
        // DON'T fill any details - just click continue
        System.out.println("\n📍 ATTEMPTING CHECKOUT WITH EMPTY DETAILS");
        clickElement(wait.until(ExpectedConditions.elementToBeClickable(By.id("continue"))));
        
        sleep(1000);
        
        // This assertion WILL FAIL because checkout won't succeed with empty details
        String currentUrl = driver.getCurrentUrl();
        System.out.println("❌ Current URL: " + currentUrl);
        System.out.println("❌ Expected to reach 'checkout-step-two' but validation failed!");
        
        // This WILL FAIL - we won't reach step two without filling details
        Assert.assertTrue(currentUrl.contains("checkout-step-two"), 
            "CHECKOUT FAILED! Expected to reach checkout step 2 but validation prevented it. Empty details not allowed!");
    }
    
    // ========== TAKE SCREENSHOT ==========
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
        
        // Take screenshot on FAILURE
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