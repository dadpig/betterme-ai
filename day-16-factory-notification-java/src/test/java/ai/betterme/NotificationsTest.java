package ai.betterme;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The acceptance spec for Day 16. Every {@code @Test}-annotated method
 * below is one scenario that {@link Notifications} (and the sealed
 * {@link Notification} hierarchy) must satisfy.
 *
 * <p>The file is written exactly as it would be against real JUnit 5;
 * the in-tree shim under {@code org.junit.jupiter.api.*} lets it compile
 * and run without any external jars. Migrating to real Jupiter later
 * is a 3-step swap - this file needs no edits.
 *
 * <p>Treat the tests as the fixed contract. Adding your own extra
 * {@code @Test} methods to probe edge cases is encouraged.
 */
public class NotificationsTest {

    // ---- Typed factories: happy path --------------------------------------

    @Test
    public void emailFactoryProducesEmailNotification() {
        EmailNotification n = Notifications.email("a@b.com", "Welcome", "Hi there");
        assertEquals("a@b.com", n.recipient());
        assertEquals("Welcome", n.subject());
        assertEquals("Hi there", n.message());
    }

    @Test
    public void smsFactoryProducesSmsNotification() {
        SmsNotification n = Notifications.sms("+15551234567", "Code: 4821");
        assertEquals("+15551234567", n.recipient());
        assertEquals("Code: 4821", n.message());
    }

    @Test
    public void pushFactoryProducesPushNotification() {
        PushNotification n = Notifications.push("device-abc", "Reminder", "Meeting at 3pm");
        assertEquals("device-abc", n.recipient());
        assertEquals("Reminder", n.title());
        assertEquals("Meeting at 3pm", n.message());
    }

    // ---- Render dispatch (default method, switch on sealed type) ----------

    @Test
    public void emailRendersWithBracketedChannelSubjectAndMessage() {
        Notification n = Notifications.email("a@b.com", "Welcome", "Hi there");
        assertEquals("[EMAIL to a@b.com] Welcome: Hi there", n.render());
    }

    @Test
    public void smsRendersWithBracketedChannelAndMessage() {
        Notification n = Notifications.sms("+15551234567", "Code: 4821");
        assertEquals("[SMS to +15551234567] Code: 4821", n.render());
    }

    @Test
    public void pushRendersWithBracketedChannelTitleAndMessage() {
        Notification n = Notifications.push("device-abc", "Reminder", "Meeting at 3pm");
        assertEquals("[PUSH to device-abc] Reminder - Meeting at 3pm", n.render());
    }

    // ---- Typed factories: null / blank / empty rejection ------------------

    @Test
    public void emailRejectsNullRecipient() {
        assertThrows(IllegalArgumentException.class,
                () -> Notifications.email(null, "Welcome", "Hi there"));
    }

    @Test
    public void emailRejectsBlankRecipient() {
        assertThrows(IllegalArgumentException.class,
                () -> Notifications.email("   ", "Welcome", "Hi there"));
    }

    @Test
    public void emailRejectsEmptyRecipient() {
        assertThrows(IllegalArgumentException.class,
                () -> Notifications.email("", "Welcome", "Hi there"));
    }

    @Test
    public void emailRejectsBlankSubject() {
        assertThrows(IllegalArgumentException.class,
                () -> Notifications.email("a@b.com", "  ", "Hi there"));
    }

    @Test
    public void emailRejectsBlankMessage() {
        assertThrows(IllegalArgumentException.class,
                () -> Notifications.email("a@b.com", "Welcome", ""));
    }

    @Test
    public void smsRejectsNullRecipient() {
        assertThrows(IllegalArgumentException.class,
                () -> Notifications.sms(null, "Code"));
    }

    @Test
    public void smsRejectsBlankMessage() {
        assertThrows(IllegalArgumentException.class,
                () -> Notifications.sms("+15551234567", "   "));
    }

    @Test
    public void pushRejectsNullTitle() {
        assertThrows(IllegalArgumentException.class,
                () -> Notifications.push("device-abc", null, "Meeting"));
    }

    @Test
    public void pushRejectsBlankMessage() {
        assertThrows(IllegalArgumentException.class,
                () -> Notifications.push("device-abc", "Reminder", ""));
    }

    @Test
    public void iaeMessageNamesOffendingField() {
        // Validation errors must mention the field name - "recipient",
        // "subject", "message", etc. A bare "argument is null" is not
        // useful; the caller needs to know WHICH argument.
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> Notifications.email("   ", "Welcome", "Hi there"));
        assertNotNull(ex.getMessage());
        assertTrue(ex.getMessage().toLowerCase().contains("recipient"),
                "expected message to name 'recipient', was: " + ex.getMessage());
    }

    // ---- fromChannel: happy path for every channel ------------------------

    @Test
    public void fromChannelBuildsEmail() {
        Notification n = Notifications.fromChannel("email", Map.of(
                "recipient", "a@b.com",
                "subject", "Hi",
                "message", "Hello"));
        assertTrue(n instanceof EmailNotification,
                "expected EmailNotification, was: " + n.getClass().getSimpleName());
        assertEquals("a@b.com", n.recipient());
        assertEquals("Hello", n.message());
    }

    @Test
    public void fromChannelBuildsSms() {
        Notification n = Notifications.fromChannel("sms", Map.of(
                "recipient", "+1555",
                "message", "Code"));
        assertTrue(n instanceof SmsNotification,
                "expected SmsNotification, was: " + n.getClass().getSimpleName());
        assertEquals("+1555", n.recipient());
        assertEquals("Code", n.message());
    }

    @Test
    public void fromChannelBuildsPush() {
        Notification n = Notifications.fromChannel("push", Map.of(
                "recipient", "device-abc",
                "title", "Reminder",
                "message", "Meeting"));
        assertTrue(n instanceof PushNotification,
                "expected PushNotification, was: " + n.getClass().getSimpleName());
        assertEquals("device-abc", n.recipient());
        assertEquals("Meeting", n.message());
    }

    // ---- fromChannel: case-insensitive channel matching -------------------

    @Test
    public void fromChannelIsCaseInsensitiveUpper() {
        Notification n = Notifications.fromChannel("EMAIL", Map.of(
                "recipient", "a@b.com", "subject", "Hi", "message", "Hello"));
        assertTrue(n instanceof EmailNotification);
    }

    @Test
    public void fromChannelIsCaseInsensitiveMixed() {
        Notification n = Notifications.fromChannel("eMaIl", Map.of(
                "recipient", "a@b.com", "subject", "Hi", "message", "Hello"));
        assertTrue(n instanceof EmailNotification);
    }

    @Test
    public void fromChannelIsCaseInsensitiveForAllThreeChannels() {
        assertTrue(Notifications.fromChannel("Email", Map.of(
                "recipient", "a@b.com", "subject", "Hi", "message", "Hello"))
                instanceof EmailNotification);
        assertTrue(Notifications.fromChannel("SMS", Map.of(
                "recipient", "+1555", "message", "Code"))
                instanceof SmsNotification);
        assertTrue(Notifications.fromChannel("Push", Map.of(
                "recipient", "device-abc", "title", "T", "message", "Hello"))
                instanceof PushNotification);
    }

    // ---- fromChannel: unknown channel -------------------------------------

    @Test
    public void fromChannelUnknownChannelRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> Notifications.fromChannel("slack", Map.of("recipient", "x")));
    }

    @Test
    public void fromChannelUnknownChannelMessageEchoesOriginalCasing() {
        // The error should echo what the caller actually passed, not the
        // lowercased version - the original casing is the useful debugging
        // signal.
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> Notifications.fromChannel("SLACK", Map.of("recipient", "x")));
        assertNotNull(ex.getMessage());
        assertTrue(ex.getMessage().contains("SLACK"),
                "expected message to echo 'SLACK', was: " + ex.getMessage());
    }

    // ---- fromChannel: missing required keys -------------------------------

    @Test
    public void fromChannelEmailMissingSubjectRejected() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> Notifications.fromChannel("email", Map.of(
                        "recipient", "a@b.com",
                        "message", "Hello")));
        assertNotNull(ex.getMessage());
        assertTrue(ex.getMessage().toLowerCase().contains("subject"),
                "expected message to name 'subject', was: " + ex.getMessage());
    }

    @Test
    public void fromChannelSmsMissingMessageRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> Notifications.fromChannel("sms", Map.of("recipient", "+1555")));
    }

    @Test
    public void fromChannelPushMissingTitleRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> Notifications.fromChannel("push", Map.of(
                        "recipient", "device-abc",
                        "message", "Hello")));
    }

    @Test
    public void fromChannelEmailMissingRecipientReportsRecipientFirst() {
        // When multiple keys are missing, report the FIRST in declaration
        // order. For email the order is recipient, subject, message - so
        // an empty params map should complain about "recipient" first.
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> Notifications.fromChannel("email", new HashMap<>()));
        assertNotNull(ex.getMessage());
        assertTrue(ex.getMessage().toLowerCase().contains("recipient"),
                "expected first missing key to be 'recipient', was: " + ex.getMessage());
    }

    // ---- fromChannel: blank values delegated to typed-factory validation --

    @Test
    public void fromChannelBlankValueRejected() {
        // Validation should be the same whether you go through the typed
        // factory or fromChannel - blank values are not OK in either path.
        Map<String, String> params = new HashMap<>();
        params.put("recipient", "  ");
        params.put("subject", "Hi");
        params.put("message", "Hello");
        assertThrows(IllegalArgumentException.class,
                () -> Notifications.fromChannel("email", params));
    }

    // ---- fromChannel: null inputs -----------------------------------------

    @Test
    public void fromChannelNullChannelRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> Notifications.fromChannel(null, Map.of("recipient", "x")));
    }

    @Test
    public void fromChannelNullParamsRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> Notifications.fromChannel("email", null));
    }

    // ---- Non-instantiability of the utility class -------------------------

    @Test
    public void notificationsUtilityClassIsNotInstantiable() throws Exception {
        // Reflection bypasses the private modifier. The constructor must
        // additionally throw on entry (AssertionError) so even reflective
        // instantiation fails. This is the standard non-instantiable
        // utility-class idiom: see java.lang.Math, java.util.Collections.
        Constructor<Notifications> ctor = Notifications.class.getDeclaredConstructor();
        ctor.setAccessible(true);
        try {
            ctor.newInstance();
            // If we reach this line the test fails - the constructor was
            // expected to throw.
            assertFalse(true, "expected Notifications constructor to throw");
        } catch (java.lang.reflect.InvocationTargetException e) {
            Throwable cause = e.getCause();
            assertTrue(cause instanceof AssertionError,
                    "expected AssertionError from private constructor, was: "
                            + (cause == null ? "null" : cause.getClass().getSimpleName()));
        }
    }
}
