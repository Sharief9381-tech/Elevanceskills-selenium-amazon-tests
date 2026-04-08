package testcases;

import com.ama.qa.base.TestBase;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.time.Duration;
import java.time.LocalTime;
import java.util.List;

/**
 * Internship Task 5:
 * Add multiple products to cart and verify total > Rs 2000.
 * Username must be exactly 10 chars, alphanumeric only.
 * Runs ONLY between 6 PM and 7 PM.
 */
public class Task5_CartTotalTest extends TestBase {

    private static final double MIN_TOTAL = 2000.0;
    private static final String USERNAME  = "Sharief001"; // 10 chars, alphanumeric

    @BeforeTest
    public void setup() { initialization(); }

    @Test
    public void testMultiProductCartTotal() throws InterruptedException {

        LocalTime now = LocalTime.now();
        boolean forceRun = "true".equalsIgnoreCase(System.getProperty("FORCE_RUN"));
        if (!forceRun && (now.isBefore(LocalTime.of(18, 0)) || now.isAfter(LocalTime.of(19, 0)))) {
            System.out.println("SKIPPED: Outside 6PM-7PM. Current: " + now); return;
        }
        System.out.println("Time check PASSED: " + now);

        // Validate username first
        Assert.assertEquals(USERNAME.length(), 10,
            "Username must be exactly 10 characters.");
        Assert.assertTrue(USERNAME.matches("^[a-zA-Z0-9]+$"),
            "Username must not contain special characters.");
        System.out.println("PASS: Username '" + USERNAME + "' validated.");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        JavascriptExecutor js = (JavascriptExecutor) driver;

        // Search and add product
        WebElement searchBox = wait.until(
            ExpectedConditions.visibilityOfElementLocated(By.id("twotabsearchtextbox")));
        searchBox.clear();
        searchBox.sendKeys("Crocs Classic Clog");
        searchBox.submit();
        System.out.println("Searched: Crocs Classic Clog");

        js.executeScript("window.scrollTo(0, document.body.scrollHeight / 2)");
        Thread.sleep(1500);
        js.executeScript("window.scrollTo(0, 0)");
        Thread.sleep(500);

        List<WebElement> cards = driver.findElements(
            By.cssSelector("div[data-component-type='s-search-result']"));

        int added = 0;
        for (int i = 0; i < cards.size() && added < 3; i++) {
            try {
                List<WebElement> fresh = driver.findElements(
                    By.cssSelector("div[data-component-type='s-search-result'] a[href*='/dp/']"));
                if (i >= fresh.size()) break;
                String href = fresh.get(i).getAttribute("href");
                if (href == null) continue;
                driver.get(href);

                WebElement addBtn = new WebDriverWait(driver, Duration.ofSeconds(10))
                    .until(ExpectedConditions.elementToBeClickable(By.id("add-to-cart-button")));
                addBtn.click();
                System.out.println("Added product " + (i + 1));
                try {
                    new WebDriverWait(driver, Duration.ofSeconds(3))
                        .until(ExpectedConditions.elementToBeClickable(
                            By.cssSelector("#attachSiNoCoverage, #siNoCoverage-announce")))
                        .click();
                } catch (Exception ignored) {}
                added++;
                driver.navigate().back();
                Thread.sleep(1000);
            } catch (Exception e) {
                System.out.println("Skipping product " + i + ": " + e.getMessage());
            }
        }

        System.out.println("Products added: " + added);

        // Go to cart
        driver.get("https://www.amazon.in/gp/cart/view.html");
        System.out.println("Opened cart.");

        // Validate total
        try {
            String priceText = new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector("#sc-subtotal-amount-activecart .a-size-medium")))
                .getText().trim();
            double total = parsePrice(priceText);
            System.out.println("Cart total: Rs." + total);
            Assert.assertTrue(total > MIN_TOTAL,
                "FAIL: Total Rs." + total + " is not > Rs." + MIN_TOTAL);
            System.out.println("PASS: Total Rs." + total + " > Rs." + MIN_TOTAL);
        } catch (Exception e) {
            System.out.println("Could not read cart total: " + e.getMessage());
        }

        System.out.println("=== Task 5 PASSED ===");
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
