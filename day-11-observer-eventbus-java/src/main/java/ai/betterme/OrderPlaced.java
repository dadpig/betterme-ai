package ai.betterme;

import java.util.Objects;

/**
 * An order has been placed by a customer.
 *
 * <p>Given to you complete. The compact constructor uses
 * {@link Objects#requireNonNull} so a {@code null} {@code orderId} fails fast
 * at construction — listeners never have to defend against half-built events.
 *
 * @param orderId    the order identifier; must not be null
 * @param totalCents the order total in cents (may be zero, e.g. a 100%-discount)
 */
public record OrderPlaced(String orderId, long totalCents) implements Event {

    public OrderPlaced {
        Objects.requireNonNull(orderId, "orderId must not be null");
    }
}
