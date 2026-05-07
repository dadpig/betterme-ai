package ai.betterme;

import java.util.List;

/**
 * Demo runner. Prints expected vs. actual for the four scenarios in the README.
 *
 * <p>Run after implementing the TODOs:
 * <pre>
 *   javac -d out src/main/java/ai/betterme/*.java
 *   java  -cp out ai.betterme.Main
 * </pre>
 */
public final class Main {

    public static void main(String[] args) {
        Cart cart = new Cart(List.of(
                new LineItem("apple", 100, 3),
                new LineItem("bread", 250, 2)
        ));

        // Subtotal = 100*3 + 250*2 = 800
        check("NoDiscount",                800, new Checkout(new Discounts.NoDiscount()), cart);
        check("PercentageOff(10)",         720, new Checkout(new Discounts.PercentageOff(10)), cart);
        check("FixedAmountOff(150)",       650, new Checkout(new Discounts.FixedAmountOff(150)), cart);
        check("FixedAmountOff(10000) cap",   0, new Checkout(new Discounts.FixedAmountOff(10_000)), cart);
        check("BuyNGetOneFree(apple, 2)",  700, new Checkout(new Discounts.BuyNGetOneFree("apple", 2)), cart);

        // Bonus: a lambda strategy. Useful sanity check that the @FunctionalInterface
        // contract holds — once you have the core implementations passing, this should
        // also print PASS without any new class.
        DiscountStrategy fivePercent = c -> c.subtotalCents() * 5 / 100;
        check("Lambda 5%",                 760, new Checkout(fivePercent), cart);
    }

    private static void check(String label, long expected, Checkout co, Cart cart) {
        try {
            long actual = co.finalPriceCents(cart);
            String status = (actual == expected) ? "PASS" : "FAIL";
            System.out.printf("%-35s expected=%4d actual=%4d  %s%n", label, expected, actual, status);
        } catch (UnsupportedOperationException e) {
            System.out.printf("%-35s expected=%4d  TODO (%s)%n", label, expected, e.getMessage());
        }
    }
}
