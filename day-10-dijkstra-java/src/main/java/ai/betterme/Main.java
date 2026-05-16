package ai.betterme;

import java.util.List;
import java.util.Map;

/**
 * A small demo so you can eyeball your implementation on a realistic graph.
 *
 * <p>The real correctness check lives in {@code DijkstraTest} — see the README.
 * This is just a "does it look right" runner.
 */
public final class Main {

    private Main() { }

    public static void main(String[] args) {
        // A tiny road network. Weights are travel minutes.
        WeightedGraph city = new WeightedGraph(Map.of(
                "Home",    List.of(new Edge("Park", 4), new Edge("Cafe", 2)),
                "Cafe",    List.of(new Edge("Park", 1), new Edge("Office", 7)),
                "Park",    List.of(new Edge("Office", 3)),
                "Office",  List.of(new Edge("Gym", 2)),
                "Gym",     List.of()));

        System.out.println("Home -> Office distance: "
                + city.shortestDistance("Home", "Office")
                + "  (expected 6: Home->Cafe->Park->Office = 2+1+3)");
        System.out.println("Home -> Office path:     "
                + city.shortestPath("Home", "Office"));
        System.out.println("Home -> Gym distance:    "
                + city.shortestDistance("Home", "Gym"));
        System.out.println("Gym  -> Home distance:   "
                + city.shortestDistance("Gym", "Home")
                + "  (expected -1: Gym is a dead end)");
    }
}
