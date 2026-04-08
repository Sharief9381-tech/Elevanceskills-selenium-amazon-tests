import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Scrapes the current price and title of an Amazon product using Selenium.
 */
public class PriceScraper {

    private final String productUrl;

    public PriceScraper(String productUrl) {
        this.productUrl = productUrl;
    }

    public double scrapePrice() {
        WebDriver driver = createDriver();
        try {
            driver.get(productUrl);
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

            // Wait for page to load
            wait.until(ExpectedConditions.presenceOfElementLocated(By.id("productTitle")));

            // Try multiple price selectors
            String[] selectors = {
                ".a-price .a-offscreen",
                "#priceblock_ourprice",
                "#priceblock_dealprice",
                "#apex_offerDisplay_desktop .a-price .a-offscreen",
                "#corePrice_feature_div .a-offscreen",
                ".priceToPay .a-offscreen"
            };

            for (String sel : selectors) {
                try {
                    WebElement el = driver.findElement(By.cssSelector(sel));
                    String raw = el.getAttribute("innerHTML");
                    if (raw == null || raw.isBlank()) raw = el.getText();
                    double price = parsePrice(raw);
                    if (price > 0) {
                        System.out.println("Price found: Rs." + price + " (selector: " + sel + ")");
                        return price;
                    }
                } catch (Exception ignored) {}
            }

            System.out.println("Price element not found on page.");
            return -1;

        } catch (Exception e) {
            System.out.println("Error scraping price: " + e.getMessage());
            return -1;
        } finally {
            driver.quit();
        }
    }

    public String scrapeTitle() {
        WebDriver driver = createDriver();
        try {
            driver.get(productUrl);
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
            WebElement title = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("productTitle")));
            return title.getText().trim();
        } catch (Exception e) {
            return "Unknown Product";
        } finally {
            driver.quit();
        }
    }

    private WebDriver createDriver() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");   // run silently in background
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        return new ChromeDriver(options);
    }

    private double parsePrice(String text) {
        if (text == null) return 0;
        String digits = text.replaceAll("[^\\d.]", "").replace(",", "");
        try { return Double.parseDouble(digits); } catch (Exception e) { return 0; }
    }
}
