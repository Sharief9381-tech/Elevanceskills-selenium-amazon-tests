import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.*;
import org.testng.Assert;
import org.testng.annotations.*;

import java.time.Duration;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Task 1 — Select a product from search results and verify product page details.
 *
 * Rules:
 *   1. Product must NOT be an electronics item.
 *   2. Product name must NOT start with A, B, C, or D.
 *   3. Test runs ONLY between 3 PM and 6 PM.
 *
 * Verifications on product page:
 *   - Title is displayed
 *   - Price is displayed
 *   - Availability is shown
 */
public class ProductSelectionTest {

    WebDriver driver;
    WebDriverWait wait;

    // Letters to skip
    private static final Set<String> SKIP_LETTERS = new HashSet<>(
        Arrays.asList("A", "B", "C", "D")
    );

    // Electronics keywords — skip any product whose title contains these
    private static final List<String> ELECTRONICS = Arrays.asList(
        "mobile", "phone", "laptop", "computer", "camera", "television", "tv",
        "headphone", "earphone", "speaker", "tablet", "smartwatch", "charger",
        "cable", "router", "keyboard", "mouse", "monitor", "printer",
        "hard disk", "pendrive", "usb", "bluetooth", "wifi", "processor",
        "graphics card", "motherboard", "ram", "ssd", "power bank"
    );

    @BeforeClass
    public void setup() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.manage().window().maximize();
    }

    @Test
    public void testSelectProductAndVerify() {

        // ── Step 1: Time check — 3 PM to 6 PM only ────────────────────────────
        LocalTime now = LocalTime.now();
        boolean forceRun = "true".equalsIgnoreCase(System.getProperty("FORCE_RUN"));
        if (!forceRun && (now.isBefore(LocalTime.of(15, 0)) || now.isAfter(LocalTime.of(18, 0)))) {
            System.out.println("TEST SKIPPED: Not within 3PM–6PM. Current time: " + now);
            return;
        }
        System.out.println("Time check PASSED: " + now + (forceRun ? " (FORCE_RUN)" : ""));

        // ── Step 2: Open Amazon and search ────────────────────────────────────
        driver.get("https://www.amazon.in/");

        WebElement searchBox = wait.until(
            ExpectedConditions.visibilityOfElementLocated(By.id("twotabsearchtextbox")));
        searchBox.sendKeys("borosil glass bottle");
        searchBox.submit();
        System.out.println("Search submitted.");

        // ── Step 3: Wait for results ───────────────────────────────────────────
        wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.cssSelector("div.s-main-slot")));

        // Scroll to load all cards
        ((JavascriptExecutor) driver).executeScript(
            "window.scrollTo(0, document.body.scrollHeight / 2)");
        try { Thread.sleep(1500); } catch (Exception ignored) {}
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, 0)");
        try { Thread.sleep(500); } catch (Exception ignored) {}

        // ── Step 4: Find a valid product ──────────────────────────────────────
        List<WebElement> results = driver.findElements(
            By.cssSelector("div[data-component-type='s-search-result']"));

        System.out.println("Total result cards: " + results.size());

        WebElement selectedCard = null;
        String selectedTitle = "";

        for (WebElement card : results) {
            String title = "";
            try {
                title = card.findElement(By.cssSelector("h2 span")).getText().trim();
            } catch (Exception e) {
                continue;
            }
            if (title.isEmpty()) continue;

            System.out.println("Card: " + title);

            // Rule 1: Skip if starts with A, B, C, or D
            String firstLetter = String.valueOf(title.charAt(0)).toUpperCase();
            if (SKIP_LETTERS.contains(firstLetter)) {
                System.out.println("  --> [SKIP] Starts with '" + firstLetter + "' (A/B/C/D rule)");
                continue;
            }

            // Rule 2: Skip if electronics
            String titleLower = title.toLowerCase();
            boolean isElectronic = ELECTRONICS.stream().anyMatch(titleLower::contains);
            if (isElectronic) {
                System.out.println("  --> [SKIP] Electronics product");
                continue;
            }

            // Valid product found
            System.out.println("  --> [VALID] Selected: " + title);
            selectedCard = card;
            selectedTitle = title;
            break;
        }

        Assert.assertNotNull(selectedCard,
            "No valid product found (all start with A/B/C/D or are electronics).");

        // ── Step 5: Click the product ──────────────────────────────────────────
        WebElement link = null;
        String[] linkSelectors = {
            "a.a-link-normal.s-no-outline",
            "a[href*='/dp/']",
            "h2 a",
            "a.a-link-normal"
        };
        for (String sel : linkSelectors) {
            try {
                link = selectedCard.findElement(By.cssSelector(sel));
                if (link.isDisplayed()) break;
            } catch (Exception ignored) {}
        }
        Assert.assertNotNull(link, "Could not find clickable link for: " + selectedTitle);
        link.click();
        System.out.println("Clicked product. Loading product page...");

        // Handle new tab if opened
        if (driver.getWindowHandles().size() > 1) {
            String newTab = driver.getWindowHandles().stream()
                .filter(h -> !h.equals(driver.getWindowHandle()))
                .findFirst().orElse(null);
            if (newTab != null) driver.switchTo().window(newTab);
        }

        // ── Step 6: Verify product page ────────────────────────────────────────

        // 6a. Title is displayed
        WebElement titleElem = wait.until(
            ExpectedConditions.visibilityOfElementLocated(By.id("productTitle")));
        String pageTitle = titleElem.getText().trim();
        Assert.assertFalse(pageTitle.isEmpty(), "Product title is empty on product page.");
        System.out.println("PASS: Title = " + pageTitle);

        // 6b. Title must not start with A, B, C, D
        String pageTitleFirst = String.valueOf(pageTitle.charAt(0)).toUpperCase();
        Assert.assertFalse(SKIP_LETTERS.contains(pageTitleFirst),
            "Product title starts with forbidden letter '" + pageTitleFirst + "': " + pageTitle);
        System.out.println("PASS: Title does not start with A/B/C/D.");

        // 6c. Price is displayed
        String price = "";
        String[] priceSelectors = {
            ".a-price .a-offscreen",
            "#priceblock_ourprice",
            "#priceblock_dealprice",
            "#apex_offerDisplay_desktop .a-price .a-offscreen",
            "#corePrice_feature_div .a-offscreen",
            ".priceToPay .a-offscreen"
        };
        for (String sel : priceSelectors) {
            try {
                WebElement pe = driver.findElement(By.cssSelector(sel));
                String raw = pe.getAttribute("innerHTML");
                if (raw == null || raw.isBlank()) raw = pe.getText();
                if (raw != null && !raw.isBlank()) { price = raw.trim(); break; }
            } catch (Exception ignored) {}
        }
        Assert.assertFalse(price.isEmpty(), "Price is not displayed on product page.");
        System.out.println("PASS: Price = " + price);

        // 6d. Availability is shown
        String availability = "";
        try {
            availability = driver.findElement(By.id("availability")).getText().trim();
        } catch (Exception e) {
            try {
                availability = driver.findElement(
                    By.cssSelector("#availability span")).getText().trim();
            } catch (Exception ignored) {}
        }
        Assert.assertFalse(availability.isEmpty(), "Availability is not shown on product page.");
        System.out.println("PASS: Availability = " + availability);

        System.out.println("\n=== Task 1 PASSED ===");
        System.out.println("Product : " + pageTitle);
        System.out.println("Price   : " + price);
        System.out.println("Stock   : " + availability);
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) driver.quit();
    }
}
