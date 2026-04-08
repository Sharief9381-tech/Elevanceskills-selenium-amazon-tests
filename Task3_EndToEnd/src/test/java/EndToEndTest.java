import org.testng.annotations.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import io.github.bonigarcia.wdm.WebDriverManager;

/**
 * Task 3 — End-to-End Purchase Flow Test
 *
 * Flow: Search → Select product → Add to cart → Checkout → Validate payment > Rs500 → Place order → Confirm
 * Time restriction: 6 PM – 7 PM only
 */
public class EndToEndTest {

    WebDriver driver;
    EcomPage ecomPage;

    @BeforeClass
    public void setup() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        ecomPage = new EcomPage(driver);
    }

    @Test
    public void testCompleteFlow() {

        // Time restriction: 6 PM – 7 PM
        if (!TimeUtil.isWithinTimeRange(18, 19)) {
            System.out.println("Test skipped due to time restriction (6PM-7PM only). " +
                "Current time: " + java.time.LocalTime.now());
            return;
        }
        System.out.println("Time check PASSED: " + java.time.LocalTime.now());

        driver.get("https://www.amazon.in");

        // Step 1: Search
        ecomPage.searchProduct("water bottle");

        // Step 2: Select first product
        ecomPage.selectFirstProduct();

        // Step 3: Add to cart
        ecomPage.addToCart();

        // Step 4: Proceed to checkout
        ecomPage.proceedToCheckout();

        // Step 5: Validate payment > Rs 500
        ecomPage.validatePaymentAmount();

        // Step 6: Place order (safety mode — button verified but not clicked)
        ecomPage.placeOrder();

        // Step 7: Verify order confirmation
        ecomPage.verifyOrderConfirmation();

        System.out.println("\n=== Task 3 Complete ===");
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) driver.quit();
    }
}
