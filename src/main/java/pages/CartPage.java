package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import base.BasePage;

import java.util.List;

public class CartPage extends BasePage {

    @FindBy(className = "title")
    private WebElement pageTitle;

    @FindBy(className = "cart_item")
    private List<WebElement> cartItems;

    @FindBy(id = "continue-shopping")
    private WebElement continueShoppingButton;

    @FindBy(id = "checkout")
    private WebElement checkoutButton;

    @FindBy(className = "cart_quantity")
    private List<WebElement> quantities;

    public CartPage(WebDriver driver) {
        super(driver);
    }

    public boolean isCartPageDisplayed() {
        return isDisplayed(pageTitle) && getText(pageTitle).equals("Your Cart");
    }

    public int getCartItemCount() {
        return cartItems.size();
    }

    public boolean isProductInCart(String productName) {
        for (WebElement item : cartItems) {
            String name = item.findElement(By.className("inventory_item_name")).getText();
            if (name.equalsIgnoreCase(productName)) {
                return true;
            }
        }
        return false;
    }

    public CartPage removeProductByName(String productName) {
        logger.info("Removing product from cart: " + productName);
        
        for (WebElement item : cartItems) {
            String name = item.findElement(By.className("inventory_item_name")).getText();
            
            if (name.equalsIgnoreCase(productName)) {
                WebElement removeButton = item.findElement(
                    By.cssSelector("button[id^='remove']")
                );
                click(removeButton);
                logger.info("Product removed from cart: " + productName);
                return this;
            }
        }
        
        logger.warn("Product not found in cart: " + productName);
        return this;
    }

    public ProductsPage continueShopping() {
        logger.info("Continuing shopping");
        click(continueShoppingButton);
        return new ProductsPage(driver);
    }

    public CheckoutPage proceedToCheckout() {
        logger.info("Proceeding to checkout");
        click(checkoutButton);
        return new CheckoutPage(driver);
    }
}