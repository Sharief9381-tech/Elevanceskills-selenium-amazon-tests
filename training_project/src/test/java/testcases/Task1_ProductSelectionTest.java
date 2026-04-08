package testcases;

import com.ama.qa.base.TestBase;
import com.ama.qa.pages.AmazonHomePage;
import com.ama.qa.pages.AmazonSearchProductsPage;
import com.ama.qa.pages.AmazonProductDetailsPage;
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
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Internship Task 1:
 * Select a product from search results and verify product page details.
 * - Must NOT be an electronics product
 * - Product name must NOT start with A, B, C, or D
 * - Runs ONLY between 3 PM and 6 PM
 */
public class Task1_ProductSelectionTest extends TestBase {

    private static final Set<String> SKIP_LETTERS = new HashSet<>(
        Arrays.asList("A", "B", "C", "D"));

    private static final List<String> ELECTRONICS = Arrays.asList(
        "mobile", "phone", "laptop", "computer", "camera", "television", "tv",
        "headphone", "earphone", "speaker", "tablet", "smartwatch", "charger",
        "cable", "router", "keyboard", "mouse", "monitor", "printer",
        "hard disk", "pendrive", "usb", "bluetooth", "wifi", "processor",
        "graphics card", "motherboard", "ram", "ssd", "power bank"
    );

    @BeforeTest
    public void setup() {
        initialization();
    }

    @Test
    public void testSelectProductAndVerify() throws InterruptedException {

        // Time check: 3 PM – 6 PM only
        LocalTime now = LocalTime.now();
        boolean forceRun = "true".equalsIgnoreCase(System.getProperty("FORCE_RUN"));
        if (!forceRun && (now.isBefore(LocalTime.of(15, 0)) || now.isAfter(LocalTime.of(18, 0)))) {
            System.out.println("SKIPPED: Outside 3PM-6PM. Current: " + now);
            return;
        }
        System.out.println("Time check PASSED: " + now);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        // Search
        WebElement searchBox = wait.until(
            ExpectedConditions.visibilityOfElementLocated(By.id("twotabsearchtextbox")));
        searchBox.clear();
        searchBox.sendKeys("borosil glass bottle");
        searchBox.submit();
        System.out.println("Searched: borosil glass bottle");

        // Scroll to load cards
        ((JavascriptExecutor) driver).executeScript(
            "window.scrollTo(0, document.body.scrollHeight / 2)");
        Thread.sleep(1500);
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, 0)");
        Thread.sleep(500);

        // Find valid product
        List<WebElement> cards = driver.findElements(
            By.cssSelector("div[data-component-type='s-search-result']"));
        System.out.println("Cards found: " + cards.size());

        WebElement selectedLink = null;
        String selectedTitle = "";

        for (WebElement card : cards) {
            String title = "";
            try { title = card.findElement(By.cssSelector("h2 span")).getText().trim(); }
            catch (Exception e) { continue; }
            if (title.isEmpty()) continue;

            System.out.println("Card: " + title);
            String first = String.valueOf(title.charAt(0)).toUpperCase();
            if (SKIP_LETTERS.contains(first)) {
                System.out.println("  [SKIP] Starts with " + first); continue;
            }
            String low = title.toLowerCase();
            if (ELECTRONICS.stream().anyMatch(low::contains)) {
                System.out.println("  [SKIP] Electronics"); continue;
            }

            // Find link
            String[] linkSelectors = {
                "a.a-link-normal.s-no-outline", "a[href*='/dp/']", "h2 a", "a.a-link-normal"
            };
            for (String sel : linkSelectors) {
                try {
                    WebElement l = card.findElement(By.cssSelector(sel));
                    if (l.isDisplayed()) { selectedLink = l; break; }
                } catch (Exception ignored) {}
            }
            if (selectedLink != null) { selectedTitle = title; break; }
        }

        Assert.assertNotNull(selectedLink, "No valid product found.");
        System.out.println("[SELECTED] " + selectedTitle);
        selectedLink.click();

        // Switch tab if needed
        if (driver.getWindowHandles().size() > 1) {
            for (String w : driver.getWindowHandles()) driver.switchTo().window(w);
        }

        // Verify product page
        WebElement titleElem = new WebDriverWait(driver, Duration.ofSeconds(20))
            .until(ExpectedConditions.visibilityOfElementLocated(By.id("productTitle")));
        String pageTitle = titleElem.getText().trim();
        Assert.assertFalse(pageTitle.isEmpty(), "Title empty.");
        System.out.println("PASS: Title = " + pageTitle);

        Assert.assertFalse(SKIP_LETTERS.contains(
            String.valueOf(pageTitle.charAt(0)).toUpperCase()), "Title starts with A/B/C/D.");
        System.out.println("PASS: Title does not start with A/B/C/D.");

        // Price
        String price = "";
        for (String sel : new String[]{".a-price .a-offscreen", "#priceblock_ourprice",
                "#corePrice_feature_div .a-offscreen", ".priceToPay .a-offscreen"}) {
            try {
                WebElement pe = driver.findElement(By.cssSelector(sel));
                String raw = pe.getAttribute("innerHTML");
                if (raw == null || raw.isBlank()) raw = pe.getText();
                if (raw != null && !raw.isBlank()) { price = raw.trim(); break; }
            } catch (Exception ignored) {}
        }
        Assert.assertFalse(price.isEmpty(), "Price not displayed.");
        System.out.println("PASS: Price = " + price);

        // Availability
        String avail = "";
        try { avail = driver.findElement(By.id("availability")).getText().trim(); }
        catch (Exception e) {
            try { avail = driver.findElement(
                By.cssSelector("#availability span")).getText().trim(); }
            catch (Exception ignored) {}
        }
        Assert.assertFalse(avail.isEmpty(), "Availability not shown.");
        System.out.println("PASS: Availability = " + avail);
        System.out.println("=== Task 1 PASSED ===");
    }

    @AfterTest
    public void tearDown() {
        if (driver != null) { driver.quit(); driver = null; }
    }
}
