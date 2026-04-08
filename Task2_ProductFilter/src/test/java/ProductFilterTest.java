import org.testng.annotations.*;
import org.openqa.selenium.WebDriver;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.chrome.ChromeDriver;

/**
 * Task 2 — Search filters test (POM structure).
 *
 * Requirements:
 *   - Brand name starts with letter C
 *   - Price > Rs 2000
 *   - Customer rating > 4 stars
 *   - Runs ONLY between 3 PM and 6 PM
 */
public class ProductFilterTest {

    WebDriver driver;
    SearchPage searchPage;

    @BeforeClass
    public void setup() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        searchPage = new SearchPage(driver);
    }

    @Test
    public void testFilters() {

        // Time restriction: 3 PM – 6 PM
        if (!TimeUtil.isWithinTimeRange(15, 18)) {
            System.out.println("Test skipped due to time restriction (3PM-6PM only). " +
                "Current time: " + java.time.LocalTime.now());
            return;
        }
        System.out.println("Time check PASSED.");

        driver.get("https://www.amazon.in");

        // Search for Crocs shoes — brand starts with C, priced above 2000
        searchPage.searchProduct("Crocs shoes");

        // Apply filters
        searchPage.applyFilters();

        // Validate results
        searchPage.validateProducts();
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) driver.quit();
    }
}
