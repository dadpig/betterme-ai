---
name: Daily progress log
description: Per-day record of what was proposed, accepted, completed, and rated — used to calibrate future challenges
type: project
---

Append one entry per session. Most recent at the bottom. Update the **Status** field as the day progresses (`proposed` → `in-progress` → `completed` / `partial` / `skipped`).

## 2026-05-05 — Day 5

- **Topic:** Linear regression from scratch (batch gradient descent)
- **Language:** Java (21+, records / sealed types / streams)
- **Folder:** `/Users/tairone/personal-pocs/betterme-ai/day-05-linear-regression-java/`
- **Difficulty target:** intermediate Java, beginner ML — first Java POC of the routine
- **Acceptance:** recover `w≈3, b≈7` on synthetic `y=3x+7+ε` within tolerance `0.1`; multivariate-capable; uses Streams API non-trivially; immutable `record` types for `Dataset`/`Model`/`TrainingConfig`
- **Stretch:** (A) sealed `Optimizer` interface with BatchGD + SGD impls; (B) feature normalizer; (C) parallel gradient sum via `IntStream.parallel()`
- **Status:** completed (baseline)
- **Result:** Recovered `w=3.0009, b=6.9939` on synthetic `y=3x+7+ε` — well within tolerance. Compiles and runs.
- **Tuning:** Defaults landed at `lr=0.02`, `epochs=10_000`.
- **Stretches:** A/B/C untouched — leave on the shelf for a future revisit (good cross-language retrospective material).
- **Notes:** Day 1–4 of the originally-proposed Rust→Scala→Java arc do **not** have evidence of completion in the working directory as of this proposal — no `day-01-` through `day-04-` folders exist. Today's challenge therefore stands on its own (synthetic data generated in Java, no carryover from earlier days). If the user later confirms Days 1–4 happened, revisit and reconcile.

### 2026-05-05 — Day 5b (warm-down / second challenge)

- **Topic:** Roman numeral converter (int ↔ Roman, both directions)
- **Language:** Java (21+, single-file friendly)
- **Folder:** `/Users/tairone/personal-pocs/betterme-ai/day-05b-roman-numerals-java/`
- **Difficulty target:** easier — beginner-to-intermediate fundamentals; ~30–45 min budget
- **Acceptance:** correct on standard cases 1..3999; both `toRoman(int)` and `fromRoman(String)` implemented; subtractive forms handled.
- **Status:** completed
- **Result:** Both directions work on standard cases. `toRoman` was hand-coded with length-of-digits branches instead of walking the declared descending table — works but un-idiomatic. `fromRoman` cleanly does the two-char lookahead via the reversed map.
- **Calibration note:** Used `UnsupportedOperationException` for validation instead of `IllegalArgumentException` — recurring pattern (see also Day 6). Worth surfacing on a future input-validation challenge.

## 2026-05-06 — Day 6 (Java track day 2)

- **Topic:** GoF **Strategy** pattern — checkout discount engine
- **Language:** Java (21+, records, `@FunctionalInterface`, lambdas)
- **Folder:** `/Users/tairone/personal-pocs/betterme-ai/day-06-strategy-discount-java/`
- **Difficulty target:** easy — design-pattern entry tier
- **Acceptance:** four named strategies; `Checkout` clamps at zero; demo prints PASS for all six scenarios (incl. a lambda `5%`).
- **Status:** completed (core)
- **Result:** All four strategies + `Checkout` + `Cart.subtotalCents()` implemented. Streams used confidently in `BuyNGetOneFree`. Recent commit `e7eca2e fix sum of failing discounts` suggests at least one defect was caught and fixed.
- **Issues observed (not yet flagged to user):**
  - `Checkout.finalPriceCents` skips the negative-discount guard and the `Math.min(discount, subtotal)` cap — works because `FixedAmountOff` internally caps, but the Open/Closed contract is broken (a lambda strategy could return a value > subtotal and produce a negative price).
  - `PercentageOff(0)` and `FixedAmountOff(0)` throw — spec allowed `0`.
  - Validation throws `UnsupportedOperationException` instead of `IllegalArgumentException` (same pattern as Day 5b).
- **Stretches A/B/C:** untouched.

## 2026-05-12 — Day 7 (Java track day 3)

- **Topic:** Frequency counting + small-data sorting — "word-frequency CLI" using `Map`, `Collectors.groupingBy`, `Comparator`
- **Language:** Java (21+, records, streams, JUnit 5)
- **Folder:** `/Users/tairone/personal-pocs/betterme-ai/day-07-word-frequency-java/`
- **Difficulty target:** beginner — one core concept (data-driven streaming aggregation), ~45–60 min budget
- **Why this topic:** Rotates the axis back to **algorithms / collections fundamentals** after two consecutive design-pattern / kata days. Targets two observed idiomatic gaps from prior days: (a) preferring data-driven iteration over hand-coded branches (the Roman numerals lesson), and (b) introducing `IllegalArgumentException` for input validation. Adds a gentle on-ramp to **JUnit 5** without forcing a build tool — single `junit-platform-console-standalone` jar runs from CLI.
- **Acceptance:**
  - `WordFrequency.topN(String text, int n)` returns the `n` most frequent words, ties broken alphabetically.
  - Case-insensitive; words are `[a-zA-Z']+` runs; everything else is a separator.
  - Empty/blank input returns an empty list; `n < 0` throws `IllegalArgumentException`; `n == 0` returns empty.
  - `n` larger than the distinct-word count returns all words.
  - Implementation uses `Collectors.groupingBy` + `Collectors.counting` (no manual `Map.get/put` loops).
  - At least 5 JUnit 5 tests pass.
- **Stretch:** (A) immutable `record WordCount(String word, long count)` returned instead of `Map.Entry`; (B) read text from a file path passed as `args[0]` so it becomes a real CLI; (C) parallel stream variant and a one-line benchmark on a long text.
- **Status:** proposed

**Why this calibration:** Beginner-tier conceptually (just `groupingBy` + `sorted` + `limit`) but introduces three things they have not yet done in this routine: (1) `Collectors.groupingBy`, (2) chained `Comparator` with tie-breaking, (3) JUnit 5. No new build tooling — JUnit standalone jar keeps the IntelliJ/CLI workflow they're already on.

**How to apply on completion:** If they reach for a manual loop instead of `groupingBy`, surface the data-driven idiom directly. If `IllegalArgumentException` lands without prompting, great signal. If tests come out as one giant method instead of several `@Test` methods, coach toward one-assertion-per-test. Bump Day 8 either to **Decorator pattern** (next GoF, builds on Strategy) or to **a graph traversal kata** (rotate to algorithms harder tier) depending on which stretch they chose.

**Day 7 observed outcome (post-scaffold review):**
- `topN` shipped as a single stream pipeline using `groupingBy(identity(), counting())` + chained `Comparator.comparingLong(...).reversed().thenComparing(...)`. The data-driven idiom landed cleanly.
- `IllegalArgumentException` was used correctly for `n < 0`. The Day 5b/6 UOE-for-validation habit is broken.
- JUnit 5 was **not** wired up — the user kept the hand-rolled `Main` PASS/FAIL harness. Stretch A (JUnit) carries over as the JUnit 5 on-ramp moves to Day 8 stretch.
- Style nits remaining: stray `else` after a throwing `if` (early-return style is cleaner), inconsistent import indentation. Mention only if they come up — not blocking.

## 2026-05-13 — Day 8 (Java track day 4)

- **Topic:** GoF **Decorator** pattern — stackable discounts (composes on top of Day 6's Strategy domain)
- **Language:** Java (21+, records, `@FunctionalInterface`, `UnaryOperator`, default methods)
- **Folder:** `/Users/tairone/personal-pocs/betterme-ai/day-08-decorator-discount-java/`
- **Difficulty target:** intermediate — design-pattern second-tier; ~60–90 min budget for core
- **Why this topic:**
  - Rotation: returns to design patterns after Day 7's algorithms/streams. Decorator is the natural next GoF after Strategy.
  - Builds on familiar domain (Cart + DiscountStrategy from Day 6) so the *new* concept (recursive wrapping) lands without domain noise.
  - Forces the user to confront the two latent bugs in Day 6's `Checkout` that the log flagged but Day 6 never closed: missing negative-discount guard and missing `Math.min(discount, subtotal)` cap. Open/Closed payoff becomes concrete.
  - Reinforces the Day 7 win: `IllegalArgumentException` for validation, `IllegalStateException` only for "the strategy itself misbehaved" (legitimate ISE territory).
  - Introduces `default` interface methods and `UnaryOperator<T>` as a decorator-factory idiom — same shape as `Function.andThen` / `Comparator.thenComparing`, which is high-leverage Java vocabulary.
- **Acceptance:**
  - `DiscountDecorator` (abstract) holds a `protected final DiscountStrategy wrapped`, rejects null in constructor.
  - Four concrete decorators: `PercentageOffDecorator`, `FixedAmountOffDecorator`, `CapDecorator`, `MinSubtotalDecorator`. Each `discountCents` is a single expression delegating to `wrapped`.
  - `NoDiscount` (Null Object) is the base of every stack.
  - `Discounts` factory exposes `none()`, `percentageOff(int)`, `fixedAmountOff(long)`, `capAt(long)`, `minSubtotal(long)`. The last four return `UnaryOperator<DiscountStrategy>`.
  - `DiscountStrategy.then(UnaryOperator<DiscountStrategy>)` is a default method that rejects null and returns `decorator.apply(this)`.
  - `Checkout.finalPriceCents` is hardened: rejects negative discounts with `IllegalStateException`, caps at subtotal, returns non-negative.
  - All input validation throws `IllegalArgumentException` with a message. Zero is a valid no-op for `percentageOff(0)` / `fixedAmountOff(0)`.
  - `Main` runs nine scenarios (seven from README + two Open/Closed self-checks: lambda returning > subtotal, lambda returning negative).
- **Stretch:** (A) JUnit 5 via `junit-platform-console-standalone` jar — the on-ramp that didn't happen on Day 7; (B) port Day 6's `BuyNGetOneFree` as a decorator; (C) sealed permits across `DiscountStrategy` and `DiscountDecorator` for compile-time exhaustiveness on `switch`.
- **Status:** completed (core)
- **Result (post-commit `b1d34b0`):** Eight decorator files shipped under `ai.betterme`. `Checkout` is hardened — negative-discount guard throws `IllegalStateException`, cap-at-subtotal via `Math.min`, never returns a negative price. `CapDecorator` correctly *replaces* with `Math.min(wrapped, maxCents)` (not adds) — the classic Decorator misread is avoided. `IllegalArgumentException` used consistently for validation. The Day 5b/6 UOE habit is now firmly broken across two consecutive challenges.
- **Calibration note:** `CapDecorator` rejects `maxCents <= 0`, but `0` is arguably a valid "cap to nothing" — same off-by-one as Day 6's `PercentageOff(0)`. Minor; surface only if a future spec explicitly allows `0`.
- **Stretches A/B/C:** untouched. JUnit 5 on-ramp still pending — keep on the shelf.

## 2026-05-14 — Day 9 (Java track day 5)

- **Topic:** Graph traversal fundamentals — **BFS + DFS on an adjacency-list graph**, with a shortest-unweighted-path application
- **Language:** Java (21+, records, `Map`, `Set`, `Deque`/`ArrayDeque`, `List`)
- **Folder:** `/Users/tairone/personal-pocs/betterme-ai/day-09-graph-traversal-java/`
- **Difficulty target:** intermediate — first algorithms-tier challenge that requires both a data structure choice (queue vs stack) and a careful invariant (visited-set placement); ~60–90 min budget
- **Why this topic:**
  - Rotation: two consecutive design-pattern days (Strategy → Decorator). Time to swing back to algorithms.
  - First **graph** problem in the routine. The user has done arrays (word-freq), tables (Roman numerals), and numeric math (linreg) — adjacency lists and traversal are the natural next data-structure rung.
  - Targets one carried-over idiomatic gap from Day 7: prefer *data-driven* iteration over hand-coded branches. BFS and DFS are textbook examples of "the queue/stack *is* the algorithm."
  - Introduces `ArrayDeque` as the idiomatic Java queue (not `LinkedList`, not `Stack`) — a common Java-vocabulary gap for advanced beginners.
  - Lets the same data structure (an immutable `Graph` record holding `Map<String, List<String>>`) drive *three* methods: `bfs`, `dfs`, `shortestPathLength`. Reinforces "let the data shape the algorithm."
- **Acceptance:**
  - `record Graph(Map<String, List<String>> adjacency)` with a compact constructor that (a) rejects null, (b) defensively copies into an unmodifiable map of unmodifiable lists.
  - `Graph.bfs(String start)` returns `List<String>` of nodes in BFS visit order. Throws `IllegalArgumentException` if `start` is not a node.
  - `Graph.dfs(String start)` returns `List<String>` in DFS visit order — **iterative** with an explicit `ArrayDeque` stack (no recursion). Same validation.
  - `Graph.shortestPathLength(String from, String to)` returns the edge-count of the shortest unweighted path, or `-1` if unreachable. `from == to` returns `0`. Uses BFS with a `Map<String,Integer>` distance map (not parent reconstruction — keep it tight).
  - Visited tracking: `Set<String>` marked **when enqueued/pushed**, never on dequeue (the classic correctness bug).
  - Handles disconnected graphs (don't traverse beyond the component of `start`).
  - Deterministic neighbor iteration — preserve the order given in the adjacency list, do not sort.
  - `Main` runs at least five scenarios printing PASS/FAIL: linear chain, branching tree, cycle, disconnected pair, unreachable target.
- **Stretch:** (A) **JUnit 5** via `junit-platform-console-standalone` — the on-ramp that has been deferred twice now; (B) `record Path(List<String> nodes)` returned by a new `shortestPath(from, to)` that reconstructs the actual path via a parent-map; (C) topological sort method `topoSort()` that throws on a cycle, useful for the next pattern day (Command / dependency graphs).
- **Status:** proposed

**Why this calibration:** First algorithms-axis challenge that genuinely demands a correct invariant (visited-on-enqueue), not just stream-pipeline fluency. The data structure (adjacency map) is shaped exactly like the discount-strategy domain the user just mastered — immutable record + defensive copy — so the *new* load is purely the traversal logic. Iterative DFS with `ArrayDeque` is deliberately chosen over recursion to (a) build the muscle for the queue/stack symmetry and (b) sidestep stack-overflow concerns on bigger graphs in the stretch goals. 60–90 min lands the core; JUnit stretch adds ~30 min.

**How to apply on completion:**
- If they mark visited on dequeue (not enqueue), BFS will still terminate but `shortestPathLength` will explode in worst-case time and may double-count nodes. The "cycle" scenario in `Main` should expose it.
- If they reach for `LinkedList`, `Stack`, or recursion, surface `ArrayDeque` as the idiomatic answer — `Stack` is legacy synchronized, `LinkedList` is a fat doubly-linked queue.
- If `Graph`'s compact constructor doesn't defensively copy (they pass the map reference straight through), mention it — they got this right on Day 5's `Dataset`, so it's a regression worth flagging.
- Day 10 candidates: (i) **Observer** pattern (next GoF, no concurrency required) on the discount domain or a new pub-sub mini-app; (ii) **Dijkstra** on a `Map<String,List<Edge>>` weighted graph — natural progression from BFS; (iii) **language rotation** — port Day 7's word-freq or Day 8's decorator chain to Scala or Rust to finally start the cross-language contrast arc.

**Day 9 confirmed completed** — commit `2cbdecb "bfs as deque dfs as stack"` ships `Graph.java` (BFS, DFS, `shortestPathLength`) + `Main.java` under `ai.betterme`. Visited-set marked on enqueue/push (`visited.add()` as the gate) — the classic correctness invariant landed correctly. `ArrayDeque` used for both queue and stack as specified. Compact constructor takes the deep defensive copy. No `Main` was reviewed for scenario count but the structure is sound. Stretches A/B/C (JUnit, path reconstruction, topo sort) untouched.

## 2026-05-15 — Day 10 (Java track day 6)

- **Topic:** **Dijkstra's shortest path** on a directed weighted graph — `shortestDistance` + `shortestPath` (with reconstruction)
- **Language:** Java (25, records, `PriorityQueue`, `Comparator.comparingLong`, `Map.getOrDefault`)
- **Folder:** `/Users/tairone/personal-pocs/betterme-ai/day-10-dijkstra-java/`
- **Difficulty target:** intermediate — algorithms axis; ~60–90 min for core. Direct progression from Day 9 BFS.
- **Why this topic:** Chosen from Day 9's candidate (ii). BFS (unweighted shortest path) → Dijkstra (weighted shortest path) is the natural next rung; reuses the exact immutable-`record`-adjacency-map shape the user just mastered on Day 9, so the *new* load is purely (a) `PriorityQueue` as idiomatic min-heap, (b) the stale-entry / lazy-deletion trick instead of decrease-key, (c) `previous`-map path reconstruction. Also picked because Dijkstra's outputs are deterministic and trivially assertable — ideal for the user's explicit "include tests" request.
- **Tests:** The user explicitly asked for tests. Shipped a real test suite: `DijkstraTest` (15 named one-scenario `testXxx` methods) + `TestRunner` (zero-dependency reflective harness with JUnit-5-compatible assertion names: `assertEquals`/`assertThrows`/`assertTrue`). Plain `javac`/`java`, no build tool. Reason for not using real JUnit: no `junit-platform-console-standalone` jar in `~/.m2` and no network in the scaffold environment — Jupiter 6.0.3 api/engine jars ARE cached but the launcher is missing. Real JUnit 5 swap is documented as stretch A (the on-ramp deferred since Day 7 — note the harness assertion names match Jupiter exactly to make the swap nearly mechanical).
- **Scaffold state:** `Edge` (record, rejects negative weight, allows 0), `WeightedGraph` constructor + `requireNode` helper given complete. The two methods are stubbed with `UnsupportedOperationException` (deliberately — used as a teaching contrast: UOE = "not implemented", IAE = "bad argument"). Verified: project compiles, tests run 2 pass / 13 fail against stubs; a reference solution passes all 15 (spec is internally consistent and achievable).
- **Acceptance:** `shortestDistance` returns 0 for from==to, -1 for unreachable, `IllegalArgumentException` for unknown node. `shortestPath` returns `[from]`, `[]`, throws respectively; returned list unmodifiable. `O((V+E) log V)`. All 15 tests green.
- **Stretch:** (A) swap `TestRunner` for real JUnit 5; (B) `record Route(List<String>, long)` returning path + cost in one pass; (C) `distancesFrom(String)` single-source-all-targets map.
- **Status:** proposed
- **How to apply on completion:** If they use a plain `ArrayDeque` (Day 9 reflex) instead of `PriorityQueue`, that's the central teaching moment — the priority queue *is* the algorithm. If they mark nodes settled before popping (not on pop), `testDistanceLongerHopCountCanWin` / `testDistancePicksCheaperOfTwoPaths` should expose it. If they attempt decrease-key, point to the stale-entry skip. Watch whether they duplicate the Dijkstra loop across both methods (they will) — reflection Q3 + stretch B set up the fix. Day 11 candidates: (i) **Observer** pattern (the still-pending next GoF); (ii) finally do the **language rotation** — port a Java challenge to Scala/Rust; (iii) **A\*** or **topological sort** if they want to stay on graphs.
- **Day 10 confirmed completed** (folder timestamps `May 18 18:59`, `out/` populated). Stretches A/B/C not visible in tree — assume untouched. JUnit 5 on-ramp still pending (3 consecutive deferrals: Days 7, 8, 9, 10).

## 2026-05-19 — Day 11 (Java track day 7)

- **Topic:** GoF **Observer** pattern — single-threaded pub/sub `EventBus` with typed events
- **Language:** Java (21+, records, sealed interfaces optional, `List<Listener>`, `@FunctionalInterface`)
- **Folder:** `/Users/tairone/personal-pocs/betterme-ai/day-11-observer-eventbus-java/`
- **Difficulty target:** **easy** — design-pattern entry tier, ~45–60 min budget. Easier than Day 8 Decorator (no recursive composition, no harden-the-checkout subplot).
- **Why this topic:**
  - Rotation: Days 9 (BFS) + 10 (Dijkstra) were both algorithms-axis. Time to swing to design patterns.
  - Observer has been a Day-10 deferred candidate twice (Day 9 plan, Day 10 plan). Closing it.
  - Builds vocabulary on a familiar surface: `List`, `record`, `@FunctionalInterface` — all already in the user's active toolbox from Days 5–8.
  - Sets up two future arcs cleanly: (a) the Day-6→Day-8 Strategy→Decorator progression now extends to a third GoF behavioral pattern, (b) future concurrency variant (thread-safe `CopyOnWriteArrayList` or a coroutine-style channel-based version in Scala/Rust) sits naturally on top.
  - Easy enough to leave room for the **JUnit 5 on-ramp stretch** that has been deferred 4 times. Today is the moment — the spec is small and the assertions are obvious (`assertEquals` on captured event counts).
- **Acceptance (core):**
  - `sealed interface Event permits OrderPlaced, OrderShipped, OrderCancelled` (or non-sealed if they prefer — call out the sealed payoff in review). Each is a `record` with order id + minimal fields.
  - `@FunctionalInterface Listener<E extends Event> { void onEvent(E event); }`.
  - `class EventBus` exposes:
    - `<E extends Event> Subscription subscribe(Class<E> type, Listener<E> listener)` — registers a typed listener.
    - `void publish(Event event)` — dispatches to every listener whose `type.isInstance(event)` is true.
    - `Subscription` is a `@FunctionalInterface` (or a `record`) with `unsubscribe()` — calling it must remove the listener.
  - `subscribe` rejects null type and null listener with `IllegalArgumentException`. `publish(null)` does the same.
  - A listener that throws during `publish` must **not** prevent the remaining listeners from being invoked. Catch and continue (log via `System.err`).
  - Unsubscribing during dispatch must not throw `ConcurrentModificationException` — iterate over a defensive snapshot of the listener list.
  - `Main` runs at least four scenarios printing PASS/FAIL: (i) two listeners both receive a published event, (ii) unsubscribed listener stops receiving, (iii) typed filtering — a `Listener<OrderShipped>` does not see `OrderPlaced`, (iv) one throwing listener does not stop the next.
- **Stretch:**
  - **(A) JUnit 5 finally** — pull `junit-platform-console-standalone` jar (the one stretch deferred since Day 7). At least 5 `@Test` methods. Today's small surface area is the perfect on-ramp.
  - **(B) Pattern matching for `instanceof`** with a `switch` on the sealed `Event` hierarchy in `Main` — shows the modern Java 21 idiom and the sealed-types payoff.
  - **(C) `subscribeAll(Listener<Event>)`** convenience that receives every event regardless of type — built on top of the existing typed-subscribe.
- **Status:** proposed

**Why this calibration (easy):** The user has all the primitives — `record`, `@FunctionalInterface`, `List`, defensive copy — already in active vocabulary from Days 5–8. The *new* concept is a single idea: a registry of `(type, listener)` pairs filtered by `Class.isInstance`. No new data structures. No concurrency. No algorithmic invariants. The trickiest part is "iterate over a snapshot so unsubscribe-during-dispatch doesn't blow up" — and even that is just `List.copyOf(listeners)` before the loop. The bulk of the 45–60 min budget should land on the JUnit 5 stretch, which is the strategic goal.

**How to apply on completion:**
- If they reach for `ArrayList` and don't snapshot before iterating, the "unsubscribe during dispatch" scenario in `Main` will throw `ConcurrentModificationException` — that's the teaching moment for `List.copyOf` or `CopyOnWriteArrayList`.
- If they validate with `UnsupportedOperationException`, that habit was broken on Day 7/8 — should be a non-issue, but watch for regression.
- If they skip the JUnit 5 stretch *again*, drop one of the algorithm challenges from Day 12 and make JUnit 5 *the* core challenge — refactor Day 7 word-frequency tests, no new domain. The deferral has gone on long enough.
- If they nail the sealed `Event` hierarchy + pattern-matching switch in stretch B, Day 12 should escalate to **Visitor** (the natural sealed-types-meet-double-dispatch pattern) or to the **Scala port** of today's EventBus to show ADTs + pattern matching in their idiomatic form.

**Day 11 confirmed completed** (folder exists with all 8 source files including `EventBus.java`, `Event` sealed hierarchy, and a `TestRunner` + `EventBusTest` test pair — so the JUnit-style assertion harness landed even though real JUnit 5 was again deferred). `EventBus.publish` correctly: (a) validates with `IllegalArgumentException`, (b) snapshots via `List.copyOf(registrations)` before iterating (the `ConcurrentModificationException` teaching moment was avoided), (c) catches `RuntimeException` per-listener so one throwing listener doesn't block the rest. The unchecked `(Listener<Event>) listener` cast is the one wart — type-safety hand-wave at the registration boundary; mention in passing on a future generics-focused challenge, don't make it the headline. Stretches A/B/C untouched.

## 2026-05-20 — Day 12 (Java track day 8)

- **Topic:** **Balanced Brackets** — stack-based bracket matcher (`isBalanced(String)`)
- **Language:** Java 21+ (no build tool — `Deque`/`ArrayDeque`, `Map.of` lookup table, single-pass char loop)
- **Folder:** `/Users/tairone/personal-pocs/betterme-ai/day-12-balanced-brackets-java/`
- **Difficulty target:** **easy** — algorithms-axis entry tier, ~45 min budget. Pure single-method exercise, smaller surface than Day 11.
- **Why this topic:**
  - Rotation: Day 11 was a design pattern (Observer). Swing back to algorithms / data structures.
  - First **stack-as-stack** problem in the routine. Day 9 used `ArrayDeque` for DFS, but the *algorithm* there was traversal; here the stack-ness IS the algorithm. Reinforces the same data-structure vocabulary on a different shape.
  - Direct attack on the user's recurring **"control flow over data"** gap (Day 5b Roman numerals lesson). The `CLOSER_TO_OPENER` map is handed to them pre-built at the top of the file — the implementation literally cannot help but be data-driven if they use it.
  - Locks in **`IllegalArgumentException` for validation** for the 5th challenge in a row (Days 7, 8, 10, 11, 12). The Day 5b/6 `UnsupportedOperationException`-for-validation regression is fully extinguished.
  - Easy enough to leave the JUnit 5 stretch on the table as a real option (now 5 consecutive deferrals: Days 7, 8, 9, 10, 11).
- **Scaffold state:** `BalancedBrackets.java` shipped with the `CLOSER_TO_OPENER` map pre-built, an extensive step-by-step implementation guide as in-method comments (STEP 1..STEP 4 + edge cases + common bugs section), and `isBalanced` stubbed with `UnsupportedOperationException` (deliberate UOE-as-"not implemented" — contrast with IAE-as-"bad argument", same teaching contrast as Day 10). Demo `main` and 16-test `BalancedBracketsTest` + `TestRunner` harness shipped. **Verified end-to-end**: project compiles, 0 pass / 16 fail against stub, and a reference solution passes all 16 — spec is internally consistent and achievable.
- **Acceptance:** `isBalanced("") == true`, `isBalanced(null)` throws `IllegalArgumentException`, all 16 test cases green. Must use `Deque<Character>` + `ArrayDeque` (not `Stack`, not `LinkedList`). Must use the `CLOSER_TO_OPENER` map (no hand-coded if-else chains). O(n) time, O(n) aux space.
- **Stretch:** (A) **JUnit 5 for real** — the on-ramp deferred since Day 7; today's tiny surface is the perfect moment; assertion API in `TestRunner` already matches Jupiter names. (B) Pluggable bracket alphabets via a `BracketMatcher` class constructor parameter — quiet Strategy moment. (C) Return a `record Result(boolean ok, int errorIndex, String reason)` so callers can underline the bad char (linter-style).
- **Status:** proposed
- **How to apply on completion:**
  - If they reach for a counter (`int depth = 0; depth++/--`) instead of a stack, `testInterleavedIsUnbalanced` (`([)]`) is the teaching moment — that test exists *specifically* to kill the counter approach.
  - If they call `stack.pop()` without checking `isEmpty()` first, the `testCloserWithoutOpenerIsUnbalanced` (`)`) and `testCloserBeforeOpenerIsUnbalanced` (`)(`) cases will throw `NoSuchElementException` instead of returning `false`. Surface the empty-check ordering.
  - If they hand-code `if (c == '(' || c == '[' || c == '{')` instead of using `CLOSER_TO_OPENER.containsValue(c)` / `containsKey(c)`, that's the *exact* control-flow-over-data regression from Day 5b. Worth flagging — the data table was literally placed in their hands.
  - If they return `true` at the end without the final `stack.isEmpty()` check, `testOpenerWithoutCloserIsUnbalanced` (`(`) catches it.
  - If they finally do the JUnit 5 stretch, Day 13 should be the **Scala port** — port either today's `BalancedBrackets` (clean ADT/pattern-match opportunity) or Day 11's `EventBus` (ADT + pattern-match showcase). The language rotation has been pending since Day 7 and is now the biggest unaddressed thread in the routine.
  - Day 13 candidates: (i) **Scala port** of today's BalancedBrackets or Day 11's EventBus — finally start the cross-language contrast arc; (ii) **Singleton + Factory** GoF doubleheader (easy, fills the "common starter patterns" gap); (iii) **Iterator** pattern or **two-pointer palindrome** if they want to stay on algorithms; (iv) escalate to **Visitor** if they nailed sealed types on Day 11 stretch B.

## 2026-05-21 — Day 13 (Java track day 9)

- **Topic:** GoF **Iterator** pattern via a fixed-capacity **circular buffer** (`CircularBuffer<T> implements Iterable<T>` + custom `Iterator<T>`)
- **Language:** Java 21+ (no build tool — generics, `Iterable<T>`, `Iterator<T>`, `NoSuchElementException`)
- **Folder:** `/Users/tairone/personal-pocs/betterme-ai/day-13-iterator-circular-buffer-java/`
- **Difficulty target:** **easy** — design-pattern entry tier, ~45 min budget. Smaller surface than Day 11 Observer.
- **Why this topic:**
  - Rotation: Day 12 was algorithms (stack). Swing back to design patterns. Picked candidate (iii) from Day 12's shortlist.
  - First **Iterator** pattern in the routine. Days 6, 8, 11 covered Strategy / Decorator / Observer — Iterator is the natural next GoF behavioral pattern and the one that demystifies what `for (x : collection)` is actually doing.
  - Reuses Day 9's FIFO / oldest-first walking vocabulary, but now on a plain array — exposes what `ArrayDeque.iterator()` does under the hood.
  - **Reinforces the data-driven idiom** for the third time in a row: the iterator's state (`cursor`, `remaining`, plus the buffer's `head`/`size`) IS the algorithm — zero branching on element values.
  - **Locks in `IllegalArgumentException` for validation** for the 6th challenge in a row (Days 7, 8, 10, 11, 12, 13). UOE-for-validation regression should be fully extinct.
  - **Introduces the one place UOE is actually correct**: `Iterator.remove()`'s default. Explicit teaching contrast between "I haven't implemented it yet" (wrong UOE usage) and "this operation is genuinely unsupported by this class" (right UOE usage).
  - **Gentle generics on-ramp**: first generic class definition in the Java track. The `Object[]` + cast trick surfaces erasure without making it the headline.
- **Scaffold state:** `CircularBuffer.java` ships with constructor + `capacity`/`size`/`isEmpty`/`isFull`/`get` fully implemented. Two stubs (`add`, `iterator`) throw `UnsupportedOperationException` with `TODO` messages. In-method comments structured as Day 12 was — `STEP 1..N` + edge cases + common bugs. Comments are explicit about how to derive `writeIndex = (head + size) % capacity` and the head-advance-on-overflow trick. The iterator section ships a commented-out skeleton anonymous-inner-class so they fill in two method bodies, not write the boilerplate. `TestRunner` + `CircularBufferTest` (16 named scenarios) ported from Day 12. **Verified end-to-end**: project compiles, 3 pass / 13 fail against stub, reference solution passes all 16 — spec is internally consistent and achievable.
- **Acceptance:** all 16 tests green; `Iterable<T>` so enhanced-`for` works; constructor rejects `capacity <= 0`; `add(null)` rejected; `next()` on exhausted iterator throws `NoSuchElementException`; `remove()` left at default UOE; iteration walks `size` (not `capacity`) elements; O(1) `add`.
- **Stretch:** (A) **JUnit 5 for real** — the on-ramp deferred since Day 7 (now 7 deferrals: 7, 8, 9, 10, 11, 12, 13); today's iterator surface is small and the assertion API in `TestRunner` already matches Jupiter. (B) `snapshot()` returning an `Iterable<T>` decoupled from later mutations. (C) **Fail-fast on concurrent modification**: `modCount` field + `ConcurrentModificationException` from `next()` — exactly what `ArrayList.iterator()` does.
- **Status:** proposed
- **How to apply on completion:**
  - If they iterate `capacity` times instead of `size` times, `testIterationOnPartiallyFullBuffer` will fail with stale data or a `ClassCastException` on null — that's the teaching moment.
  - If they forget the `% capacity` modulus, `testIterationOrderAfterManyOverflows` blows up first — head + cursor runs off the array.
  - If they return `null` from exhausted `next()` (instead of throwing `NoSuchElementException`), `testNextOnExhaustedIteratorThrowsNoSuchElement` catches it — and that's the moment to explain the `Iterator` contract.
  - If they override `remove()` to do something cute, `testRemoveIsUnsupported` flags it — surface the "UOE is right HERE because the operation really is unsupported" contrast against their old habit of using UOE for input validation.
  - If they read `(T[]) new Object[capacity]` and balk at the unchecked warning, that's the moment to explain erasure briefly — but keep it under 2 minutes; not the headline today.
  - Watch the Day 12 attempt too — quick scan showed two bugs (`input.charAt(0)` instead of `input.charAt(i)`, and the closer-equality check is inverted: `if (CLOSER_TO_OPENER.get(head).equals(character)) return false` should be `if (!...equals(...))`). If the user mentions Day 12 tests are red, those are the two lines.
- **Day 14 candidates:** (i) **Scala port** of today's `CircularBuffer` or Day 12's `BalancedBrackets` — language rotation has been deferred since Day 7 and is now the single biggest unaddressed thread; (ii) **two-pointer palindrome** kata (easy algorithms, ~30 min) if they want a quick win in Java; (iii) **Factory Method** as the next GoF (Strategy/Decorator/Observer/Iterator covered — Factory fills the creational gap); (iv) revisit Day 12 bugs with the user if they didn't catch them.

## 2026-05-22 — Day 14 (Java track day 10)

- **Topic:** **LRU Cache** — generic `LRUCache<K, V>` with O(1) `get` / `put` via `HashMap<K, Node>` + intrusive doubly-linked list (with sentinel head/tail).
- **Language:** Java 21+ (no build tool — generics with two type params `<K, V>`, `HashMap`, private inner `Node` class, `NoSuchElementException`).
- **Folder:** `/Users/tairone/personal-pocs/betterme-ai/day-14-lru-cache-java/`
- **Difficulty target:** **intermediate** — algorithms / data-structures axis, ~60–90 min for core. Step up from Days 12/13 (both easy). First challenge where the *data-structure choice IS the algorithm* (two structures composed to hit O(1) on operations neither could do alone).
- **Why this topic:**
  - User explicitly requested: Java + JUnit tests included + step-by-step comments. Today's the day to close the **JUnit 5 on-ramp** that has been deferred since Day 7 (now 8 deferrals: 7, 8, 9, 10, 11, 12, 13). The test file is written as a *real* JUnit 5 file (`org.junit.jupiter.api.Test` imports, real `@Test` annotation, real static-imported `Assertions`); the project ships an in-tree Jupiter shim under `src/test/java/org/junit/jupiter/api/` so it runs offline with plain `javac`/`java`. README documents the mechanical migration to real Jupiter (delete shim, drop real jars on classpath, swap launcher). Rationale for not using real Jupiter directly: same blocker as Day 10 — `junit-platform-console-standalone` jar still not in `~/.m2` cache.
  - Rotation: Day 13 was a design pattern (Iterator). Swing back to algorithms / data structures.
  - Builds on Day 13's fixed-capacity-with-eviction shape but introduces the *composition* lesson: HashMap alone is O(1) lookup but no order; linked list alone is O(1) reorder but O(n) lookup; together they hit O(1) on both. This is the canonical example of "pick your data structures, then the algorithm writes itself."
  - First **two-type-parameter generics** in the routine (`<K, V>`). Small but real step up from Day 13's `<T>`. No `Object[]` cast needed (uses `HashMap` directly), so erasure does not become the headline.
  - Introduces the **sentinel-node trick** (dummy `head`/`tail` always exist, real nodes live strictly between them) — kills every "is this the first node?" / "is this the last node?" branch. 30-second technique that pays career dividends.
  - **Locks in `IllegalArgumentException` for validation** for the 7th challenge in a row (Days 7, 8, 10, 11, 12, 13, 14). The Day 5b/6 UOE-for-validation habit is now deeply extinct.
  - **Reinforces the right place to use `UnsupportedOperationException`**: the two stubbed methods (`get`, `put`) throw UOE with `TODO: implement` messages — same deliberate teaching contrast as Day 13's `Iterator.remove()` default. "UOE = I haven't implemented it yet" vs "IAE = you gave me a bad argument."
- **Scaffold state:** `LRUCache.java` ships with constructor + validation, `size`/`capacity`/`isEmpty`/`containsKey`, the three private linked-list helpers (`linkAfterHead`, `unlink`, `moveToFront`), and the inner `Node` class **all fully implemented**. `get` and `put` are stubbed with `throw new UnsupportedOperationException("TODO: implement ...")` and detailed in-method `STEP 1..N` comments (same style as Day 12/13 scaffolds). README explicitly tells them to replace the `throw` lines with real logic.
- **Test suite:** `LRUCacheTest` (20 `@Test`-annotated methods) + `TestRunner` (reflection-based runner that discovers `@Test`-annotated methods — does NOT use the `testXxx` naming convention from Day 10/11/12/13 because today's test file is a real JUnit 5 file). Shim files: `org/junit/jupiter/api/Test.java`, `org/junit/jupiter/api/Assertions.java`, `org/junit/jupiter/api/function/Executable.java`. Assertions cover: `assertEquals` (Object + long overloads), `assertTrue`/`assertFalse` (one-arg + two-arg overloads matching real Jupiter), `assertThrows` (returns the thrown exception, matches real Jupiter signature).
- **Verified end-to-end:** Project compiles. Tests run against the stubbed scaffold: 8 pass / 12 fail (the 8 are the trivial no-state-touch tests — constructor validation, empty-state checks, null-arg rejection; the 12 require working `get`/`put`). A reference implementation passes all 20. Spec is internally consistent and achievable. The demo `main` runs cleanly when bodies are filled in.
- **Acceptance:** All 20 tests green. O(1) `get`/`put`/`containsKey`/`size`/`isEmpty`. Constructor rejects `capacity <= 0` with IAE. Null keys and null values rejected with IAE. `get(missingKey)` throws `NoSuchElementException` (not `null`). `containsKey(...)` does NOT count as a use (true peek, no recency change). `get` and `put(existingKey, ...)` both bump to most-recently-used. `put(newKey, ...)` into a full cache evicts the LRU AND removes its key from the index map.
- **Stretch:** (A) Pluggable `EvictionPolicy<K, V>` — refactor LRU into a Strategy; add `LFUCache` reusing the same skeleton; (B) thread-safe variant via `synchronized` / `ReentrantLock` / striped locks; (C) **wire up real JUnit 5** — pull `junit-platform-console-standalone`, delete the shim, swap the launcher (the work is real and tiny — and unblocks every future Java challenge).
- **Status:** proposed
- **How to apply on completion:**
  - If they forget to call `moveToFront(node)` in `get` (the easy bug — write a "fast" read-only `get`), `getBumpsToMostRecentlyUsed` catches it. THE central LRU correctness test.
  - If they handle `put(existingKey, ...)` by allocating a new node instead of updating in place, `putExistingKeyUpdatesValue` flags it via `size() != 1`.
  - If `containsKey` accidentally calls `get` internally, `containsKeyDoesNotCountAsUse` catches the silent recency-change.
  - If they evict from the wrong end (just-after-head instead of just-before-tail), basically every eviction test explodes — that's the moment to draw the linked list on paper.
  - If they unlink the LRU node from the list but forget to remove its key from the `index` map, `manyOverflowsKeepOnlyRecentEntries` fails because `size()` reports too many entries.
  - If the project compiles but the user asks "where's JUnit?" — point at `src/test/java/org/junit/jupiter/api/` shim and the README "About the JUnit 5 setup" section. The whole point is that the test file is bit-for-bit a real JUnit 5 file.
  - Watch the Day 13 attempt too — recent commit `c462857 implement iterator and fixed stack` suggests CircularBuffer is now working; quick scan also showed Day 12's `BalancedBrackets` still has the two bugs flagged on Day 13 (`input.charAt(0)` instead of `input.charAt(i)`; inverted closer-equality). If the user mentions Day 12 tests are still red, those are the two lines.
- **Day 15 candidates:** (i) **Scala port** of today's LRUCache (HashMap + Doubly-linked list in Scala — natural for showing ADT-with-mutable-state contrast against Java) or Day 13's CircularBuffer (cleanest pure-FP shape) — language rotation now 8 deferrals deep; (ii) **Factory Method** as the next GoF creational pattern (Strategy/Decorator/Observer/Iterator covered behavioral; Factory fills the creational gap); (iii) **two-pointer palindrome** kata (easy algorithms, ~30 min, gives them a quick-win day after today's intermediate); (iv) **escalate to real JUnit 5** as the *core* of Day 15 — drop the standalone jar, port today's tests, build-tool-free Maven-equivalent CLI.

## 2026-05-25 — Day 15 (Java track day 11)

- **Topic:** **Valid Palindrome** via two-pointer — `Palindrome.isPalindrome(String s)` (alphanumeric-only, case-insensitive)
- **Language:** Java 21+ (no build tool, no external deps — `Character.isLetterOrDigit`, `Character.toLowerCase`, two `int` indices)
- **Folder:** `/Users/tairone/personal-pocs/betterme-ai/day-15-palindrome-two-pointer-java/`
- **Difficulty target:** **easy** — algorithms-axis entry tier, ~30–45 min budget. Quick-win day after Day 14's intermediate LRU cache. User explicitly asked for easy + Java + full test suite.
- **Why this topic:**
  - Picked candidate (iii) from Day 14's shortlist. Quick-win calibration for an "easy" request, lands the two-pointer technique that is foundational for ~10 future algorithm challenges (3sum, container-with-most-water, valid-palindrome-II, reverse-string, merge-sorted-arrays).
  - **First two-pointer challenge** in the routine. Single static method, O(n) time, O(1) extra space — the two indices ARE the algorithm. Same "let the data drive" lesson the user has been working through (Roman numerals → balanced brackets), now applied to indices instead of a lookup table.
  - **Continues the JUnit 5 on-ramp from Day 14.** Same in-tree Jupiter shim (`@Test`, `Assertions`, `Executable` under `src/test/java/org/junit/jupiter/api/`), same migration story documented in the README. Today's test file is a real JUnit 5 file byte-for-byte. Real-Jupiter-jar swap is still stretch C — Day 14 deferred the actual swap and Day 15 keeps the pattern alive without burning a fresh on-ramp moment.
  - **Locks in `IllegalArgumentException` for validation** for the 8th challenge in a row (Days 7, 8, 10, 11, 12, 13, 14, 15). UOE-for-validation regression is by now deeply extinct.
  - **Reinforces the right place for `UnsupportedOperationException`** (stubbed `throw new UnsupportedOperationException("TODO: implement ...")` in the production method — same teaching contrast as Days 12/13/14: UOE = "I haven't implemented it yet", IAE = "you gave me a bad argument").
  - Smaller surface area than any prior Java challenge — single static method, no class state, no generics. Deliberate recharge day after the LRU cache (which had two composed data structures and two type parameters).
- **Scaffold state:** `Palindrome.java` ships with `isPalindrome` stubbed (`throw new UnsupportedOperationException("TODO: ...")`) plus detailed `STEP 1..STEP 4` in-method comments, an edge-cases list, and a "common bugs" section structured exactly like Day 12/13/14 scaffolds. `main` demo runs nine sample inputs. Test suite is **22 `@Test`-annotated methods** in `PalindromeTest` covering: null rejection (1), empty/single-char/punctuation-only base cases (4), simple letters-only positives and negatives (5), case folding (2), digits (3), non-alphanumeric skipping (3), and canonical interview examples including "A man, a plan, a canal: Panama", "Was it a car or a cat I saw?", "race a car", "No 'x' in Nixon" (4). `TestRunner` reflectively discovers `@Test` methods.
- **Verified end-to-end:** Project compiles. Tests run 0 pass / 22 fail against the stub. A reference solution (`while (left < right) { skip non-alnum on both ends; compare lowercased chars; advance both }`) passes all 22. Spec is internally consistent and achievable.
- **Acceptance:** All 22 tests green. O(n) time, O(1) extra space (no `StringBuilder`, no `s.toLowerCase()` on the whole string, no reversal). `null` throws `IllegalArgumentException`. Uses `Character.isLetterOrDigit(char)` (NOT hand-coded ASCII ranges). Uses `Character.toLowerCase(char)` at the comparison step only.
- **Stretch:** (A) `reverseWords` — a different two-pointer warmup; (B) `isPalindromeAllowingOneDeletion` (LeetCode 680) — same algorithm with a single branch point; (C) **real JUnit 5** (carried over from Day 14 — drop standalone jar, delete shim, swap launcher).
- **Status:** proposed
- **How to apply on completion:**
  - If they reach for `s.toLowerCase().replaceAll("[^a-z0-9]", "")` and reverse-compare, surface the technique: works, but defeats the whole point. The two-pointer technique is what they're learning today, not the answer to this specific question. Future challenges (3sum, container-with-most-water) will REQUIRE it.
  - If they hand-code the alphanumeric check as `(c >= 'a' && c <= 'z') || ...`, that's the Day 5b/12 control-flow-over-data regression again — point at `Character.isLetterOrDigit(char)`.
  - If they forget the inner-loop bound check (`left < right` inside the skip loops), `onlyNonAlphanumericIsPalindrome` (`" ,.;:!?-"`) will throw `StringIndexOutOfBoundsException` instead of returning `true`. That's the teaching moment for "skip-loops must also check the outer bound, not just the predicate."
  - If they forget to advance `left++; right--;` at the end of the loop body, infinite loop — `simpleNonPalindrome` will hang.
  - If they lowercase the whole string up front, point out the O(n) extra allocation; the whole point of two-pointer is O(1) extra space.
  - **Watch Day 14 too:** quick scan of `LRUCache.java` shows the user started but the `get` body is broken (missing semicolon, returns `node` instead of `node.value`) and `put` body has logic errors (calls `linkAfterHead(node)` while `node` is still null; missing branch for the existing-key case; capacity check has inverted condition `capacity()>=size()`). If they mention Day 14 tests are still red, those are the four lines.
- **Day 16 candidates:** (i) **Scala port** of any Java challenge (LRU cache, EventBus, BalancedBrackets, or today's Palindrome — language rotation now 9 deferrals deep, this is by far the biggest unaddressed thread); (ii) **Factory Method** as the next GoF creational pattern (the Day 15 candidate (ii) that was deferred today); (iii) **3sum** or **container-with-most-water** if today's two-pointer landed cleanly and they want a harder algorithm rep; (iv) **finish Day 14 LRU cache** and revisit it (the user has unfinished work there — coaching opportunity).
