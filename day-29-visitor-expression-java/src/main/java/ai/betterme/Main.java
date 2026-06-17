package ai.betterme;

import static ai.betterme.Expressions.add;
import static ai.betterme.Expressions.div;
import static ai.betterme.Expressions.mul;
import static ai.betterme.Expressions.neg;
import static ai.betterme.Expressions.num;
import static ai.betterme.Expressions.sub;

/**
 * Demo runner. Builds a few trees and prints both operations over each one.
 *
 * <p>Expected output once the visitors are implemented:
 * <pre>
 * (2 + 3)            = 5.0
 * ((2 + 3) * 4)      = 20.0
 * (10 / (2 + 3))     = 2.0
 * (-(6 - 8))         = 2.0
 * (3 + (4 * 2))      = 11.0
 * </pre>
 */
public final class Main {

    private Main() {
    }

    public static void main(String[] args) {
        Expr[] trees = {
                add(num(2), num(3)),
                mul(add(num(2), num(3)), num(4)),
                div(num(10), add(num(2), num(3))),
                neg(sub(num(6), num(8))),
                add(num(3), mul(num(4), num(2))),
        };

        for (Expr tree : trees) {
            String text = Expressions.print(tree);
            double value = Expressions.evaluate(tree);
            System.out.printf("%-18s = %s%n", text, value);
        }

        // Division by zero surfaces as ArithmeticException at evaluation time.
        try {
            Expressions.evaluate(div(num(1), num(0)));
            System.out.println("BUG: expected ArithmeticException");
        } catch (ArithmeticException e) {
            System.out.println("1 / 0 correctly threw ArithmeticException: " + e.getMessage());
        }
    }
}
