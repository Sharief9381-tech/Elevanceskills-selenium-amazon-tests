import java.time.LocalTime;

public class TimeUtil {
    public static boolean isWithinTimeRange(int startHour, int endHour) {
        // Allow bypass via -DFORCE_RUN=true for testing outside time window
        if ("true".equalsIgnoreCase(System.getProperty("FORCE_RUN"))) return true;

        LocalTime now = LocalTime.now();
        return !now.isBefore(LocalTime.of(startHour, 0)) &&
               !now.isAfter(LocalTime.of(endHour, 0));
    }
}
