import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;
import java.time.Duration;

public class LoginPage {

    WebDriver driver;
    WebDriverWait wait;
    JavascriptExecutor js;

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        this.js = (JavascriptExecutor) driver;
    }

    public void clickSignIn() {
        // Navigate directly to sign-in page — avoids FedCM popup from nav click
        driver.get("https://www.amazon.in/ap/signin?" +
            "openid.pape.max_auth_age=0" +
            "&openid.return_to=https%3A%2F%2Fwww.amazon.in%2F" +
            "&openid.identity=http%3A%2F%2Fspecs.openid.net%2Fauth%2F2.0%2Fidentifier_select" +
            "&openid.assoc_handle=inflex" +
            "&openid.mode=checkid_setup" +
            "&openid.claimed_id=http%3A%2F%2Fspecs.openid.net%2Fauth%2F2.0%2Fidentifier_select" +
            "&openid.ns=http%3A%2F%2Fspecs.openid.net%2Fauth%2F2.0");
        try { Thread.sleep(3000); } catch (Exception ignored) {}
        System.out.println("Sign-in page loaded. Title: " + driver.getTitle());
    }

    public void enterCredentials(String email, String password) {
        // Try email field — use By.name as fallback (worked in previous run)
        WebElement emailField = null;
        By[] emailSelectors = {
            By.name("email"), By.id("ap_email"),
            By.cssSelector("input[type='email']")
        };
        for (By sel : emailSelectors) {
            try {
                emailField = wait.until(ExpectedConditions.presenceOfElementLocated(sel));
                System.out.println("Email field found: " + sel);
                break;
            } catch (Exception ignored) {}
        }
        if (emailField == null) throw new RuntimeException("Email field not found.");

        js.executeScript("arguments[0].removeAttribute('readonly');", emailField);
        js.executeScript("arguments[0].value = arguments[1];", emailField, email);
        js.executeScript("arguments[0].dispatchEvent(new Event('input',{bubbles:true}));", emailField);
        js.executeScript("arguments[0].dispatchEvent(new Event('change',{bubbles:true}));", emailField);
        System.out.println("Email entered: " + email);

        // Click Continue — try direct click first, then JS, then ENTER key
        try {
            WebElement continueBtn = wait.until(
                ExpectedConditions.elementToBeClickable(By.id("continue")));
            continueBtn.click();
        } catch (Exception e1) {
            try {
                js.executeScript("document.getElementById('continue').click();");
            } catch (Exception e2) {
                js.executeScript(
                    "var e = document.getElementById('ap_email');" +
                    "e.dispatchEvent(new KeyboardEvent('keydown',{key:'Enter',bubbles:true}));" +
                    "e.dispatchEvent(new KeyboardEvent('keyup',{key:'Enter',bubbles:true}));");
            }
        }
        System.out.println("Clicked Continue.");
        try { Thread.sleep(3000); } catch (Exception ignored) {}
        System.out.println("After Continue URL: " + driver.getCurrentUrl());

        WebElement passField = wait.until(
            ExpectedConditions.presenceOfElementLocated(By.id("ap_password")));
        js.executeScript("arguments[0].removeAttribute('readonly');", passField);
        js.executeScript("arguments[0].value = arguments[1];", passField, password);
        js.executeScript("arguments[0].dispatchEvent(new Event('input',{bubbles:true}));", passField);
        js.executeScript("arguments[0].dispatchEvent(new Event('change',{bubbles:true}));", passField);
        System.out.println("Password entered.");

        js.executeScript("document.getElementById('signInSubmit').click();");
        System.out.println("Clicked Sign In.");
        try { Thread.sleep(4000); } catch (Exception ignored) {}
        System.out.println("Post-submit URL: " + driver.getCurrentUrl());
    }

    public boolean waitForManualOtp() {
        System.out.println("=== OTP/2FA detected — please enter OTP in the browser ===");
        System.out.println("Waiting up to 2 minutes...");
        WebDriverWait longWait = new WebDriverWait(driver, Duration.ofSeconds(120));
        try {
            longWait.until(d -> {
                String url = d.getCurrentUrl();
                return !url.contains("ap/signin") && !url.contains("mfa")
                    && !url.contains("cvf") && !url.contains("ap/");
            });
            System.out.println("OTP completed. URL: " + driver.getCurrentUrl());
            return true;
        } catch (Exception e) {
            System.out.println("Timed out waiting for OTP.");
            return false;
        }
    }

    public void navigateToProfile() {
        driver.get("https://www.amazon.in/gp/css/homepage.html");
        try { Thread.sleep(2000); } catch (Exception ignored) {}
        System.out.println("Profile page URL: " + driver.getCurrentUrl());
    }

    public void validateUsername() {
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
            try {
                username = driver.findElement(
                    By.xpath("//span[contains(@class,'a-profile-name')]"))
                    .getText().trim();
            } catch (Exception ignored) {}
        }

        if (username.isEmpty()) {
            System.out.println("Username not found — not logged in.");
            return;
        }
        validateUsernameDirectly(username);
    }

    public void validateUsernameDirectly(String username) {
        System.out.println("Validating username: '" + username + "'");
        if (username.matches(".*[ACGILKacgilk].*")) {
            StringBuilder found = new StringBuilder();
            for (char c : username.toCharArray()) {
                if ("ACGILKacgilk".indexOf(c) >= 0)
                    found.append("'").append(c).append("' ");
            }
            throw new AssertionError("FAIL: Username '" + username
                + "' contains restricted chars: " + found.toString().trim());
        }
        System.out.println("PASS: Username '" + username
            + "' has no restricted characters (A,C,G,I,L,K).");
    }
}
