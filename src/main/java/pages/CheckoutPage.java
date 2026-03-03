package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import base.BasePage;

public class CheckoutPage extends BasePage {

    @FindBy(id = "first-name")
    private WebElement firstNameField;

    @FindBy(id = "last-name")
    private WebElement lastNameField;

    @FindBy(id = "postal-code")
    private WebElement postalCodeField;

    @FindBy(id = "continue")
    private WebElement continueButton;

    @FindBy(id = "cancel")
    private WebElement cancelButton;

    @FindBy(className = "title")
    private WebElement pageTitle;

    @FindBy(css = "[data-test='error']")
    private WebElement errorMessage;

    public CheckoutPage(WebDriver driver) {
        super(driver);
    }

    public boolean isCheckoutPageDisplayed() {
        return isDisplayed(pageTitle) && 
               getText(pageTitle).equals("Checkout: Your Information");
    }

    public CheckoutPage enterFirstName(String firstName) {
        logger.info("Entering first name: " + firstName);
        type(firstNameField, firstName);
        return this;
    }

    public CheckoutPage enterLastName(String lastName) {
        logger.info("Entering last name: " + lastName);
        type(lastNameField, lastName);
        return this;
    }

    public CheckoutPage enterPostalCode(String postalCode) {
        logger.info("Entering postal code: " + postalCode);
        type(postalCodeField, postalCode);
        return this;
    }

    public CheckoutOverviewPage clickContinue() {
        logger.info("Clicking continue button");
        click(continueButton);
        return new CheckoutOverviewPage(driver);
    }

    public CheckoutOverviewPage fillCheckoutInformation(String firstName, String lastName, String postalCode) {
        logger.info("Filling checkout information");
        return enterFirstName(firstName)
                .enterLastName(lastName)
                .enterPostalCode(postalCode)
                .clickContinue();
    }

    public String getErrorMessage() {
        return getText(errorMessage);
    }

    public boolean isErrorDisplayed() {
        return isDisplayed(errorMessage);
    }

    public CartPage cancel() {
        logger.info("Canceling checkout");
        click(cancelButton);
        return new CartPage(driver);
    }
}