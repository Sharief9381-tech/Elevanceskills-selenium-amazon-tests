import org.testng.annotations.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import io.github.bonigarcia.wdm.WebDriverManager;

/**
 * Task 5 — Add multiple products to cart, verify total > Rs.2000, validate username.
 * Time restriction: 6 PM – 7 PM
 */
public class CartTest {

    WebDriver driver;
    CartPage cartPage;

    @BeforeClass
    public void setup() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        options.setExperimentalOption("useAutomationExtension", false);
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        cartPage = new CartPage(driver);
    }

    @Test
    public void testCartAndValidation() {

        // Time restriction: 6 PM – 7 PM
        if (!TimeUtil.isWithinTimeRange(18, 19)) {
            System.out.println("Test skipped: outside 6PM-7PM. Current: "
                + java.time.LocalTime.now());
            return;
        }
        System.out.println("Time check PASSED: " + java.time.LocalTime.now());

        driver.get("https://www.amazon.in");

        // Step 1: Search and add 3 products to cart
        // Using "Crocs shoes" — priced above Rs.2000 each, so total will easily exceed 2000
        cartPage.searchAndAddProducts("Crocs Classic Clog", 3);

        // Step 2: Go to cart
        cartPage.goToCart();

        // Step 3: Validate total price > 2000
        cartPage.validateTotalPrice();

        // Step 4: Validate username — exactly 10 chars, no special chars
        cartPage.validateUsername("Sharief001");  // 10 chars, alphanumeric ✅

        System.out.println("\n=== Task 5 Complete ===");
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) driver.quit();
    }
}
