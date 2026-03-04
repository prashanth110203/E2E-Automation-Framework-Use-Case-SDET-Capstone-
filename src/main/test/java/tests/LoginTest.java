package tests;

import org.testng.Assert;
import org.testng.annotations.Test;

import pages.ProductsPage;

public class LoginTest extends BaseTest {

    @Test(priority = 1, description = "Verify successful login with valid credentials")
    public void testValidLogin() {
        logger.info("Test: Valid Login Started");

        ProductsPage productsPage = loginPage.login("standard_user", "secret_sauce");

        Assert.assertTrue(productsPage.isProductsPageDisplayed(),
                "Products page should be displayed after successful login");

        logger.info("Test: Valid Login Completed Successfully");
    }

    @Test(priority = 2, description = "Verify login fails with invalid credentials")
    public void testInvalidLogin() {
        logger.info("Test: Invalid Login Started");

        loginPage.login("invalid_user", "wrong_password");

        Assert.assertTrue(loginPage.isErrorDisplayed(),
                "Error message should be displayed for invalid credentials");

        logger.info("Test: Invalid Login Completed Successfully");
    }
}