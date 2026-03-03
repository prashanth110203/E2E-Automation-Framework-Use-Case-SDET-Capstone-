package tests;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import base.DriverFactory;
import config.ConfigReader;
import pages.LoginPage;

public class BaseTest {
    protected static final Logger logger = LogManager.getLogger(BaseTest.class);
    protected WebDriver driver;
    protected LoginPage loginPage;

    @BeforeMethod
    public void setup() {
        logger.info("=== Test Setup Started ===");
        driver = DriverFactory.getDriver();
        driver.get(ConfigReader.getUrl());
        loginPage = new LoginPage(driver);
        logger.info("Navigated to: " + ConfigReader.getUrl());
        logger.info("=== Test Setup Completed ===");
    }

    @AfterMethod
    public void teardown() {
        logger.info("=== Test Teardown Started ===");
        DriverFactory.quitDriver();
        logger.info("=== Test Teardown Completed ===");
    }
}