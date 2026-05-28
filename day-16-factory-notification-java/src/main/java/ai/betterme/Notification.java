package ai.betterme;

/**
 * Day 16 - Sealed type for notification messages.
 *
 * <p>A {@code Notification} is one of exactly three shapes:
 * {@link EmailNotification}, {@link SmsNotification}, {@link PushNotification}.
 * The {@code sealed ... permits ...} clause is how the compiler knows that
 * list is complete - which means a {@code switch} expression over the type
 * doesn't need a {@code default} branch. If you ever add a fourth subtype,
 * every exhaustive switch in the codebase will fail to compile until you
 * handle the new case. That's the whole point of sealed.
 *
 * <h2>Why a sealed interface, not an abstract class?</h2>
 *
 * <p>Same rationale as Day 11's {@code Event}: the subtypes are
 * {@code record}s (which can't extend a class) and there's no shared state
 * to inherit. A sealed interface gives you "ADT-style" closed unions with
 * zero boilerplate.
 *
 * <h2>Idioms to apply</h2>
 *
 * <ul>
 *   <li>{@link #render()} is a {@code default} method that uses a
 *       <b>pattern-matching switch expression</b> on {@code this}. No
 *       {@code default} branch - the compiler must prove exhaustiveness
 *       from {@code permits}. Do NOT override {@code render()} per-record;
 *       per-record override works but throws away the exhaustiveness
 *       check, which is today's whole lesson.</li>
 *   <li>Records implement this interface but contain <b>no logic</b> -
 *       they're dumb data carriers. All validation lives in
 *       {@link Notifications}, the single legal entry point. Compact
 *       constructors stay empty.</li>
 * </ul>
 */
public sealed interface Notification
        permits EmailNotification, SmsNotification, PushNotification {

    /** The destination identifier (email address, phone number, device token). */
    String recipient();

    /** The body of the notification. */
    String message();

    /**
     * One-line human-readable summary, dispatched via pattern-matching
     * switch on the sealed hierarchy. Exact formats:
     *
     * <pre>
     *   EMAIL: "[EMAIL to {recipient}] {subject}: {message}"
     *   SMS:   "[SMS to {recipient}] {message}"
     *   PUSH:  "[PUSH to {recipient}] {title} - {message}"
     * </pre>
     *
     * <p>Use a {@code switch} expression with pattern matching on the
     * three permitted subtypes. No {@code default} branch.
     */
    default String render() {

        return switch (this) {
            case EmailNotification(String recipient, String subject, String message) -> String
                    .format("[EMAIL to %s] %s: %s", recipient, subject, message);
            case SmsNotification(String recipient, String message)  -> String
                    .format("[SMS to %s] %s", recipient, message);
            case PushNotification(String recipient, String title, String message) -> String
                    .format("[PUSH to %s] %s - %s", recipient, title, message);
            default -> "Unexpected value: " + this;
        };
        // STEP-BY-STEP IMPLEMENTATION
        //
        // 1. Write a `switch` EXPRESSION (not statement) on `this`. Assign the
        //    result to `return` directly:  return switch (this) { ... };
        //
        // 2. Add exactly ONE arm per permitted subtype, using record-pattern
        //    deconstruction so the components are bound as locals:
        //       case EmailNotification(String r, String s, String m) -> ...
        //       case SmsNotification(String r, String m)             -> ...
        //       case PushNotification(String r, String t, String m)  -> ...
        //
        // 3. For each arm, return a String.format(...) (or string concatenation)
        //    matching the exact formats from the Javadoc above:
        //       EMAIL: "[EMAIL to {recipient}] {subject}: {message}"
        //       SMS:   "[SMS to {recipient}] {message}"
        //       PUSH:  "[PUSH to {recipient}] {title} - {message}"
        //    The test suite asserts the exact strings - watch the punctuation:
        //    EMAIL uses ": " between subject and message; PUSH uses " - ".
        //
        // 4. DO NOT add a `default` branch. The compiler must prove
        //    exhaustiveness from the `permits` clause. If a fourth subtype is
        //    added later, this method should fail to compile until it's
        //    handled - that compile-time safety is the whole point of sealed.

    }
}
