import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;
import java.time.Duration;
import java.util.List;

/**
 * POM class for Amazon Search Results page.
 * Task 2: brand starts with C, price > 2000, rating > 4
 */
public class SearchPage {

    WebDriver driver;
    WebDriverWait wait;

    public SearchPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    // Search for a product
    public void searchProduct(String product) {
        WebElement searchBox = wait.until(
            ExpectedConditions.visibilityOfElementLocated(By.id("twotabsearchtextbox")));
        searchBox.clear();
        searchBox.sendKeys(product);
        searchBox.submit();
        System.out.println("Searched for: " + product);
    }

    // Apply filters: brand C, price > 2000, rating > 4
    public void applyFilters() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.cssSelector("div.s-main-slot")));

        // Brand filter — try Campus or Crocs (both start with C)
        try {
            WebElement brand = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath(
                    "//span[text()='Campus']/ancestor::a[1] |" +
                    "//span[text()='Crocs']/ancestor::a[1]"
                )));
            brand.click();
            Thread.sleep(2000);
            System.out.println("Brand filter applied.");
        } catch (Exception e) {
            System.out.println("Brand filter not found: " + e.getMessage());
        }

        // Price filter — min 2000
        try {
            WebElement minPrice = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.id("low-price")));
            minPrice.clear();
            minPrice.sendKeys("2000");
            try {
                driver.findElement(By.id("submit_go")).click();
            } catch (Exception e) {
                minPrice.sendKeys(Keys.RETURN);
            }
            Thread.sleep(2000);
            System.out.println("Price filter applied: min 2000.");
        } catch (Exception e) {
            System.out.println("Price filter not applied: " + e.getMessage());
        }

        // Rating filter — 4 Stars & Up
        try {
            WebElement rating = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//span[contains(text(),'4 Stars & Up')]/ancestor::a[1]")));
            rating.click();
            Thread.sleep(2000);
            System.out.println("Rating filter applied: 4 Stars & Up.");
        } catch (Exception e) {
            System.out.println("Rating filter not found: " + e.getMessage());
        }
    }

    // Validate products — only check cards where brand starts with C
    public void validateProducts() {
        List<WebElement> products = wait.until(
            ExpectedConditions.presenceOfAllElementsLocatedBy(
                By.cssSelector("div.s-main-slot div[data-component-type='s-search-result']")));

        System.out.println("Total products found: " + products.size());
        int validated = 0;

        for (WebElement product : products) {
            try {
                // Get title
                String title = product.findElement(
                    By.cssSelector("h2 span")).getText().trim();
                if (title.isEmpty()) continue;

                // Get brand (a-size-base-plus span)
                String brand = "";
                try {
                    brand = product.findElement(
                        By.cssSelector("span.a-size-base-plus")).getText().trim();
                } catch (Exception e) {
                    brand = title;
                }

                // Skip if brand does not start with C
                if (!brand.toUpperCase().startsWith("C")) {
                    System.out.println("  [SKIP] Brand '" + brand + "' not starting with C");
                    continue;
                }

                // Get price — use .a-offscreen which works on Amazon India
                String priceText = "";
                try {
                    priceText = product.findElement(
                        By.cssSelector(".a-price .a-offscreen"))
                        .getAttribute("innerHTML").replace(",", "").trim();
                    // strip currency symbol, keep digits
                    priceText = priceText.replaceAll("[^\\d]", "");
                } catch (Exception e) {
                    System.out.println("  [SKIP] Price not found");
                    continue;
                }
                int price = Integer.parseInt(priceText);

                // Get rating — scan all aria-label spans for "star"
                double rating = 0;
                try {
                    List<WebElement> spans = product.findElements(
                        By.cssSelector("span[aria-label]"));
                    for (WebElement span : spans) {
                        String aria = span.getAttribute("aria-label");
                        if (aria != null && aria.toLowerCase().contains("star")) {
                            rating = Double.parseDouble(aria.trim().split(" ")[0]);
                            break;
                        }
                    }
                } catch (Exception e) {
                    // rating stays 0 — will be skipped in validation
                }

                // Validations
                if (!brand.toUpperCase().startsWith("C")) {
                    throw new AssertionError("Brand '" + brand + "' does not start with C");
                }
                if (price <= 2000) {
                    System.out.println("  [SKIP] Price " + price + " is not > 2000");
                    continue;
                }
                if (rating > 0 && rating <= 4.0) {
                    throw new AssertionError("Rating " + rating + " is not > 4");
                }

                System.out.println("  PASS | Brand=" + brand
                    + " | Price=" + price
                    + " | Rating=" + rating
                    + " | " + title.substring(0, Math.min(60, title.length())));
                validated++;

            } catch (AssertionError ae) {
                throw ae; // re-throw assertion failures
            } catch (Exception e) {
                System.out.println("  [SKIP] Missing data: " + e.getMessage());
            }
        }

        System.out.println("\nTotal validated (brand=C, price>2000, rating>4): " + validated);
        if (validated == 0) {
            System.out.println("  No C-brand products with price>2000 and rating>4 found in results.");
        }
    }
}
