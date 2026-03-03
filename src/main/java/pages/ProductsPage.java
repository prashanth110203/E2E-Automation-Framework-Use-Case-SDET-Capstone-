package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import base.BasePage;

import java.util.List;

public class ProductsPage extends BasePage {

    @FindBy(className = "title")
    private WebElement pageTitle;

    @FindBy(className = "inventory_item")
    private List<WebElement> products;

    @FindBy(className = "shopping_cart_link")
    private WebElement cartIcon;

    @FindBy(className = "shopping_cart_badge")
    private WebElement cartBadge;

    @FindBy(className = "product_sort_container")
    private WebElement sortDropdown;

    @FindBy(id = "react-burger-menu-btn")
    private WebElement menuButton;

    @FindBy(id = "logout_sidebar_link")
    private WebElement logoutLink;

    public ProductsPage(WebDriver driver) {
        super(driver);
    }

    public boolean isProductsPageDisplayed() {
        return isDisplayed(pageTitle) && getText(pageTitle).equals("Products");
    }

    public ProductsPage addProductToCartByName(String productName) {
        logger.info("Adding product to cart: " + productName);
        
        for (WebElement product : products) {
            String name = product.findElement(By.className("inventory_item_name")).getText();
            
            if (name.equalsIgnoreCase(productName)) {
                WebElement addToCartButton = product.findElement(
                    By.cssSelector("button[id^='add-to-cart']")
                );
                click(addToCartButton);
                logger.info("Product added to cart: " + productName);
                return this;
            }
        }
        
        logger.warn("Product not found: " + productName);
        throw new RuntimeException("Product not found: " + productName);
    }

    public ProductsPage addProductToCartByIndex(int index) {
        logger.info("Adding product to cart at index: " + index);
        
        if (index >= 0 && index < products.size()) {
            WebElement product = products.get(index);
            WebElement addToCartButton = product.findElement(
                By.cssSelector("button[id^='add-to-cart']")
            );
            click(addToCartButton);
            return this;
        }
        
        throw new IndexOutOfBoundsException("Invalid product index: " + index);
    }

    public int getCartItemCount() {
        if (isDisplayed(cartBadge)) {
            return Integer.parseInt(getText(cartBadge));
        }
        return 0;
    }

    public CartPage clickCart() {
        logger.info("Clicking cart icon");
        click(cartIcon);
        return new CartPage(driver);
    }

    public int getProductCount() {
        return products.size();
    }

    public String getProductName(int index) {
        return products.get(index)
                .findElement(By.className("inventory_item_name"))
                .getText();
    }

    public void logout() {
        logger.info("Performing logout");
        click(menuButton);
        sleep(500);
        click(logoutLink);
    }
}