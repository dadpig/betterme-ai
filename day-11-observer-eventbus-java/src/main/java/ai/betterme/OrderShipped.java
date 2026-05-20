package ai.betterme;

import java.util.Objects;

/**
 * An order has been handed to the carrier and is now in transit.
 *
 * <p>Given to you complete.
 *
 * @param orderId        the order identifier; must not be null
 * @param trackingNumber the carrier tracking number; must not be null
 */
public record OrderShipped(String orderId, String trackingNumber) implements Event {

    public OrderShipped {
        Objects.requireNonNull(orderId, "orderId must not be null");
        Objects.requireNonNull(trackingNumber, "trackingNumber must not be null");
    }
}
