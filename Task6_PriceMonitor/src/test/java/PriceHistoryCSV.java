import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Stores price check history in a CSV file.
 * Format: timestamp, productUrl, price, threshold, alertSent
 */
public class PriceHistoryCSV {

    private static final String HEADER = "timestamp,productUrl,price,threshold,alertSent";
    private static final DateTimeFormatter FMT =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final Path filePath;

    public PriceHistoryCSV(String fileName) {
        this.filePath = Paths.get(fileName);
        init();
    }

    private void init() {
        try {
            if (!Files.exists(filePath)) {
                Files.writeString(filePath, HEADER + System.lineSeparator());
                System.out.println("Created price history file: " + filePath.toAbsolutePath());
            }
        } catch (IOException e) {
            System.out.println("Could not create history file: " + e.getMessage());
        }
    }

    public void record(String productUrl, double price, double threshold, boolean alertSent) {
        String line = String.format("%s,%s,%.2f,%.2f,%b",
            LocalDateTime.now().format(FMT), productUrl, price, threshold, alertSent);
        try {
            Files.writeString(filePath, line + System.lineSeparator(),
                StandardOpenOption.APPEND);
            System.out.println("Recorded to CSV: " + line);
        } catch (IOException e) {
            System.out.println("Failed to write CSV: " + e.getMessage());
        }
    }

    public double getLastPrice(String productUrl) {
        try {
            java.util.List<String> lines = Files.readAllLines(filePath);
            for (int i = lines.size() - 1; i >= 1; i--) {
                String[] parts = lines.get(i).split(",");
                if (parts.length >= 3 && parts[1].equals(productUrl)) {
                    return Double.parseDouble(parts[2]);
                }
            }
        } catch (Exception e) {
            System.out.println("Could not read last price: " + e.getMessage());
        }
        return -1;
    }
}
