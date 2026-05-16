package ai.betterme;

import static ai.betterme.TestRunner.assertEquals;
import static ai.betterme.TestRunner.assertThrows;
import static ai.betterme.TestRunner.assertTrue;

import java.util.List;
import java.util.Map;

/**
 * The acceptance spec for Day 10. Every {@code testXxx} method below is one
 * scenario your {@link WeightedGraph} implementation must satisfy.
 *
 * <p>You do not normally need to edit this file — treat the tests as the
 * fixed contract and make {@code WeightedGraph} pass them. (Adding your own
 * extra {@code testXxx} methods to probe edge cases is encouraged.)
 *
 * <p>Run with: see README "Build and run".
 */
public class DijkstraTest {

    /** A small reusable graph builder. */
    private static Edge e(String to, long w) {
        return new Edge(to, w);
    }

    /*
     * Diamond graph used by several tests:
     *
     *        (1)        (5)
     *     A ------> B ------> D
     *      \                 ^
     *   (4) \      (1)       /
     *        +---> C -------+
     *
     * A->B->D = 1 + 5 = 6 ;  A->C->D = 4 + 1 = 5  (so A->D shortest = 5 via C)
     */
    private static WeightedGraph diamond() {
        return new WeightedGraph(Map.of(
                "A", List.of(e("B", 1), e("C", 4)),
                "B", List.of(e("D", 5)),
                "C", List.of(e("D", 1)),
                "D", List.of()));
    }

    // ---- shortestDistance --------------------------------------------------

    public void testDistanceSameNodeIsZero() {
        assertEquals(0L, diamond().shortestDistance("A", "A"));
    }

    public void testDistanceSingleEdge() {
        assertEquals(1L, diamond().shortestDistance("A", "B"));
    }

    public void testDistancePicksCheaperOfTwoPaths() {
        // A->B->D costs 6, A->C->D costs 5 — Dijkstra must pick 5.
        assertEquals(5L, diamond().shortestDistance("A", "D"));
    }

    public void testDistanceUnreachableReturnsMinusOne() {
        // D has no outgoing edges, so nothing is reachable from D except itself.
        assertEquals(-1L, diamond().shortestDistance("D", "A"));
    }

    public void testDistanceUnknownSourceThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> diamond().shortestDistance("Z", "A"));
    }

    public void testDistanceUnknownTargetThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> diamond().shortestDistance("A", "Z"));
    }

    public void testDistanceLongerHopCountCanWin() {
        // Direct A->C weight 10, but A->B->C weight 1+1=2. Dijkstra must not
        // be fooled by edge count — it minimizes total weight.
        WeightedGraph g = new WeightedGraph(Map.of(
                "A", List.of(e("B", 1), e("C", 10)),
                "B", List.of(e("C", 1)),
                "C", List.of()));
        assertEquals(2L, g.shortestDistance("A", "C"));
    }

    public void testDistanceHandlesCycleWithoutLooping() {
        // A<->B cycle plus A->C. Must terminate and return 7 for A->C.
        WeightedGraph g = new WeightedGraph(Map.of(
                "A", List.of(e("B", 2), e("C", 7)),
                "B", List.of(e("A", 2)),
                "C", List.of()));
        assertEquals(7L, g.shortestDistance("A", "C"));
    }

    // ---- shortestPath ------------------------------------------------------

    public void testPathSameNodeIsSingleton() {
        assertEquals(List.of("A"), diamond().shortestPath("A", "A"));
    }

    public void testPathReconstructsCheapestRoute() {
        // Cheapest A->D is via C, so the path is [A, C, D] (not [A, B, D]).
        assertEquals(List.of("A", "C", "D"), diamond().shortestPath("A", "D"));
    }

    public void testPathUnreachableIsEmptyList() {
        assertEquals(List.of(), diamond().shortestPath("D", "A"));
    }

    public void testPathUnknownNodeThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> diamond().shortestPath("A", "Z"));
    }

    public void testPathIsUnmodifiable() {
        List<String> path = diamond().shortestPath("A", "D");
        assertThrows(UnsupportedOperationException.class,
                () -> path.add("X"));
    }

    // ---- Edge construction (given to you, but exercised here) --------------

    public void testNegativeEdgeWeightRejected() {
        // Dijkstra's correctness requires non-negative weights — Edge enforces it.
        assertThrows(IllegalArgumentException.class,
                () -> new Edge("B", -1));
    }

    public void testZeroWeightEdgeIsAllowed() {
        // Zero is a valid edge weight (a free hop) — must NOT be rejected.
        Edge zero = new Edge("B", 0);
        assertTrue(zero.weight() == 0, "zero-weight edge should be allowed");
    }
}
