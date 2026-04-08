import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Task 6 — Price Monitor
 *
 * - Scrapes live price from Amazon using Selenium (headless Chrome)
 * - Compares with threshold
 * - Sends Gmail alert if price drops below threshold
 * - Uses ScheduledExecutorService for periodic checks
 * - Stores price history to CSV file
 */
public class PriceMonitorTest {

    // ── Configuration ─────────────────────────────────────────────────────────
    private static final String PRODUCT_URL    = "https://www.amazon.in/dp/B0FPXKMT65";
    private static final double THRESHOLD      = 1500.0;
    private static final String SENDER_EMAIL   = "shariefsk95@gmail.com";
    private static final String APP_PASSWORD   = "your_app_password"; // Gmail App Password
    private static final String RECEIVER_EMAIL = "shariefsk95@gmail.com";
    private static final int    CHECK_COUNT    = 1;   // checks to run in test
    private static final long   INTERVAL_SECS  = 5;   // seconds between checks
    private static final String HISTORY_FILE   = "price_history.csv";
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    public void testPriceMonitor() throws InterruptedException {
        System.out.println("=".repeat(55));
        System.out.println("  Amazon Price Monitor — Task 6 (Java)");
        System.out.println("=".repeat(55));
        System.out.println("Product URL  : " + PRODUCT_URL);
        System.out.println("Threshold    : Rs." + THRESHOLD);
        System.out.println("Checks       : " + CHECK_COUNT);
        System.out.println("=".repeat(55));

        PriceScraper    scraper   = new PriceScraper(PRODUCT_URL);
        EmailNotifier   notifier  = new EmailNotifier(SENDER_EMAIL, APP_PASSWORD, RECEIVER_EMAIL);
        PriceHistoryCSV history   = new PriceHistoryCSV(HISTORY_FILE);

        AtomicInteger successCount  = new AtomicInteger(0);
        AtomicInteger checksDone    = new AtomicInteger(0);
        AtomicBoolean alertSent     = new AtomicBoolean(false);

        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        Runnable checkTask = () -> {
            int num = checksDone.incrementAndGet();
            System.out.println("\n--- Price Check #" + num + " ---");
            System.out.println("Time: " + java.time.LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

            double price = scraper.scrapePrice();

            if (price <= 0) {
                System.out.println("Could not read price. Skipping.");
                return;
            }

            successCount.incrementAndGet();
            System.out.println("Current Price : Rs." + price);
            System.out.println("Threshold     : Rs." + THRESHOLD);

            boolean belowThreshold = price < THRESHOLD;
            boolean emailSent = false;

            if (belowThreshold && !alertSent.get()) {
                System.out.println("ALERT: Price Rs." + price
                    + " is below threshold Rs." + THRESHOLD + "!");
                String title = scraper.scrapeTitle();
                System.out.println("Product: " + title);
                notifier.sendAlert(title, PRODUCT_URL, price, THRESHOLD);
                alertSent.set(true);
                emailSent = true;
            } else if (belowThreshold) {
                System.out.println("Price below threshold but alert already sent.");
            } else {
                System.out.println("Price Rs." + price
                    + " is above threshold. No alert needed.");
            }

            // Save to CSV
            history.record(PRODUCT_URL, price, THRESHOLD, emailSent);
        };

        // Run checks
        scheduler.scheduleAtFixedRate(checkTask, 0, INTERVAL_SECS, TimeUnit.SECONDS);

        // Wait for all checks to complete
        long waitTime = (INTERVAL_SECS * CHECK_COUNT) + 60;
        scheduler.awaitTermination(waitTime, TimeUnit.SECONDS);
        scheduler.shutdown();

        System.out.println("\n=== Task 6 Complete ===");
        System.out.println("Successful price reads: " + successCount.get());
        System.out.println("History saved to: " + HISTORY_FILE);

        // Assertions
        Assert.assertTrue(successCount.get() > 0,
            "FAIL: No price could be scraped from Amazon.");

        java.io.File csvFile = new java.io.File(HISTORY_FILE);
        Assert.assertTrue(csvFile.exists(),
            "FAIL: Price history CSV was not created.");

        System.out.println("PASS: Price monitor ran successfully.");
    }
}
