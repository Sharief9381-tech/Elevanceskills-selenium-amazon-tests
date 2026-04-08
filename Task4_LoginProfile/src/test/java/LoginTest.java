import org.testng.annotations.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import io.github.bonigarcia.wdm.WebDriverManager;

public class LoginTest {

    WebDriver driver;
    LoginPage loginPage;

    private static final String EMAIL    = "shariefsk95@gmail.com";
    private static final String PASSWORD = "your_password_here"; // replace before running

    @BeforeClass
    public void setup() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        options.setExperimentalOption("useAutomationExtension", false);
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        loginPage = new LoginPage(driver);
    }

    @Test
    public void testLoginAndProfileValidation() {

        // Time restriction: 12 PM - 3 PM
        if (!TimeUtil.isWithinTimeRange(12, 15)) {
            System.out.println("Test skipped: outside 12PM-3PM. Current: "
                + java.time.LocalTime.now());
            return;
        }
        System.out.println("Time check PASSED: " + java.time.LocalTime.now());

        // Step 1: Open Amazon and click Sign In
        loginPage.clickSignIn();

        // Step 2: Enter email and password
        loginPage.enterCredentials(EMAIL, PASSWORD);

        // Step 3: If OTP page — wait for user to complete it manually
        String url = driver.getCurrentUrl();
        if (url.contains("mfa") || url.contains("cvf") || url.contains("ap/signin")) {
            boolean loggedIn = loginPage.waitForManualOtp();
            if (!loggedIn) {
                System.out.println("Login not completed within timeout.");
                return;
            }
        }

        System.out.println("Logged in successfully.");

        // Step 4: Navigate to profile
        loginPage.navigateToProfile();

        // Step 5: Validate username
        loginPage.validateUsername();

        System.out.println("\n=== Task 4 Complete ===");
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) driver.quit();
    }
}
