package ai.betterme;

import java.util.List;

import static ai.betterme.Discounts.capAt;
import static ai.betterme.Discounts.fixedAmountOff;
import static ai.betterme.Discounts.minSubtotal;
import static ai.betterme.Discounts.none;
import static ai.betterme.Discounts.percentageOff;

/**
 * Demo runner. Prints expected vs. actual for the seven scenarios in the README.
 *
 * <p>Run after implementing the TODOs:
 * <pre>
 *   javac -d out src/main/java/ai/betterme/*.java
 *   java  -cp out ai.betterme.Main
 * </pre>
 */
public final class Main {

    public static void main(String[] args) {
        // Cart subtotal = $30.00 = 3000 cents (3 items at $10.00 each).
        Cart cart = new Cart(List.of(new LineItem("SKU-1", 1000, 3)));

        check("no discount -> $30.00",
                3000L,
                () -> new Checkout(none()).finalPriceCents(cart));

        check("10% off -> $27.00",
                2700L,
                () -> new Checkout(none().then(percentageOff(10))).finalPriceCents(cart));

        check("10% + $5 off -> $22.00",
                2200L,
                () -> new Checkout(
                        none().then(percentageOff(10)).then(fixedAmountOff(500))
                ).finalPriceCents(cart));

        check("10% + $5 off, capped at $7 -> $23.00",
                2300L,
                () -> new Checkout(
                        none()
                                .then(percentageOff(10))
                                .then(fixedAmountOff(500))
                                .then(capAt(700))
                ).finalPriceCents(cart));

        check("$50 off on $30 cart -> $0.00 (Checkout caps at subtotal)",
                0L,
                () -> new Checkout(none().then(fixedAmountOff(5000))).finalPriceCents(cart));

        check("10% off gated by minSubtotal($100) -> $30.00 (stack disabled)",
                3000L,
                () -> new Checkout(
                        none().then(percentageOff(10)).then(minSubtotal(10000))
                ).finalPriceCents(cart));

        checkThrows("percentageOff(-5) -> IllegalArgumentException",
                IllegalArgumentException.class,
                () -> new Checkout(none().then(percentageOff(-5))).finalPriceCents(cart));

        // Open/Closed self-check: a lambda strategy returning > subtotal must
        // not produce a negative final price. The hardened Checkout caps it.
        check("lambda strategy returns > subtotal -> $0.00, not negative",
                0L,
                () -> new Checkout((DiscountStrategy) c -> 99_999L).finalPriceCents(cart));

        // Open/Closed self-check: a lambda strategy returning a negative
        // discount must be rejected with IllegalStateException by Checkout.
        checkThrows("lambda strategy returns negative -> IllegalStateException",
                IllegalStateException.class,
                () -> new Checkout((DiscountStrategy) c -> -1L).finalPriceCents(cart));
    }

    // ---------------------------------------------------------------------
    // Tiny harness — same shape as Day 7, with try/catch so unimplemented
    // TODOs report as TODO instead of crashing the whole run.
    // ---------------------------------------------------------------------

    @FunctionalInterface
    private interface Body {
        Object run();
    }

    private static void check(String label, Object expected, Body body) {
        try {
            Object actual = body.run();
            String status = expected.equals(actual) ? "PASS" : "FAIL";
            System.out.printf("%-65s %s%n", label, status);
            if (!expected.equals(actual)) {
                System.out.printf("    expected: %s%n", expected);
                System.out.printf("    actual:   %s%n", actual);
            }
        } catch (UnsupportedOperationException e) {
            System.out.printf("%-65s TODO (%s)%n", label, e.getMessage());
        } catch (RuntimeException e) {
            System.out.printf("%-65s ERROR (%s: %s)%n",
                    label, e.getClass().getSimpleName(), e.getMessage());
        }
    }

    private static void checkThrows(String label, Class<? extends RuntimeException> expected, Body body) {
        try {
            Object actual = body.run();
            System.out.printf("%-65s FAIL (no exception, returned %s)%n", label, actual);
        } catch (UnsupportedOperationException e) {
            // Only treat UOE as "TODO" when it is NOT the exception we expected.
            if (expected.isAssignableFrom(UnsupportedOperationException.class)) {
                System.out.printf("%-65s PASS (threw UnsupportedOperationException)%n", label);
            } else {
                System.out.printf("%-65s TODO (%s)%n", label, e.getMessage());
            }
        } catch (RuntimeException e) {
            String status = expected.isInstance(e) ? "PASS" : "FAIL";
            System.out.printf("%-65s %s (threw %s)%n", label, status, e.getClass().getSimpleName());
        }
    }
}
