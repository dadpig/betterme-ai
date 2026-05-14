package ai.betterme;

/**
 * Caps the wrapped discount at {@code maxCents}.
 *
 * <p>Crucially, this decorator <b>does not add</b> to the wrapped value —
 * it <b>replaces</b> it with the minimum of the wrapped value and the cap.
 * That asymmetry is what makes order matter:
 * {@code capAt(7).then(fixedAmountOff(500))} is different from
 * {@code fixedAmountOff(500).then(capAt(7))}.
 */
public final class CapDecorator extends DiscountDecorator {

    private final long maxCents;

    public CapDecorator(DiscountStrategy wrapped, long maxCents) {
        super(wrapped);
        if(maxCents <= 0){
            throw new IllegalArgumentException("maxcents: should be greater than zero.");
        }

        this.maxCents = maxCents;
    }

    @Override
    public long discountCents(Cart cart) {
         return Math.min(wrapped.discountCents(cart), maxCents);
    }
}
