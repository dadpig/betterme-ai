package ai.betterme;

/**
 * Day 16 - Push notification record.
 *
 * <p>Dumb data carrier. <b>The compact constructor stays empty</b> - all
 * validation lives in {@link Notifications#push(String, String, String)}.
 */
public record PushNotification(String recipient, String title, String message)
        implements Notification {
}
