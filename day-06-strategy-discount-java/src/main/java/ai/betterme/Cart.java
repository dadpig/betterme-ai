package ai.betterme;

import java.util.List;

/**
 * A shopping cart: an immutable list of line items.
 *
 * <p>The compact constructor defensively copies the incoming list so callers
 * cannot mutate the cart after construction. Use {@link #subtotalCents()} as
 * the canonical pre-discount total.
 */
public record Cart(List<LineItem> items) {

    public Cart {
        if (items == null) {
            throw new IllegalArgumentException("items must not be null");
        }
        items = List.copyOf(items); // defensive copy + immutable view
    }

    /** Sum of {@code unitPriceCents * quantity} across all line items. */
    public long subtotalCents() {
        // TODO: implement using a stream or a simple loop.
        //       Hint: items.stream().mapToLong(li -> li.unitPriceCents() * li.quantity()).sum();
        throw new UnsupportedOperationException("TODO: implement subtotalCents");
    }
}
