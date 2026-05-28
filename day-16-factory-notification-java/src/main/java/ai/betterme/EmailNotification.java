package ai.betterme;

/**
 * Day 16 - Email notification record.
 *
 * <p>Dumb data carrier. <b>The compact constructor stays empty</b> - all
 * validation lives in {@link Notifications#email(String, String, String)}.
 * That's the whole point of using static factories: one legal entry
 * point, one place validation happens, records remain pure data.
 */
public record EmailNotification(String recipient, String subject, String message)
        implements Notification {
}
