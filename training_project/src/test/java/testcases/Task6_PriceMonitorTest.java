package testcases;

import com.ama.qa.base.TestBase;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import jakarta.mail.*;
import jakarta.mail.internet.*;

import java.io.IOException;
import java.nio.file.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

/**
 * Internship Task 6:
 * Monitor product price on Amazon.
 * - Scrapes price using Selenium
 * - Compares with threshold
 * - Sends email alert if price drops below threshold
 * - Saves history to CSV
 * - Uses ScheduledExecutorService for scheduling
 */
public class Task6_PriceMonitorTest extends TestBase {

    private static final String PRODUCT_URL    = "https://www.amazon.in/dp/B0FPXKMT65";
    private static final double THRESHOLD      = 1500.0;
    private static final String SENDER_EMAIL   = "shariefsk95@gmail.com";
    private static final String APP_PASSWORD   = "your_app_password"; // Gmail App Password
    private static final String RECEIVER_EMAIL = "shariefsk95@gmail.com";
    private static final String HISTORY_FILE   = "price_history.csv";
    private static final int    CHECK_COUNT    = 1;
    private static final long   INTERVAL_SECS  = 5;

    @BeforeTest
    public void setup() { initialization(); }

    @Test
    public void testPriceMonitorAndAlert() throws InterruptedException {
        System.out.println("=== Task 6: Price Monitor ===");
        System.out.println("Product : " + PRODUCT_URL);
        System.out.println("Threshold: Rs." + THRESHOLD);

        initCsv();

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicBoolean alertSent    = new AtomicBoolean(false);
        AtomicInteger checksDone   = new AtomicInteger(0);

        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        Runnable checkTask = () -> {
            int num = checksDone.incrementAndGet();
            System.out.println("\n--- Check #" + num + " ---");

            double price = scrapePrice();
            if (price <= 0) { System.out.println("Price not found."); return; }

            successCount.incrementAndGet();
            System.out.println("Price: Rs." + price + " | Threshold: Rs." + THRESHOLD);

            boolean below = price < THRESHOLD;
            boolean sent  = false;

            if (below && !alertSent.get()) {
                System.out.println("ALERT: Price dropped below threshold!");
                sendEmailAlert(price);
                alertSent.set(true);
                sent = true;
            } else if (below) {
                System.out.println("Alert already sent.");
            } else {
                System.out.println("Price above threshold. No alert.");
            }

            recordCsv(price, sent);
        };

        scheduler.scheduleAtFixedRate(checkTask, 0, INTERVAL_SECS, TimeUnit.SECONDS);
        scheduler.awaitTermination((INTERVAL_SECS * CHECK_COUNT) + 60, TimeUnit.SECONDS);
        scheduler.shutdown();

        System.out.println("\nSuccessful reads: " + successCount.get());
        Assert.assertTrue(successCount.get() > 0, "No price could be scraped.");

        java.io.File csv = new java.io.File(HISTORY_FILE);
        Assert.assertTrue(csv.exists(), "CSV history file not created.");
        System.out.println("PASS: Price monitor ran. CSV saved.");
        System.out.println("=== Task 6 PASSED ===");
    }

    private double scrapePrice() {
        try {
            driver.get(PRODUCT_URL);
            new WebDriverWait(driver, Duration.ofSeconds(15))
                .until(ExpectedConditions.presenceOfElementLocated(By.id("productTitle")));

            String[] selectors = {
                ".a-price .a-offscreen", "#priceblock_ourprice",
                "#corePrice_feature_div .a-offscreen", ".priceToPay .a-offscreen"
            };
            for (String sel : selectors) {
                try {
                    WebElement el = driver.findElement(By.cssSelector(sel));
                    String raw = el.getAttribute("innerHTML");
                    if (raw == null || raw.isBlank()) raw = el.getText();
                    double p = parsePrice(raw);
                    if (p > 0) { System.out.println("Price found: Rs." + p); return p; }
                } catch (Exception ignored) {}
            }
        } catch (Exception e) { System.out.println("Scrape error: " + e.getMessage()); }
        return -1;
    }

    private void sendEmailAlert(double price) {
        Properties props = new Properties();
        props.put("mail.smtp.host",        "smtp.gmail.com");
        props.put("mail.smtp.port",        "465");
        props.put("mail.smtp.ssl.enable",  "true");
        props.put("mail.smtp.auth",        "true");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SENDER_EMAIL, APP_PASSWORD);
            }
        });
        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(SENDER_EMAIL));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(RECEIVER_EMAIL));
            msg.setSubject("Price Alert: Product dropped to Rs." + (int) price);
            msg.setText("Price Rs." + price + " is below threshold Rs." + THRESHOLD
                + "\nURL: " + PRODUCT_URL);
            Transport.send(msg);
            System.out.println("Email alert sent.");
        } catch (MessagingException e) {
            System.out.println("Email failed: " + e.getMessage());
            System.out.println("(Set Gmail App Password at myaccount.google.com/apppasswords)");
        }
    }

    private void initCsv() {
        try {
            Path p = Paths.get(HISTORY_FILE);
            if (!Files.exists(p))
                Files.writeString(p, "timestamp,url,price,threshold,alertSent\n");
        } catch (IOException e) { System.out.println("CSV init error: " + e.getMessage()); }
    }

    private void recordCsv(double price, boolean alertSent) {
        String line = String.format("%s,%s,%.2f,%.2f,%b\n",
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
            PRODUCT_URL, price, THRESHOLD, alertSent);
        try {
            Files.writeString(Paths.get(HISTORY_FILE), line, StandardOpenOption.APPEND);
            System.out.println("Recorded: " + line.trim());
        } catch (IOException e) { System.out.println("CSV write error: " + e.getMessage()); }
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
