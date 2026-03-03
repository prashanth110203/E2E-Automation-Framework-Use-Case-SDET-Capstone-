package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import base.BasePage;

public class LoginPage extends BasePage {

    @FindBy(id = "user-name")
    private WebElement usernameField;

    @FindBy(id = "password")
    private WebElement passwordField;

    @FindBy(id = "login-button")
    private WebElement loginButton;

    @FindBy(css = "[data-test='error']")
    private WebElement errorMessage;

    @FindBy(className = "login_logo")
    private WebElement loginLogo;

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public LoginPage enterUsername(String username) {
        logger.info("Entering username: " + username);
        type(usernameField, username);
        return this;
    }

    public LoginPage enterPassword(String password) {
        logger.info("Entering password");
        type(passwordField, password);
        return this;
    }

    public ProductsPage clickLoginButton() {
        logger.info("Clicking login button");
        click(loginButton);
        return new ProductsPage(driver);
    }

    public ProductsPage login(String username, String password) {
        logger.info("Performing login with username: " + username);
        return enterUsername(username)
                .enterPassword(password)
                .clickLoginButton();
    }

    public String getErrorMessage() {
        logger.info("Getting error message");
        return getText(errorMessage);
    }

    public boolean isErrorDisplayed() {
        return isDisplayed(errorMessage);
    }

    public boolean isLoginPageDisplayed() {
        return isDisplayed(loginLogo);
    }
}