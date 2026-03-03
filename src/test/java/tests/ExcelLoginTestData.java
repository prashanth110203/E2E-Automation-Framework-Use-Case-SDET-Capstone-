package tests;

import java.util.Map;
import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.*;
import utils.ExcelReader;

public class ExcelLoginTestData {

    WebDriver driver;
    WebDriverWait wait;
    
    String excelPath = System.getProperty("user.dir") 
                       + "/src/test/resources/testdata/LoginTestData.xlsx";
    
    String baseUrl = "https://www.saucedemo.com/";

    @BeforeMethod
    public void setUp() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        
        driver.get(baseUrl);
        System.out.println("✅ Opened URL: " + baseUrl);
    }

    @DataProvider(name = "loginData")
    public Object[][] getData() {
        return ExcelReader.getExcelDataAsArray(excelPath, "Sheet1");
    }

    @Test(dataProvider = "loginData")
    public void loginTest(Map<String, String> testData) {
        
        String testCase = testData.get("TestCase");
        String username = testData.get("Username");
        String password = testData.get("Password");
        String expectedResult = testData.get("ExpectedResult");
        String description = testData.get("Description");
        
        System.out.println("==================================================");
        System.out.println("TEST CASE: " + testCase);
        System.out.println("Description: " + description);
        System.out.println("Username: " + username);
        System.out.println("Password: " + password);
        System.out.println("Expected Result: " + expectedResult);
        System.out.println("==================================================");
        
        WebElement usernameField = driver.findElement(By.id("user-name"));
        usernameField.clear();
        if (username != null && !username.isEmpty()) {
            usernameField.sendKeys(username);
        }
        
        WebElement passwordField = driver.findElement(By.id("password"));
        passwordField.clear();
        if (password != null && !password.isEmpty()) {
            passwordField.sendKeys(password);
        }
        
        WebElement loginButton = driver.findElement(By.id("login-button"));
        loginButton.click();
        
        verifyLoginResult(testCase, username, expectedResult);
    }
    
    private void verifyLoginResult(String testCase, String username, String expectedResult) {
        
        String currentUrl = driver.getCurrentUrl();
        
        switch (expectedResult.toLowerCase()) {
            
            case "success":
                if (currentUrl.contains("inventory")) {
                    System.out.println("✅ PASS - " + testCase);
                    System.out.println("✅ Login successful for: " + username);
                    
                    WebElement productsTitle = driver.findElement(By.className("title"));
                    Assert.assertEquals(productsTitle.getText(), "Products");
                    System.out.println("✅ Products page displayed correctly");
                    
                    logout();
                    
                } else {
                    System.out.println("❌ FAIL - " + testCase);
                    Assert.fail("Expected successful login but failed");
                }
                break;
                
            case "failed":
                if (!currentUrl.contains("inventory")) {
                    System.out.println("✅ PASS - " + testCase);
                    System.out.println("✅ Login failed as expected for: " + username);
                    
                    WebElement errorMessage = driver.findElement(By.cssSelector("[data-test='error']"));
                    System.out.println("✅ Error message: " + errorMessage.getText());
                    Assert.assertTrue(errorMessage.isDisplayed());
                    
                } else {
                    System.out.println("❌ FAIL - " + testCase);
                    Assert.fail("Expected login failure but succeeded");
                }
                break;
                
            case "locked":
                if (!currentUrl.contains("inventory")) {
                    WebElement errorMessage = driver.findElement(By.cssSelector("[data-test='error']"));
                    String errorText = errorMessage.getText();
                    
                    if (errorText.contains("locked out")) {
                        System.out.println("✅ PASS - " + testCase);
                        System.out.println("✅ User is locked out as expected");
                    } else {
                        Assert.fail("Expected locked out message");
                    }
                } else {
                    Assert.fail("Expected locked user but login succeeded");
                }
                break;
                
            default:
                System.out.println("⚠️ Unknown expected result: " + expectedResult);
        }
        
        System.out.println("--------------------------------------------------\n");
    }
    
    private void logout() {
        try {
            driver.findElement(By.id("react-burger-menu-btn")).click();
            wait.until(ExpectedConditions.elementToBeClickable(By.id("logout_sidebar_link"))).click();
            System.out.println("✅ Logged out successfully");
        } catch (Exception e) {
            System.out.println("⚠️ Logout failed: " + e.getMessage());
        }
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
            System.out.println("✅ Browser closed\n");
        }
    }
}