package ai.betterme;

import java.util.Objects;

/**
 * An order has been cancelled before fulfillment.
 *
 * <p>Given to you complete.
 *
 * @param orderId the order identifier; must not be null
 * @param reason  a short human-readable reason; must not be null
 */
public record OrderCancelled(String orderId, String reason) implements Event {

    public OrderCancelled {
        Objects.requireNonNull(orderId, "orderId must not be null");
        Objects.requireNonNull(reason, "reason must not be null");
    }
}
