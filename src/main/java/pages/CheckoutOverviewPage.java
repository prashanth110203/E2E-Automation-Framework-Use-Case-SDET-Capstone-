package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import base.BasePage;

import java.util.List;

public class CheckoutOverviewPage extends BasePage {

    @FindBy(className = "title")
    private WebElement pageTitle;

    @FindBy(className = "cart_item")
    private List<WebElement> cartItems;

    @FindBy(className = "summary_subtotal_label")
    private WebElement subtotal;

    @FindBy(className = "summary_tax_label")
    private WebElement tax;

    @FindBy(className = "summary_total_label")
    private WebElement total;

    @FindBy(id = "finish")
    private WebElement finishButton;

    @FindBy(id = "cancel")
    private WebElement cancelButton;

    @FindBy(className = "summary_info")
    private WebElement summaryInfo;

    public CheckoutOverviewPage(WebDriver driver) {
        super(driver);
    }

    public boolean isOverviewPageDisplayed() {
        return isDisplayed(pageTitle) && 
               getText(pageTitle).equals("Checkout: Overview");
    }

    public int getCartItemCount() {
        return cartItems.size();
    }

    public boolean isProductInOverview(String productName) {
        for (WebElement item : cartItems) {
            String name = item.findElement(By.className("inventory_item_name")).getText();
            if (name.equalsIgnoreCase(productName)) {
                return true;
            }
        }
        return false;
    }

    public String getSubtotal() {
        return getText(subtotal);
    }

    public String getTax() {
        return getText(tax);
    }

    public String getTotal() {
        return getText(total);
    }

    public double getTotalAmount() {
        String totalText = getTotal();
        return Double.parseDouble(totalText.replaceAll("[^0-9.]", ""));
    }

    public OrderConfirmationPage finishOrder() {
        logger.info("Finishing order");
        click(finishButton);
        return new OrderConfirmationPage(driver);
    }

    public ProductsPage cancel() {
        logger.info("Canceling order");
        click(cancelButton);
        return new ProductsPage(driver);
    }
}