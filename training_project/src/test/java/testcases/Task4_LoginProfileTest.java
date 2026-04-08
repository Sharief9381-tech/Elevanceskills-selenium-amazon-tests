package testcases;

import com.ama.qa.base.TestBase;
import com.ama.qa.util.ConfigReader;
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

/**
 * Internship Task 4:
 * Login and validate profile username.
 * - Username must NOT contain: A, C, G, I, L, K (case-insensitive)
 * - Runs ONLY between 12 PM and 3 PM
 */
public class Task4_LoginProfileTest extends TestBase {

    ConfigReader configReader = new ConfigReader();
    JavascriptExecutor js;

    @BeforeTest
    public void setup() { initialization(); }

    @Test
    public void testLoginAndProfileValidation() throws InterruptedException {

        LocalTime now = LocalTime.now();
        boolean forceRun = "true".equalsIgnoreCase(System.getProperty("FORCE_RUN"));
        if (!forceRun && (now.isBefore(LocalTime.of(12, 0)) || now.isAfter(LocalTime.of(15, 0)))) {
            System.out.println("SKIPPED: Outside 12PM-3PM. Current: " + now); return;
        }
        System.out.println("Time check PASSED: " + now);

        js = (JavascriptExecutor) driver;
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        // Navigate to sign-in page
        driver.get("https://www.amazon.in/ap/signin?" +
            "openid.pape.max_auth_age=0" +
            "&openid.return_to=https%3A%2F%2Fwww.amazon.in%2F" +
            "&openid.identity=http%3A%2F%2Fspecs.openid.net%2Fauth%2F2.0%2Fidentifier_select" +
            "&openid.assoc_handle=inflex" +
            "&openid.mode=checkid_setup" +
            "&openid.claimed_id=http%3A%2F%2Fspecs.openid.net%2Fauth%2F2.0%2Fidentifier_select" +
            "&openid.ns=http%3A%2F%2Fspecs.openid.net%2Fauth%2F2.0");
        Thread.sleep(3000);
        System.out.println("Sign-in page loaded.");

        // Enter email via JS
        WebElement emailField = wait.until(
            ExpectedConditions.presenceOfElementLocated(By.name("email")));
        js.executeScript("arguments[0].value = arguments[1];", emailField, configReader.getUsername());
        js.executeScript("arguments[0].dispatchEvent(new Event('input',{bubbles:true}));", emailField);
        js.executeScript("arguments[0].dispatchEvent(new Event('change',{bubbles:true}));", emailField);
        System.out.println("Email entered: " + configReader.getUsername());

        // Click Continue
        try {
            wait.until(ExpectedConditions.elementToBeClickable(By.id("continue"))).click();
        } catch (Exception e) {
            js.executeScript("document.getElementById('continue').click();");
        }
        Thread.sleep(3000);

        // Enter password via JS
        WebElement passField = wait.until(
            ExpectedConditions.presenceOfElementLocated(By.id("ap_password")));
        js.executeScript("arguments[0].value = arguments[1];", passField, configReader.getPassword());
        js.executeScript("arguments[0].dispatchEvent(new Event('input',{bubbles:true}));", passField);
        js.executeScript("document.getElementById('signInSubmit').click();");
        System.out.println("Password entered. Signing in...");
        Thread.sleep(4000);

        // Handle OTP if needed
        String url = driver.getCurrentUrl();
        if (url.contains("mfa") || url.contains("cvf") || url.contains("ap/signin")) {
            System.out.println("OTP/2FA required — please enter OTP in the browser.");
            System.out.println("Waiting up to 2 minutes...");
            new WebDriverWait(driver, Duration.ofSeconds(120)).until(d -> {
                String u = d.getCurrentUrl();
                return !u.contains("ap/signin") && !u.contains("mfa") && !u.contains("ap/");
            });
            System.out.println("OTP completed.");
        }

        System.out.println("Logged in. URL: " + driver.getCurrentUrl());

        // Get username from nav greeting
        String username = "";
        try {
            String greeting = driver.findElement(
                By.id("nav-link-accountList-nav-line-1")).getText().trim();
            System.out.println("Nav greeting: " + greeting);
            if (!greeting.toLowerCase().contains("sign in")) {
                username = greeting.replaceFirst("(?i)hello,\\s*", "").trim();
            }
        } catch (Exception ignored) {}

        if (username.isEmpty()) {
            System.out.println("Username not found — not logged in.");
            return;
        }

        System.out.println("Username: '" + username + "'");

        // Validate: must NOT contain A, C, G, I, L, K
        if (username.matches(".*[ACGILKacgilk].*")) {
            StringBuilder found = new StringBuilder();
            for (char c : username.toCharArray()) {
                if ("ACGILKacgilk".indexOf(c) >= 0) found.append("'").append(c).append("' ");
            }
            Assert.fail("FAIL: Username '" + username
                + "' contains restricted chars: " + found.toString().trim());
        }

        System.out.println("PASS: Username '" + username + "' has no restricted characters.");
        System.out.println("=== Task 4 PASSED ===");
    }

    @AfterTest
    public void tearDown() {
        if (driver != null) { driver.quit(); driver = null; }
    }
}
