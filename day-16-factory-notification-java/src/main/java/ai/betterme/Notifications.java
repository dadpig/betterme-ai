package ai.betterme;

import java.util.Locale;
import java.util.Map;

/**
 * Day 16 - Factory Method pattern, applied to a tiny notification dispatch
 * model.
 *
 * <p>A non-instantiable utility class that is <b>the single legal source</b>
 * of {@link Notification} instances. Callers should never write
 * {@code new EmailNotification(...)} directly - they go through
 * {@link #email(String, String, String)}, {@link #sms(String, String)},
 * {@link #push(String, String, String)}, or the generic string-keyed
 * {@link #fromChannel(String, Map)}.
 *
 * <h2>Why static factories instead of public constructors</h2>
 *
 * <p>This is <i>Effective Java</i> Item 1, made concrete:
 *
 * <ul>
 *   <li><b>One place for validation.</b> Records are dumb data carriers;
 *       their compact constructors stay empty. The factory is where
 *       null/blank checks live - and because the factory is the only
 *       legal entry point, you can never construct an invalid value.</li>
 *   <li><b>Names that document intent.</b> {@code Notifications.sms(num, msg)}
 *       reads better than {@code new SmsNotification(num, msg)} at the
 *       call site.</li>
 *   <li><b>Decoupling.</b> Callers depend on the {@code Notification}
 *       interface and the factory, not on which concrete record they
 *       got back. Adding a fourth notification type later doesn't break
 *       any existing call site.</li>
 *   <li><b>Data-driven dispatch via {@link #fromChannel(String, Map)}.</b>
 *       A {@code switch} on {@code channel.toLowerCase(Locale.ROOT)} -
 *       NOT a chain of {@code equalsIgnoreCase} ifs. Same idiom as
 *       Days 5b (Roman numerals) and 12 (Balanced Brackets), now
 *       applied to type selection.</li>
 * </ul>
 *
 * <h2>The non-instantiability idiom</h2>
 *
 * <p>The constructor is {@code private} and throws an {@code AssertionError}
 * on entry. That defends against:
 *
 * <ul>
 *   <li>Accidental {@code new Notifications()} inside the same package
 *       (private constructor handles this).</li>
 *   <li>Reflective {@code setAccessible(true).newInstance()} (the
 *       {@code AssertionError} handles this - and the test suite
 *       verifies it).</li>
 * </ul>
 *
 * <h2>Validation contract (applies to every factory)</h2>
 *
 * <ul>
 *   <li>{@code null}, {@code ""}, or all-whitespace input for any string
 *       field rejects with {@link IllegalArgumentException}.</li>
 *   <li>The exception message <b>names the offending field</b>
 *       (e.g. {@code "recipient must not be blank"}).</li>
 *   <li>For {@link #fromChannel}: unknown channel rejects with
 *       {@code "unknown channel: <original-casing>"}; missing required
 *       key rejects with {@code "missing required key: <key>"}.</li>
 * </ul>
 */
public final class Notifications {

    private Notifications() {
        throw new AssertionError("no instances");
    }

    /**
     * Builds an {@link EmailNotification}. All three arguments must be
     * non-null and non-blank.
     *
     * @throws IllegalArgumentException if any argument is null or blank.
     */
    public static EmailNotification email(String recipient, String subject, String message) {
        required(recipient, "recipient");
        required(subject, "subject");
        required(message, "message");

        return new EmailNotification(recipient, subject, message);
    }

    private static void required(String input, String name) {
        if (null == input || input.isBlank()){
            throw new IllegalArgumentException(name +" must not be blank");
        }
    }

    /**
     * Builds an {@link SmsNotification}. Both arguments must be non-null
     * and non-blank.
     *
     * @throws IllegalArgumentException if any argument is null or blank.
     */
    public static SmsNotification sms(String recipient, String message) {
        required(recipient, "recipient");
        required(message, "message");
        return  new SmsNotification(recipient, message);
    }

    /**
     * Builds a {@link PushNotification}. All three arguments must be
     * non-null and non-blank.
     *
     * @throws IllegalArgumentException if any argument is null or blank.
     */
    public static PushNotification push(String recipient, String title, String message) {
        required(recipient, "recipient");
        required(title, "tile");
        required(message, "message");
        return  new PushNotification(recipient, title, message);

    }

    /**
     * Generic string-keyed factory. Dispatches on {@code channel} (case
     * insensitive, {@code Locale.ROOT}) to pick a concrete record, then
     * pulls the required keys from {@code params}.
     *
     * <p>Required keys per channel:
     *
     * <ul>
     *   <li>{@code "email"} - {@code recipient}, {@code subject}, {@code message}</li>
     *   <li>{@code "sms"}   - {@code recipient}, {@code message}</li>
     *   <li>{@code "push"}  - {@code recipient}, {@code title}, {@code message}</li>
     * </ul>
     *
     * <p>Implementation must be a {@code switch} on
     * {@code channel.toLowerCase(Locale.ROOT)} - NOT a chain of
     * {@code equalsIgnoreCase} ifs. The {@code Locale.ROOT} matters:
     * with the default locale, {@code "INFO".toLowerCase()} in Turkish
     * returns {@code "ınfo"} (dotless i) and breaks the match. This is
     * the textbook example, drilled in real codebases.
     *
     * @throws IllegalArgumentException if {@code channel} or {@code params}
     *         is null, the channel is unknown (message:
     *         {@code "unknown channel: <original>"}, echo original casing),
     *         a required key is missing
     *         ({@code "missing required key: <key>"} - report the FIRST
     *         missing key in declaration order), or any required value is
     *         null/blank (delegated to the typed factory's validation).
     */
    public static Notification fromChannel(String channel, Map<String, String> params) {
        required(channel, "channel");
        String channelNormalized = channel.toLowerCase(Locale.ROOT);

        return switch (channelNormalized) {
                       case "email" -> email(
                                require(params, "recipient"),
                                require(params, "subject"),
                                require(params, "message"));
                        case "sms"   -> sms(
                                require(params, "recipient"),
                                require(params, "message"));
                        case "push"  -> push(
                                require(params, "recipient"),
                                require(params, "title"),
                                require(params, "message"));
                        default -> throw new IllegalArgumentException(
                                "unknown channel: " + channel); // ECHO ORIGINAL CASING
                    };

    }

    private static String require(Map<String, String> params, String key) {
        String value = params.get(key);
        if (null == value)
            throw new IllegalArgumentException("missing required key: " + key);
        return value;
    }
    // ------------------------------------------------------------------------
    //  Demo entry point - quick smoke check while you iterate.
    //  Real verification lives in src/test/.../NotificationsTest.java.
    // ------------------------------------------------------------------------
    public static void main(String[] args) {
        Notification[] samples = {
                email("a@b.com", "Welcome", "Hi there"),
                sms("+15551234567", "Code: 4821"),
                push("device-abc", "Reminder", "Meeting at 3pm"),
                fromChannel("EMAIL", Map.of(
                        "recipient", "x@y.com",
                        "subject", "From channel",
                        "message", "Hello from fromChannel")),
        };
        for (Notification n : samples) {
            System.out.println(n.render());
        }
    }
}
