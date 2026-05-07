package ai.betterme;

/**
 * Concrete {@link DiscountStrategy} implementations.
 *
 * <p>Grouped here as static nested classes so the four strategies are easy to
 * read side-by-side. In a real codebase each would typically live in its own
 * file. Keeping them small and constructor-validated is the point.
 */
public final class Discounts {

    private Discounts() { /* utility holder */ }

    // ---------------------------------------------------------------------
    // 1. No discount.
    // ---------------------------------------------------------------------
    public static final class NoDiscount implements DiscountStrategy {
        @Override
        public long discountCents(Cart cart) {
            // TODO: trivially return 0.
            throw new UnsupportedOperationException("TODO");
        }
    }

    // ---------------------------------------------------------------------
    // 2. Percentage off the subtotal. percent in [0, 100].
    // ---------------------------------------------------------------------
    public static final class PercentageOff implements DiscountStrategy {
        private final int percent;

        public PercentageOff(int percent) {
            // TODO: validate percent in [0, 100].
            this.percent = percent;
        }

        @Override
        public long discountCents(Cart cart) {
            // TODO: return subtotal * percent / 100. Mind integer arithmetic.
            throw new UnsupportedOperationException("TODO");
        }
    }

    // ---------------------------------------------------------------------
    // 3. Flat amount off, capped externally by Checkout (but you may also cap
    //    here defensively — document your choice).
    // ---------------------------------------------------------------------
    public static final class FixedAmountOff implements DiscountStrategy {
        private final long amountCents;

        public FixedAmountOff(long amountCents) {
            // TODO: validate amountCents >= 0.
            this.amountCents = amountCents;
        }

        @Override
        public long discountCents(Cart cart) {
            // TODO: return amountCents (Checkout will cap at subtotal).
            throw new UnsupportedOperationException("TODO");
        }
    }

    // ---------------------------------------------------------------------
    // 4. Buy N get one free for a specific item.
    //    For each item with name == itemName: free units = quantity / (n + 1).
    //    The discount is freeUnits * unitPriceCents.
    // ---------------------------------------------------------------------
    public static final class BuyNGetOneFree implements DiscountStrategy {
        private final String itemName;
        private final int n;

        public BuyNGetOneFree(String itemName, int n) {
            // TODO: validate itemName not blank, n >= 1.
            this.itemName = itemName;
            this.n = n;
        }

        @Override
        public long discountCents(Cart cart) {
            // TODO: walk cart.items(), match by name, compute free units, sum.
            //       Hint: stream + filter + mapToLong + sum reads cleanly here.
            throw new UnsupportedOperationException("TODO");
        }
    }
}
