package ai.betterme;

import java.util.List;
import java.util.Map;

/**
 * Demo runner. Prints expected vs. actual for the five required scenarios
 * plus validation checks.
 *
 * <p>Run after implementing the TODOs in {@link Graph}:
 * <pre>
 *   javac -d out src/main/java/ai/betterme/*.java
 *   java  -cp out ai.betterme.Main
 * </pre>
 */
public final class Main {

    public static void main(String[] args) {
        // -----------------------------------------------------------------
        // Spec example graph:   A -> {B,C}, B -> {D}, C -> {D}, D -> {}
        // -----------------------------------------------------------------
        Graph diamond = new Graph(Map.of(
                "A", List.of("B", "C"),
                "B", List.of("D"),
                "C", List.of("D"),
                "D", List.of()));

        check("diamond bfs(A) -> [A, B, C, D]",
                List.of("A", "B", "C", "D"),
                () -> diamond.bfs("A"));

        check("diamond dfs(A) -> [A, C, D, B]   (stack LIFO reverses neighbor order)",
                List.of("A", "C", "D", "B"),
                () -> diamond.dfs("A"));

        check("diamond shortestPathLength(A, D) -> 2",
                2,
                () -> diamond.shortestPathLength("A", "D"));

        check("diamond shortestPathLength(D, A) -> -1 (one-way edges)",
                -1,
                () -> diamond.shortestPathLength("D", "A"));

        check("diamond shortestPathLength(A, A) -> 0",
                0,
                () -> diamond.shortestPathLength("A", "A"));

        // -----------------------------------------------------------------
        // Scenario 1: linear chain   A -> B -> C -> D
        // -----------------------------------------------------------------
        Graph chain = new Graph(Map.of(
                "A", List.of("B"),
                "B", List.of("C"),
                "C", List.of("D"),
                "D", List.of()));

        check("chain bfs(A) -> [A, B, C, D]",
                List.of("A", "B", "C", "D"),
                () -> chain.bfs("A"));

        check("chain dfs(A) -> [A, B, C, D]",
                List.of("A", "B", "C", "D"),
                () -> chain.dfs("A"));

        check("chain shortestPathLength(A, D) -> 3",
                3,
                () -> chain.shortestPathLength("A", "D"));

        // -----------------------------------------------------------------
        // Scenario 2: branching tree
        //   A -> {B, C}; B -> {D, E}; C -> {F}
        // -----------------------------------------------------------------
        Graph tree = new Graph(Map.of(
                "A", List.of("B", "C"),
                "B", List.of("D", "E"),
                "C", List.of("F"),
                "D", List.of(),
                "E", List.of(),
                "F", List.of()));

        check("tree bfs(A) -> [A, B, C, D, E, F]   (level by level)",
                List.of("A", "B", "C", "D", "E", "F"),
                () -> tree.bfs("A"));

        check("tree dfs(A) -> [A, C, F, B, E, D]   (depth first, LIFO neighbors)",
                List.of("A", "C", "F", "B", "E", "D"),
                () -> tree.dfs("A"));

        check("tree shortestPathLength(A, F) -> 2",
                2,
                () -> tree.shortestPathLength("A", "F"));

        // -----------------------------------------------------------------
        // Scenario 3: cycle   A -> B -> C -> A   (must TERMINATE)
        // -----------------------------------------------------------------
        Graph cycle = new Graph(Map.of(
                "A", List.of("B"),
                "B", List.of("C"),
                "C", List.of("A")));

        check("cycle bfs(A) -> [A, B, C]   (terminates despite back-edge)",
                List.of("A", "B", "C"),
                () -> cycle.bfs("A"));

        check("cycle dfs(A) -> [A, B, C]   (terminates despite back-edge)",
                List.of("A", "B", "C"),
                () -> cycle.dfs("A"));

        // -----------------------------------------------------------------
        // Scenario 4: disconnected   A -> B, isolated C
        // -----------------------------------------------------------------
        Graph disconnected = new Graph(Map.of(
                "A", List.of("B"),
                "B", List.of(),
                "C", List.of()));

        check("disconnected bfs(A) -> [A, B]   (does NOT visit C)",
                List.of("A", "B"),
                () -> disconnected.bfs("A"));

        check("disconnected dfs(C) -> [C]   (isolated component)",
                List.of("C"),
                () -> disconnected.dfs("C"));

        // -----------------------------------------------------------------
        // Scenario 5: unreachable target
        // -----------------------------------------------------------------
        check("disconnected shortestPathLength(A, C) -> -1   (unreachable)",
                -1,
                () -> disconnected.shortestPathLength("A", "C"));

        // -----------------------------------------------------------------
        // Validation: IllegalArgumentException on unknown nodes / null map
        // -----------------------------------------------------------------
        checkThrows("new Graph(null) -> IllegalArgumentException",
                IllegalArgumentException.class,
                () -> new Graph(null));

        checkThrows("diamond.bfs(\"Z\") -> IllegalArgumentException (unknown node)",
                IllegalArgumentException.class,
                () -> diamond.bfs("Z"));

        checkThrows("diamond.shortestPathLength(\"A\", \"Z\") -> IllegalArgumentException",
                IllegalArgumentException.class,
                () -> diamond.shortestPathLength("A", "Z"));
    }

    // ---------------------------------------------------------------------
    // Tiny harness — same shape as Day 7 / Day 8.
    // ---------------------------------------------------------------------

    @FunctionalInterface
    private interface Body {
        Object run();
    }

    private static void check(String label, Object expected, Body body) {
        try {
            Object actual = body.run();
            String status = expected.equals(actual) ? "PASS" : "FAIL";
            System.out.printf("%-75s %s%n", label, status);
            if (!expected.equals(actual)) {
                System.out.printf("    expected: %s%n", expected);
                System.out.printf("    actual:   %s%n", actual);
            }
        } catch (RuntimeException e) {
            System.out.printf("%-75s ERROR (%s: %s)%n",
                    label, e.getClass().getSimpleName(), e.getMessage());
        }
    }

    private static void checkThrows(String label, Class<? extends RuntimeException> expected, Body body) {
        try {
            Object actual = body.run();
            System.out.printf("%-75s FAIL (no exception, returned %s)%n", label, actual);
        } catch (RuntimeException e) {
            String status = expected.isInstance(e) ? "PASS" : "FAIL";
            System.out.printf("%-75s %s (threw %s)%n", label, status, e.getClass().getSimpleName());
        }
    }
}
