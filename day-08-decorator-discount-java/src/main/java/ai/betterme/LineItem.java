package ai.betterme;

/**
 * A single line in a {@link Cart}: a SKU, unit price in cents, and a quantity.
 *
 * <p>Same shape as Day 6 — duplicated here so this folder stands on its own.
 * No cross-day imports.
 */
public record LineItem(String sku, long unitPriceCents, int quantity) {

    public LineItem {
        if (sku == null || sku.isBlank()) {
            throw new IllegalArgumentException("sku must not be blank");
        }
        if (unitPriceCents < 0) {
            throw new IllegalArgumentException("unitPriceCents must be >= 0, got " + unitPriceCents);
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("quantity must be > 0, got " + quantity);
        }
    }

    /** Convenience: total cents for this line. */
    public long lineTotalCents() {
        return unitPriceCents * quantity;
    }
}
