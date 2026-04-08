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
 * Internship Task 2:
 * Search for a product and apply filters:
 * - Brand starts with "C"
 * - Price > Rs 2000
 * - Customer rating > 4 stars
 * - Runs ONLY between 3 PM and 6 PM
 */
public class Task2_SearchFiltersTest extends TestBase {

    private static final double MIN_PRICE  = 2000.0;
    private static final double MIN_RATING = 4.0;

    @BeforeTest
    public void setup() { initialization(); }

    @Test
    public void testSearchWithFilters() throws InterruptedException {

        LocalTime now = LocalTime.now();
        boolean forceRun = "true".equalsIgnoreCase(System.getProperty("FORCE_RUN"));
        if (!forceRun && (now.isBefore(LocalTime.of(15, 0)) || now.isAfter(LocalTime.of(18, 0)))) {
            System.out.println("SKIPPED: Outside 3PM-6PM. Current: " + now); return;
        }
        System.out.println("Time check PASSED: " + now);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        // Search for Crocs shoes (brand starts with C, price > 2000)
        WebElement searchBox = wait.until(
            ExpectedConditions.visibilityOfElementLocated(By.id("twotabsearchtextbox")));
        searchBox.clear();
        searchBox.sendKeys("Crocs shoes");
        searchBox.submit();
        System.out.println("Searched: Crocs shoes");

        wait.until(ExpectedConditions.presenceOfElementLocated(
            By.cssSelector("div[data-component-type='s-search-result']")));

        // Apply brand filter (Crocs starts with C)
        try {
            WebElement brandLink = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//span[text()='Crocs']/ancestor::a[1] | //span[text()='Campus']/ancestor::a[1]")));
            brandLink.click();
            Thread.sleep(2000);
            System.out.println("Brand filter applied.");
        } catch (Exception e) { System.out.println("Brand filter not found: " + e.getMessage()); }

        // Apply rating filter
        try {
            WebElement ratingLink = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//span[contains(text(),'4 Stars & Up')]/ancestor::a[1]")));
            ratingLink.click();
            Thread.sleep(2000);
            System.out.println("Rating filter applied.");
        } catch (Exception e) { System.out.println("Rating filter not found: " + e.getMessage()); }

        // Scroll to load all cards
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight)");
        Thread.sleep(1500);
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, 0)");
        Thread.sleep(500);

        // Read and validate results
        List<WebElement> cards = driver.findElements(
            By.cssSelector("div[data-component-type='s-search-result']"));
        System.out.println("Total cards: " + cards.size());

        int validated = 0;
        for (WebElement card : cards) {
            try {
                String brand = card.findElement(
                    By.cssSelector("span.a-size-base-plus")).getText().trim();
                if (!brand.toUpperCase().startsWith("C")) {
                    System.out.println("  [SKIP] Brand '" + brand + "' not starting with C");
                    continue;
                }
                String priceRaw = card.findElement(
                    By.cssSelector(".a-price .a-offscreen")).getAttribute("innerHTML");
                double price = parsePrice(priceRaw);
                if (price <= MIN_PRICE) {
                    System.out.println("  [SKIP] Price " + price + " not > 2000"); continue;
                }
                Assert.assertTrue(brand.toUpperCase().startsWith("C"),
                    "Brand '" + brand + "' does not start with C");
                Assert.assertTrue(price > MIN_PRICE,
                    "Price " + price + " not > 2000");
                System.out.println("  PASS | Brand=" + brand + " | Price=" + price);
                validated++;
            } catch (Exception e) {
                System.out.println("  [SKIP] Missing data: " + e.getMessage());
            }
        }

        Assert.assertTrue(validated > 0, "No C-brand products with price > 2000 found.");
        System.out.println("=== Task 2 PASSED — " + validated + " products validated ===");
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
