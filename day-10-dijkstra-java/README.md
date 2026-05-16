# Day 10 — Dijkstra's Shortest Path (Java)

Java track, day 6. Algorithms axis.

Yesterday (Day 9) you traversed an **unweighted** graph: BFS gave you the
shortest path *by edge count*. Today you put **weights** on the edges, and
"shortest" becomes "smallest total weight". A plain queue no longer works —
you need a **priority queue** so you always expand the cheapest-so-far node
next. That is Dijkstra's algorithm.

Single-folder, no Maven/Gradle, no downloaded jars. Plain `javac` + `java`.

## Learning outcome

After this challenge you should be able to:

- Explain *why* BFS solves unweighted shortest path but fails on weighted
  graphs, and why a `PriorityQueue` fixes it.
- Use `java.util.PriorityQueue` with `Comparator.comparingLong(...)` as the
  idiomatic min-heap — no hand-written branchy comparator.
- Implement the **"stale entry" / lazy-deletion** trick: instead of a
  decrease-key operation, just push duplicates and skip an entry when its
  popped distance is worse than the best you have recorded.
- Reconstruct an actual path from a `previous` map (backtrack + reverse) —
  the same technique scales to A\*, Bellman-Ford, BFS-with-parents.
- Keep the Day 9 win: an immutable `record` graph with a deep defensive
  copy, and `IllegalArgumentException` for bad arguments.

## Why Dijkstra needs non-negative weights

Dijkstra's correctness rests on one invariant: **once you pop a node from the
priority queue, its distance is final** ("settled"). That holds only when
every edge weight is `>= 0` — a later, longer route can never undercut a
settled node. With a negative edge it could, and the algorithm breaks.

That is why `Edge`'s compact constructor (given to you) **rejects negative
weights**. Zero is fine — a zero-weight edge is just a free hop.

## Your task

Open `src/main/java/ai/betterme/WeightedGraph.java`. Two methods are stubbed
with `throw new UnsupportedOperationException(...)`. Replace each stub body:

```java
public long       shortestDistance(String from, String to)
public List<String> shortestPath(String from, String to)
```

The Javadoc on each method spells out the full contract and a step-by-step
algorithm sketch. `Edge`, the `WeightedGraph` constructor, and a private
`requireNode` helper are given to you complete.

> Note on exceptions: the stubs throw `UnsupportedOperationException`, and
> that is *correct* — "this operation does not exist yet" genuinely is what
> UOE means. That is a different situation from a caller passing a bad
> argument, which is always `IllegalArgumentException`. Keep that line sharp
> (Days 5b and 6 blurred it; Days 7–9 fixed it — don't regress today).

### Contract summary

| Method                       | from == to    | unreachable | unknown node            |
|-------------------------------|---------------|-------------|-------------------------|
| `shortestDistance(from, to)`  | `0`           | `-1`        | `IllegalArgumentException` |
| `shortestPath(from, to)`      | `[from]`      | `[]`        | `IllegalArgumentException` |

`shortestPath` must return an **unmodifiable** list (`List.copyOf(...)`).

Target complexity: **O((V + E) log V)**.

## Build and run

Compile everything (main + tests):

```bash
cd /Users/tairone/personal-pocs/betterme-ai/day-10-dijkstra-java
javac -d out src/main/java/ai/betterme/*.java src/test/java/ai/betterme/*.java
```

Run the **tests** — this is your real correctness check:

```bash
java -cp out ai.betterme.TestRunner
```

Run the demo:

```bash
java -cp out ai.betterme.Main
```

Before you write any code, run the tests once. You should see
**2 passed, 13 failed** — the 2 passing tests cover the given `Edge`
constructor; the 13 failures are your stubs. Your job is `15 passed`.

## About the test harness

`TestRunner` is a tiny, dependency-free harness: it reflects over
`DijkstraTest`, runs every `public void testXxx()` method, and prints
PASS/FAIL. It is here so the challenge ships with **real, named, one-scenario
tests** without forcing a build tool on you.

`DijkstraTest` has 15 tests. Treat it as the fixed spec — make
`WeightedGraph` pass it. Adding your own extra `testXxx` methods to probe
edge cases is encouraged (the runner will pick them up automatically).

Each test is one focused scenario with one assertion — that is the habit to
build. Compare it to the single hand-rolled `Main` PASS/FAIL runner you have
used since Day 5b: when one thing breaks, a named test tells you *which*
scenario, not just "something failed".

## Stretch (optional, pick one)

- **A. Real JUnit 5.** The assertion methods in `TestRunner`
  (`assertEquals`, `assertThrows`, `assertTrue`) are deliberately named to
  match JUnit 5's `org.junit.jupiter.api.Assertions`. To switch over: add
  `@Test` from `org.junit.jupiter.api` to each test method, change the
  imports, and run with the JUnit Platform. JUnit Jupiter 6.0.3 jars are
  already in your local Maven cache under
  `~/.m2/repository/org/junit/`. The cleanest CLI path is the
  `junit-platform-console-standalone` jar (one download) — then:
  `java -jar junit-platform-console-standalone.jar -cp out -c ai.betterme.DijkstraTest`.
  This is the JUnit on-ramp that has been on the shelf since Day 7.
- **B. Path *with* total cost.** Add
  `record Route(List<String> nodes, long totalCost)` and a
  `shortestRoute(from, to)` that returns both in one pass — no second
  traversal to re-sum the weights.
- **C. All-targets variant.** Add `Map<String,Long> distancesFrom(String)`
  that returns the shortest distance to *every* reachable node from one
  source — the natural "single-source shortest paths" shape. Notice you
  drop the early-exit and let the queue drain.

## Idioms to apply

- `PriorityQueue<Entry>` with
  `Comparator.comparingLong(Entry::dist)` — a 1-line min-heap. Define a
  tiny `private record Entry(String node, long dist) {}` inside
  `WeightedGraph`.
- `Map::getOrDefault` with `Long.MAX_VALUE` as the "infinity" sentinel,
  so you never special-case "not seen yet".
- `Collections.reverse(list)` then `List.copyOf(list)` for the path
  reconstruction + immutability step.
- Validate both nodes **first**, before allocating any structures.

## Anti-patterns to avoid

- Using a plain `ArrayDeque` (Day 9's queue) and hoping — that gives you
  BFS, which ignores weights. The priority queue *is* the algorithm.
- A `decrease-key` attempt on `PriorityQueue` — it has no efficient one.
  Push a fresh `Entry` and skip stale pops instead.
- Marking a node "done" before it is popped from the queue. A node is
  settled **only** when popped.
- Re-walking the graph a second time just to total up the path cost
  (that is what stretch B fixes).
- A branchy comparator lambda with `if`/`else` instead of
  `comparingLong`.

## Reflection (after you finish)

1. Day 9's `shortestPathLength` used a plain `ArrayDeque` and marked nodes
   visited *on enqueue*. Today you skip nodes *on pop* via the stale-entry
   check instead. Why can't you reuse the Day 9 "visited on enqueue"
   approach here? (Hint: think about a node reachable two ways, the second
   of which is cheaper.)
2. What concretely breaks if one edge has weight `-3`? Trace a 3-node
   example by hand and find the exact moment Dijkstra commits to a wrong
   answer.
3. You wrote the *same* Dijkstra loop twice — once for `shortestDistance`,
   once for `shortestPath`. Which paradigm tool would let you write the
   traversal once and get both results? (You will reach for exactly this
   in stretch B.)

Time budget: 60–90 minutes for the core (both methods + all 15 tests
green); +30 min for a stretch.
