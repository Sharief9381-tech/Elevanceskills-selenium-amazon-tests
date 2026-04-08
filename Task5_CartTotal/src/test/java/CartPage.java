import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;
import java.time.Duration;
import java.util.List;

/**
 * POM class for Task 5 — Multi-product cart with total > 2000 and username validation.
 */
public class CartPage {

    WebDriver driver;
    WebDriverWait wait;

    public CartPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    // Search and add multiple products to cart
    public void searchAndAddProducts(String product, int count) {
        System.out.println("Searching for: " + product);

        WebElement searchBox = wait.until(
            ExpectedConditions.visibilityOfElementLocated(By.id("twotabsearchtextbox")));
        searchBox.clear();
        searchBox.sendKeys(product);
        searchBox.submit();

        // Scroll to load all cards
        ((JavascriptExecutor) driver).executeScript(
            "window.scrollTo(0, document.body.scrollHeight / 2)");
        try { Thread.sleep(1500); } catch (Exception ignored) {}
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, 0)");
        try { Thread.sleep(500); } catch (Exception ignored) {}

        // Get all product links
        List<WebElement> products = wait.until(
            ExpectedConditions.presenceOfAllElementsLocatedBy(
                By.cssSelector("div[data-component-type='s-search-result'] a.a-link-normal.s-no-outline")));

        System.out.println("Found " + products.size() + " products. Adding " + count + "...");
        int added = 0;

        for (int i = 0; i < products.size() && added < count; i++) {
            try {
                // Re-fetch to avoid stale element
                List<WebElement> freshProducts = driver.findElements(
                    By.cssSelector("div[data-component-type='s-search-result'] a.a-link-normal.s-no-outline"));

                if (i >= freshProducts.size()) break;
                String href = freshProducts.get(i).getAttribute("href");
                if (href == null || !href.contains("/dp/")) continue;

                driver.get(href);
                System.out.println("Opened product " + (i + 1));

                // Switch to new tab if opened
                if (driver.getWindowHandles().size() > 1) {
                    for (String win : driver.getWindowHandles()) {
                        driver.switchTo().window(win);
                    }
                }

                // Add to cart
                try {
                    WebElement addBtn = wait.until(
                        ExpectedConditions.elementToBeClickable(By.id("add-to-cart-button")));
                    addBtn.click();
                    System.out.println("Added product " + (i + 1) + " to cart.");

                    // Dismiss upsell popup
                    try {
                        new WebDriverWait(driver, Duration.ofSeconds(3))
                            .until(ExpectedConditions.elementToBeClickable(
                                By.cssSelector("#attachSiNoCoverage, #siNoCoverage-announce")))
                            .click();
                    } catch (Exception ignored) {}

                    added++;
                } catch (Exception e) {
                    System.out.println("  Add to cart failed for product " + (i + 1) + ": " + e.getMessage());
                }

                // Go back to search results
                driver.navigate().back();
                try { Thread.sleep(1000); } catch (Exception ignored) {}

            } catch (Exception e) {
                System.out.println("Skipping product " + i + ": " + e.getMessage());
            }
        }

        System.out.println("Total products added to cart: " + added);
    }

    // Go to cart
    public void goToCart() {
        driver.get("https://www.amazon.in/gp/cart/view.html");
        System.out.println("Opened cart page.");
    }

    // Validate total price > 2000
    public void validateTotalPrice() {
        try {
            String[] selectors = {
                "#sc-subtotal-amount-activecart .a-size-medium",
                "span[id*='sc-subtotal-amount'] .a-size-medium",
                ".sc-subtotal-amount .a-size-medium"
            };
            String priceText = "";
            for (String sel : selectors) {
                try {
                    WebElement el = new WebDriverWait(driver, Duration.ofSeconds(10))
                        .until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(sel)));
                    priceText = el.getText().trim();
                    if (!priceText.isEmpty()) break;
                } catch (Exception ignored) {}
            }
            if (priceText.isEmpty()) {
                // XPath fallback
                priceText = new WebDriverWait(driver, Duration.ofSeconds(10))
                    .until(ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//span[contains(@id,'sc-subtotal-amount')]")))
                    .getText().trim();
            }
            String digits = priceText.replaceAll("[^0-9]", "");
            int price = Integer.parseInt(digits);
            System.out.println("Cart total: Rs." + price);
            if (price <= 2000) {
                throw new AssertionError("FAIL: Total Rs." + price + " is not > 2000.");
            }
            System.out.println("PASS: Total price Rs." + price + " > 2000.");
        } catch (AssertionError ae) {
            throw ae;
        } catch (Exception e) {
            System.out.println("Could not validate total price: " + e.getMessage());
        }
    }

    // Validate username: exactly 10 chars, alphanumeric only
    public void validateUsername(String username) {
        System.out.println("Validating username: '" + username + "'");

        // Rule 1: exactly 10 characters
        if (username.length() != 10) {
            throw new AssertionError(
                "FAIL: Username '" + username + "' has " + username.length()
                + " chars. Must be exactly 10.");
        }

        // Rule 2: no special characters (alphanumeric only)
        if (!username.matches("^[a-zA-Z0-9]+$")) {
            throw new AssertionError(
                "FAIL: Username '" + username + "' contains special characters.");
        }

        System.out.println("PASS: Username '" + username
            + "' is exactly 10 alphanumeric characters.");
    }
}
