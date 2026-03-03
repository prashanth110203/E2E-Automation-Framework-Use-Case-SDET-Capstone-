package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import base.BasePage;

public class OrderConfirmationPage extends BasePage {

    @FindBy(className = "title")
    private WebElement pageTitle;

    @FindBy(className = "complete-header")
    private WebElement confirmationHeader;

    @FindBy(className = "complete-text")
    private WebElement confirmationText;

    @FindBy(id = "back-to-products")
    private WebElement backHomeButton;

    @FindBy(className = "pony_express")
    private WebElement confirmationImage;

    public OrderConfirmationPage(WebDriver driver) {
        super(driver);
    }

    public boolean isOrderConfirmationDisplayed() {
        return isDisplayed(pageTitle) && 
               getText(pageTitle).equals("Checkout: Complete!");
    }

    public String getConfirmationHeader() {
        return getText(confirmationHeader);
    }

    public String getConfirmationText() {
        return getText(confirmationText);
    }

    public boolean isConfirmationImageDisplayed() {
        return isDisplayed(confirmationImage);
    }

    public boolean verifyOrderSuccess() {
        logger.info("Verifying order success");
        return isOrderConfirmationDisplayed() &&
               getConfirmationHeader().equals("Thank you for your order!") &&
               isConfirmationImageDisplayed();
    }

    public ProductsPage backToHome() {
        logger.info("Navigating back to home");
        click(backHomeButton);
        return new ProductsPage(driver);
    }
}