package ai.betterme;

/**
 * A single line in a {@link Cart}.
 *
 * <p>{@code unitPriceCents} avoids floating-point money pitfalls.
 * {@code quantity} must be {@code >= 1}.
 */
public record LineItem(String name, long unitPriceCents, int quantity) {

    public LineItem {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name must not be blank");
        }
        if (unitPriceCents < 0) {
            throw new IllegalArgumentException("unitPriceCents must be >= 0");
        }
        if (quantity < 1) {
            throw new IllegalArgumentException("quantity must be >= 1");
        }
    }
}
