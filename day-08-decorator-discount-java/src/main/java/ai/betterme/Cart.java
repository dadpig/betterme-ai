package ai.betterme;

import java.util.List;

/**
 * A cart is an immutable list of {@link LineItem}s.
 *
 * <p>The compact constructor takes a defensive copy via {@link List#copyOf} so
 * callers can't mutate the cart after construction.
 */
public record Cart(List<LineItem> items) {

    public Cart {
        if (items == null) {
            throw new IllegalArgumentException("items must not be null");
        }
        items = List.copyOf(items);
    }

    /** Sum of every line's {@code unitPriceCents * quantity}. */
    public long subtotalCents() {
        return items.stream().mapToLong(LineItem::lineTotalCents).sum();
    }
}
