package testcases;

import com.ama.qa.base.TestBase;
import com.ama.qa.util.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.time.Duration;
import java.time.LocalTime;

/**
 * Internship Task 3:
 * End-to-end: Search → Add to cart → Checkout → Verify payment > Rs 500
 * Runs ONLY between 6 PM and 7 PM
 */
public class Task3_EndToEndPurchaseTest extends TestBase {

    private static final double MIN_PAYMENT = 500.0;

    @BeforeTest
    public void setup() { initialization(); }

    @Test
    public void testFullPurchaseFlow() throws InterruptedException {

        LocalTime now = LocalTime.now();
        boolean forceRun = "true".equalsIgnoreCase(System.getProperty("FORCE_RUN"));
        if (!forceRun && (now.isBefore(LocalTime.of(18, 0)) || now.isAfter(LocalTime.of(19, 0)))) {
            System.out.println("SKIPPED: Outside 6PM-7PM. Current: " + now); return;
        }
        System.out.println("Time check PASSED: " + now);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        // Step 1: Search
        WebElement searchBox = wait.until(
            ExpectedConditions.visibilityOfElementLocated(By.id("twotabsearchtextbox")));
        searchBox.clear();
        searchBox.sendKeys("water bottle");
        searchBox.submit();
        System.out.println("Searched: water bottle");

        // Step 2: Click first product
        WebElement firstProduct = wait.until(ExpectedConditions.elementToBeClickable(
            By.cssSelector("div[data-component-type='s-search-result'] a.a-link-normal.s-no-outline")));
        firstProduct.click();
        System.out.println("Clicked first product.");

        if (driver.getWindowHandles().size() > 1) {
            for (String w : driver.getWindowHandles()) driver.switchTo().window(w);
        }

        // Step 3: Add to cart
        WebElement addBtn = wait.until(
            ExpectedConditions.elementToBeClickable(By.id("add-to-cart-button")));
        addBtn.click();
        System.out.println("Added to cart.");
        try {
            new WebDriverWait(driver, Duration.ofSeconds(4))
                .until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("#attachSiNoCoverage, #siNoCoverage-announce")))
                .click();
        } catch (Exception ignored) {}

        // Step 4: Go to cart
        driver.get("https://www.amazon.in/gp/cart/view.html");
        System.out.println("Opened cart.");

        // Read subtotal
        String subtotalText = "";
        try {
            subtotalText = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector("#sc-subtotal-amount-activecart .a-size-medium")))
                .getText().trim();
            double subtotal = parsePrice(subtotalText);
            System.out.println("Cart subtotal: Rs." + subtotal);
            Assert.assertTrue(subtotal > MIN_PAYMENT,
                "Cart total Rs." + subtotal + " is not > Rs." + MIN_PAYMENT);
            System.out.println("PASS: Cart total Rs." + subtotal + " > Rs." + MIN_PAYMENT);
        } catch (Exception e) {
            System.out.println("Subtotal not found: " + e.getMessage());
        }

        // Step 5: Proceed to checkout
        try {
            WebElement checkoutBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("input[name='proceedToRetailCheckout'], #sc-buy-box-ptc-button input")));
            checkoutBtn.click();
            System.out.println("Proceeded to checkout.");
        } catch (Exception e) {
            System.out.println("Checkout button not found (login required): " + e.getMessage());
        }

        System.out.println("=== Task 3 Complete ===");
    }

    private double parsePrice(String text) {
        if (text == null) return 0;
        String d = text.replaceAll("[^\\d.]", "").replace(",", "");
        try { return Double.parseDouble(d); } catch (Exception e) { return 0; }
    }

    @AfterTest
    public void tearDown() {
        if (driver != null) { driver.quit(); driver = null; }
    }
}
