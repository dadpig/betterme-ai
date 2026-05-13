package ai.betterme;

import java.util.List;

/**
 * Demo runner. Prints expected vs. actual for the six scenarios in the README.
 *
 * <p>Run after implementing the TODOs:
 * <pre>
 *   javac -d out src/main/java/ai/betterme/*.java
 *   java  -cp out ai.betterme.Main
 * </pre>
 */
public final class Main {

    public static void main(String[] args) {
        check("top-2 with tie",
                List.of(new WordCount("the", 2), new WordCount("brown", 1)),
                () -> WordFrequency.topN("the quick brown fox the lazy dog", 2));

        check("case-insensitive",
                List.of(new WordCount("hello", 3)),
                () -> WordFrequency.topN("Hello, hello! HELLO?", 1));

        check("empty text",
                List.of(),
                () -> WordFrequency.topN("", 5));

        check("n = 0",
                List.of(),
                () -> WordFrequency.topN("a b c", 0));

        check("n > distinct",
                List.of(
                        new WordCount("a", 1),
                        new WordCount("b", 1),
                        new WordCount("c", 1)),
                () -> WordFrequency.topN("a b c", 9));

        checkThrows("negative n -> IllegalArgumentException",
                IllegalArgumentException.class,
                () -> WordFrequency.topN("anything", -1));

        // Bonus self-check: the returned list must be unmodifiable.
        checkThrows("returned list is unmodifiable",
                UnsupportedOperationException.class,
                () -> WordFrequency.topN("a b c", 3).add(new WordCount("x", 1)));
    }

    // ---------------------------------------------------------------------
    // Tiny harness — same shape as Day 6, with try/catch so unimplemented
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
            System.out.printf("%-45s %s%n", label, status);
            if (!expected.equals(actual)) {
                System.out.printf("    expected: %s%n", expected);
                System.out.printf("    actual:   %s%n", actual);
            }
        } catch (UnsupportedOperationException e) {
            System.out.printf("%-45s TODO (%s)%n", label, e.getMessage());
        } catch (RuntimeException e) {
            System.out.printf("%-45s ERROR (%s: %s)%n",
                    label, e.getClass().getSimpleName(), e.getMessage());
        }
    }

    private static void checkThrows(String label, Class<? extends RuntimeException> expected, Body body) {
        try {
            Object actual = body.run();
            System.out.printf("%-45s FAIL (no exception, returned %s)%n", label, actual);
        } catch (RuntimeException e) {
            String status = expected.isInstance(e) ? "PASS" : "FAIL";
            System.out.printf("%-45s %s (threw %s)%n", label, status, e.getClass().getSimpleName());
        }
    }
}
