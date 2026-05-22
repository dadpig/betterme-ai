# Day 14 — LRU Cache (O(1) get + O(1) put)

> **Axis:** algorithms / data structures (HashMap + doubly-linked list)
> **Difficulty:** intermediate (~60–90 min for core)
> **Language:** Java 21+ (no build tool, no external deps)
> **Strategic on-ramp:** today is the day **JUnit 5** finally lands (after 7 deferrals)

## The challenge

Implement a generic `LRUCache<K, V>` with a fixed capacity. Every `get` or `put`
on an existing key counts as a **use** and bumps that entry to the most-recent
position. When you `put` a new key into a full cache, the **least-recently-used**
entry is evicted to make room.

Target performance: **O(1) get, O(1) put, O(1) containsKey** — amortized.

```
LRUCache<String, Integer> cache = new LRUCache<>(2);
cache.put("a", 1);                    // list: a
cache.put("b", 2);                    // list: b -> a
cache.get("a");        // returns 1   // list: a -> b
cache.put("c", 3);                    // capacity hit, evicts b. list: c -> a
cache.containsKey("b"); // false
cache.get("a");        // returns 1   // list: a -> c
```

## Why this is an algorithms-and-data-structures challenge

The naïve impl (`LinkedHashMap` with `accessOrder=true`) does this in three
lines, but hides every interesting choice. The point of today is to build it
from scratch with the two-data-structure composition that makes O(1) possible:

- **`HashMap<K, Node>`** — O(1) lookup of the node for any key.
- **Doubly-linked list with sentinel head/tail** — O(1) move-to-front and
  O(1) evict-tail because every node knows its neighbours.

Either data structure alone is too slow for at least one of the operations.
Composing them is the whole game.

The sentinel head/tail trick is worth dwelling on for a minute: by giving
the list two pre-allocated dummy nodes that always exist, every real node
has non-null `prev` and `next`, which kills every "is this the first node?"
and "is this the last node?" branch. The data structure absorbs the special
cases.

## What you write

In `src/main/java/ai/betterme/LRUCache.java`:

- `class LRUCache<K, V>`
- Constructor `LRUCache(int capacity)` — throws `IllegalArgumentException` if `capacity <= 0`
- `V get(K key)` — O(1); bumps `key` to most-recently-used; throws `NoSuchElementException` if missing
- `void put(K key, V value)` — O(1); bumps `key` to most-recently-used; evicts LRU if size exceeds capacity
- `boolean containsKey(K key)` — O(1); **does not** count as a use (peek only)
- `int size()`, `int capacity()`, `boolean isEmpty()` — convenience

The file ships with the constructor, validation, `size()`, `capacity()`,
`isEmpty()`, `containsKey()`, and the three private linked-list helpers
(`linkAfterHead`, `unlink`, `moveToFront`) **already written** — read them
top to bottom and use them. `get` and `put` are stubbed with
`throw new UnsupportedOperationException("TODO: implement ...")` and ship
with detailed step-by-step comments structured as `STEP 1 / STEP 2 / ...`
blocks. Replace the `throw` lines with the real logic.

> Note the deliberate teaching contrast in exception types: `UnsupportedOperationException`
> in the stubs means "I haven't implemented it yet"; `IllegalArgumentException`
> in the validation means "you gave me a bad argument". Don't confuse the two
> in your own code — that was the original Roman-numerals / Discounts gap.

## Acceptance

- All **20 tests** in `LRUCacheTest` pass.
- O(1) for `get`, `put`, `containsKey`, `size`, `isEmpty` — no list scans.
- Constructor throws `IllegalArgumentException` if `capacity <= 0`.
- `null` keys and `null` values are rejected with `IllegalArgumentException`.
- `get(missingKey)` throws `NoSuchElementException` (**not** returns `null`).
- `containsKey(...)` does **not** affect eviction order — it is a true peek.
- `get(existing)` and `put(existing, newValue)` both count as a use (bump to front).
- `put(newKey, ...)` into a full cache evicts the current LRU.

## Build and run

From inside this directory.

### Compile

```sh
mkdir -p out
javac -d out \
  src/main/java/ai/betterme/LRUCache.java \
  src/test/java/org/junit/jupiter/api/function/Executable.java \
  src/test/java/org/junit/jupiter/api/Test.java \
  src/test/java/org/junit/jupiter/api/Assertions.java \
  src/test/java/ai/betterme/TestRunner.java \
  src/test/java/ai/betterme/LRUCacheTest.java
```

### Run the demo

```sh
java -cp out ai.betterme.LRUCache
```

### Run the test suite

```sh
java -cp out ai.betterme.TestRunner
```

You should see 20 lines of `PASS` and exit code 0 once your implementation
is complete.

## About the JUnit 5 setup

Today's test file is written **exactly as it would be against real JUnit 5**:

```java
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
// ...
@Test
public void putThenGetReturnsValue() { ... }
```

Same package paths, same annotation name, same `Assertions` static-import
pattern. To keep the project runnable with zero external jars, the project
ships a **tiny in-tree shim** of `org.junit.jupiter.api.Test`,
`org.junit.jupiter.api.Assertions`, and `org.junit.jupiter.api.function.Executable`
under `src/test/java/org/junit/jupiter/api/`. The shim only implements the
five assertions you actually use today.

**Migrating to real JUnit 5** later (when you wire up Maven/Gradle or grab
the `junit-platform-console-standalone` jar) is mechanical:

1. Delete the three shim files in `src/test/java/org/junit/jupiter/api/`.
2. Put the real Jupiter jars on the classpath (`junit-jupiter-api`,
   `junit-jupiter-engine`, `junit-platform-console-standalone`).
3. Run `java -jar junit-platform-console-standalone.jar --class-path out
   --select-class ai.betterme.LRUCacheTest` instead of `ai.betterme.TestRunner`.

`LRUCacheTest.java` itself needs **zero changes**. That's the on-ramp.

## Stretch goals

- **(A) Pluggable eviction policy.** Today's policy is LRU. Refactor the
  eviction step into a `EvictionPolicy<K, V>` strategy (yes — Day 6's
  Strategy pattern, applied for real). Then implement `LFUCache` reusing
  the same `Map + linked list` skeleton with a different bump-to-front
  rule. This is where today's code becomes a *family*.
- **(B) Thread-safe variant.** Wrap every public method in
  `synchronized` — easy, correct, contended. Or wire up a `ReentrantLock`.
  Or get fancy with a striped lock. Each choice teaches a different lesson.
- **(C) Real JUnit 5.** Pull the `junit-platform-console-standalone` jar
  and follow the migration steps above. The work is real and tiny — and
  it un-blocks every future Java challenge in this routine.

## Why this challenge today

- **Rotation:** Day 13 was a design pattern (Iterator). This swings back
  to algorithms / data structures.
- **Builds on Day 13's vocabulary.** Same fixed-capacity-with-eviction
  shape, same `IllegalArgumentException` discipline. But where Day 13's
  circular buffer was a single backing array, today's LRU is the first
  challenge where **the data-structure choice IS the algorithm** — two
  structures composed to hit O(1) on operations neither could do alone.
- **First real generics with two type parameters.** `<K, V>` is a small
  but real step up from Day 13's `<T>`. The cast trick is gone (we use
  `HashMap` directly, no `Object[]`).
- **The JUnit 5 on-ramp closes a 7-deferral thread.** Days 7, 8, 9, 10,
  11, 12, 13 all kept the JUnit stretch goal open. Today's test file
  *is* a JUnit 5 file (real imports, real annotation) — the shim is the
  ramp, and the migration documented above is the final step.
- **Sentinel-node trick.** A 30-second technique that pays dividends
  for the rest of your career. Today is the right scale to learn it on.

## What to watch out for (common bugs)

- **Forgetting to bump on `get`.** Easy to write a "fast" `get` that
  reads through the map and forgets the `moveToFront(node)` call. The
  test `getBumpsToMostRecentlyUsed` catches it — that scenario is the
  whole point of the data structure.
- **Re-inserting an existing key as a brand-new node.** `put("a", 99)`
  on a cache that already has `"a"` should update the value in place,
  not allocate a second node. Otherwise `size()` exceeds reality and
  eviction order desyncs from the map. `putExistingKeyUpdatesValue`
  flags it.
- **`containsKey` bumping recency.** Subtle. If `containsKey` calls
  `get` internally, you're silently changing the access order of the
  cache from a "peek". `containsKeyDoesNotCountAsUse` catches it.
- **Evicting from the wrong end.** The LRU lives just before the
  `tail` sentinel, not just after the `head`. Mixing them up will
  evict the MOST-recently-used entry, which is a particularly tragic
  bug.
- **Forgetting to remove the evicted node's key from the map.**
  Eviction has two steps: unlink the node from the list *and* remove
  the key from `index`. Forgetting the map removal means the cache
  silently exceeds its declared capacity forever.
