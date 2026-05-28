package ai.betterme;

/**
 * Day 16 - SMS notification record.
 *
 * <p>Dumb data carrier. <b>The compact constructor stays empty</b> - all
 * validation lives in {@link Notifications#sms(String, String)}.
 */
public record SmsNotification(String recipient, String message)
        implements Notification {
}
