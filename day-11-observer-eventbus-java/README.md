# Day 11 — Observer Pattern: in-process EventBus (Java)

Java track, day 7. Design-patterns axis. Third GoF behavioral pattern after
Strategy (Day 6) and Decorator (Day 8).

Days 9 (BFS) and 10 (Dijkstra) were both algorithms — back to patterns today.
"Strategy" lets the caller plug in *which* algorithm; "Observer" lets the
caller plug in *who gets notified*. Today you build a tiny pub/sub bus for an
e-commerce domain.

Single-folder, no Maven/Gradle, no downloaded jars. Plain `javac` + `java`.

## Learning outcome

After this challenge you should be able to:

- Distinguish Observer ("notify everyone who cares") from Strategy ("pick one
  algorithm to run").
- Use a `Class<E>` token as a runtime type filter (`type.isInstance(event)`).
- Apply the **snapshot-for-dispatch** trick to make
  unsubscribe-during-dispatch safe without locks or
  `CopyOnWriteArrayList`.
- Build a fault-isolating dispatcher: a throwing listener never crashes the
  bus or starves later listeners.
- Use Java 21 **sealed interfaces** + **pattern-matching switch** to get an
  exhaustive event router with no `default` branch (stretch B).

## Your task

Open `src/main/java/ai/betterme/EventBus.java`. Two methods are stubbed with
`throw new UnsupportedOperationException(...)`. Replace each stub body. The
Javadoc on each method spells out the full contract and a step-by-step
implementation sketch. The records (`OrderPlaced`, `OrderShipped`,
`OrderCancelled`), the sealed `Event` interface, the `Listener` and
`Subscription` functional interfaces, and the private `Registration` record
inside `EventBus` are given to you complete.

> Note on exceptions: the stubs throw `UnsupportedOperationException`, and
> that is *correct* — "this operation does not exist yet" genuinely is what
> UOE means. That is a different situation from a caller passing a bad
> argument, which is always `IllegalArgumentException`. Keep that line sharp
> (Days 5b and 6 blurred it; Days 7–10 fixed it — don't regress today).

### Contract summary

| Concern                                | Required behavior                                |
|----------------------------------------|--------------------------------------------------|
| `subscribe(null, ...)` / `(_, null)`   | `IllegalArgumentException`                       |
| `publish(null)`                        | `IllegalArgumentException`                       |
| Listener subscribed to wrong subtype   | Not invoked                                      |
| `Subscription.unsubscribe()` x2        | No-op (second call must not throw)               |
| Listener throws `RuntimeException`     | Log to `System.err`, next listeners still run    |
| Listener unsubscribes during dispatch  | No `ConcurrentModificationException`             |

## Build and run

Compile everything (main + tests):

```bash
cd /Users/tairone/personal-pocs/betterme-ai/day-11-observer-eventbus-java
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

Before you write any code, run the tests once. You should see most tests
fail with `UnsupportedOperationException` — that's the stubs. The 3 record-
validation tests (`testOrderPlacedRejectsNullOrderId`, etc.) should already
PASS because the records are given complete. Your job is **all green**.

## About the test harness

`TestRunner` is a tiny, dependency-free harness: it reflects over
`EventBusTest`, runs every `public void testXxx()` method, and prints
PASS/FAIL. It is here so the challenge ships with **real, named, one-scenario
tests** without forcing a build tool on you.

Each test is one focused scenario with one assertion-shape — that is the
habit to build. Compare it to the hand-rolled `Main` PASS/FAIL runner you
used from Day 5b through Day 9: when one thing breaks, a named test tells
you *which* scenario, not just "something failed".

## Stretch (you chose B)

**B — Sealed-type pattern matching in `Main`.** Implement the `describe(Event)`
method in `Main.java` as a single `switch` expression over the sealed `Event`
hierarchy:

```java
return switch (event) {
    case OrderPlaced p    -> "placed: " + p.orderId() + " ($" + (p.totalCents() / 100.0) + ")";
    case OrderShipped s   -> "shipped: " + s.orderId() + " via " + s.trackingNumber();
    case OrderCancelled c -> "cancelled: " + c.orderId() + " (" + c.reason() + ")";
};
```

No `default` clause. That's the entire payoff: the sealed permits list makes
the switch exhaustive, and if you ever add a fourth event the compiler will
refuse to build `Main` until you add the corresponding case. That compile-
time guarantee is what raw `instanceof` chains can never give you.

Then wire `Main.main` to:
1. Subscribe a `Listener<Event>` (or one listener per type) that prints
   `describe(event)`.
2. Publish one of each event so all three switch arms fire.
3. Demonstrate `unsubscribe` and the throwing-listener resilience —
   see the TODOs in `Main`.

(The other stretches A — real JUnit 5 — and C — `subscribeAll` — remain
optional after-credit.)

## Idioms to apply

- `sealed interface Event permits ...` + `@FunctionalInterface` on
  `Listener` and `Subscription`.
- `record` for events with `Objects.requireNonNull` validation in compact
  constructors (given).
- `Class<E>` token + `type.isInstance(event)` for typed dispatch.
- `List.copyOf(registrations)` as the dispatch snapshot.
- `try { listener.onEvent(...) } catch (RuntimeException e) { log + continue }`.

## Anti-patterns to avoid

- Iterating the live `registrations` list during `publish` — one
  self-unsubscribing listener will throw `ConcurrentModificationException`.
- Letting a listener's `RuntimeException` propagate out of `publish` and
  abort the rest of the dispatch loop.
- Throwing `UnsupportedOperationException` from `subscribe`/`publish` when a
  caller passes `null` — null args are always `IllegalArgumentException`.
- Reaching for `CopyOnWriteArrayList`, `synchronized`, or any concurrency
  primitive — this is a deliberately single-threaded bus. The reflection
  prompt is where threading enters the conversation.
- Storing listeners in a `Set` or `HashMap` keyed by listener identity —
  you'd lose insertion order and complicate the "two equal lambdas" case.
  A plain `List<Registration>` is correct.

## Reflection (after you finish)

1. **Strategy vs Observer in one sentence each.** What does the *caller*
   plug into a Strategy, and what does the *caller* plug into an Observer?
   Why does the Observer registry need to be a *list*, not a single field
   like Strategy's?
2. **The snapshot.** `List.copyOf(registrations)` allocates on every
   `publish`. What workload would make that allocation cost matter, and
   what is the smallest change that would let you skip it in the common
   "no one unsubscribes during dispatch" case?
3. **Threading.** Today's bus is single-threaded. If two threads called
   `publish` concurrently, exactly which line is the data race? What is
   the smallest change that fixes it — and what does that change cost in
   terms of throughput on the read path? (You don't have to implement it
   — just be able to describe the change in one sentence. This sets up a
   future concurrency-patterns day.)

Time budget: 60–90 minutes for the core (both methods + all tests green);
+15–20 min for stretch B (it is genuinely small once `describe` is the
only thing left to write).
