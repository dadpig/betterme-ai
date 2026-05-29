# Day 17 — Two-Sum (single-pass hash map)

> **Axis:** algorithms (hash-map lookup turns O(n^2) into O(n))
> **Difficulty:** easy (~30–45 min)
> **Language:** Java 21+ (no build tool, no external deps)
> **JUnit 5 setup:** same in-tree Jupiter shim as Days 14/15/16 — the on-ramp continues

## The challenge

Implement `TwoSum.indicesSummingTo(int[] nums, int target)`: returns the
two distinct indices `[i, j]` (with `i < j`) such that
`nums[i] + nums[j] == target`. Each input has exactly one solution, and
an element may not be used twice.

```
indicesSummingTo(new int[]{2, 7, 11, 15}, 9)    -> [0, 1]   (2 + 7 == 9)
indicesSummingTo(new int[]{3, 2, 4},        6)  -> [1, 2]   (2 + 4 == 6)
indicesSummingTo(new int[]{3, 3},           6)  -> [0, 1]   (3 + 3 == 6)
indicesSummingTo(new int[]{-1, -2, -3, -4}, -7) -> [2, 3]   ((-3) + (-4))
indicesSummingTo(null, 9)                        -> IllegalArgumentException
indicesSummingTo(new int[]{1}, 1)                -> IllegalArgumentException (len < 2)
indicesSummingTo(new int[]{1, 2, 3}, 100)        -> IllegalArgumentException (no solution)
```

Target performance: **O(n) time, O(n) extra space**. Do NOT use two
nested loops — walk the array once, building a `HashMap<Integer, Integer>`
from value to index as you go.

## Why this is a hash-map challenge

The obvious O(n²) solution is two nested loops. Correct, but quadratic.

The classic O(n) one-pass solution flips the question: for each element
`nums[i]`, the value that completes the pair is `complement = target - nums[i]`.
If you have already seen the complement at some earlier index `j`, you
are done. Otherwise, remember `(nums[i] -> i)` in a `Map<Integer, Integer>`
and keep scanning.

```
seen = empty HashMap<Integer, Integer>
for i in 0..nums.length:
    complement = target - nums[i]
    if seen.containsKey(complement):
        return new int[]{ seen.get(complement), i }   // [j, i] with j < i
    seen.put(nums[i], i)
throw IllegalArgumentException("no two indices sum to " + target)
```

O(n) time, O(n) extra space — and the hash map IS the algorithm. Same
"let the data drive" lesson from the Roman-numerals / balanced-brackets /
two-pointer-palindrome days, now applied to lookup instead of indexing.

## What you write

In `src/main/java/ai/betterme/TwoSum.java`:

- `public static int[] indicesSummingTo(int[] nums, int target)` — implement it.

The file ships with detailed step-by-step `STEP 1..STEP 4` comments,
a list of edge cases to trace by hand before writing code, and a
"common bugs" section. The method body is stubbed with
`throw new UnsupportedOperationException("TODO: implement ...")` —
**replace that one line with the real logic**.

> Note the deliberate teaching contrast in exception types:
> `UnsupportedOperationException` in the stub means "I haven't
> implemented it yet"; `IllegalArgumentException` in the validation
> means "you gave me a bad argument". Don't confuse the two.

## Acceptance

- All **22 tests** in `TwoSumTest` pass.
- O(n) time, O(n) extra space (no nested loops, no sort-then-two-pointer).
- `null` input throws `IllegalArgumentException` (NOT `NullPointerException`,
  NOT `UnsupportedOperationException`).
- Array of length `< 2` throws `IllegalArgumentException`.
- No-solution case throws `IllegalArgumentException` (NOT `null`,
  NOT a sentinel `[-1, -1]`, NOT an empty array).
- Returns a length-2 `int[]` with the smaller index first
  (`result[0] < result[1]`).
- Uses `java.util.HashMap<Integer, Integer>` — that is, the
  hash-map idiom; not a nested loop, not a sort.
- `check-before-put` ordering inside the loop (so that `[3, 3]` returns
  `[0, 1]`, not `[1, 1]`).

## Build and run

From inside this directory.

### Compile

```sh
mkdir -p out
javac -d out \
  src/main/java/ai/betterme/TwoSum.java \
  src/test/java/org/junit/jupiter/api/function/Executable.java \
  src/test/java/org/junit/jupiter/api/Test.java \
  src/test/java/org/junit/jupiter/api/Assertions.java \
  src/test/java/ai/betterme/TestRunner.java \
  src/test/java/ai/betterme/TwoSumTest.java
```

### Run the demo

```sh
java -cp out ai.betterme.TwoSum
```

### Run the test suite

```sh
java -cp out ai.betterme.TestRunner
```

You should see 22 lines of `PASS` and exit code 0 once your implementation
is complete.

## About the JUnit 5 setup

Identical to Days 14/15/16: this test file is written **exactly as it
would be against real JUnit 5**:

```java
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
// ...
@Test
public void canonicalFirstTwoElementsAtStart() { ... }
```

To keep the project runnable with zero external jars, the project ships
a tiny in-tree shim of `org.junit.jupiter.api.Test`,
`org.junit.jupiter.api.Assertions`, and
`org.junit.jupiter.api.function.Executable` under
`src/test/java/org/junit/jupiter/api/`.

**Migrating to real JUnit 5** later is mechanical:

1. Delete the three shim files in `src/test/java/org/junit/jupiter/api/`.
2. Put the real Jupiter jars on the classpath (`junit-jupiter-api`,
   `junit-jupiter-engine`, `junit-platform-console-standalone`).
3. Run `java -jar junit-platform-console-standalone.jar --class-path out
   --select-class ai.betterme.TwoSumTest` instead of `ai.betterme.TestRunner`.

`TwoSumTest.java` itself needs **zero changes**.

## Stretch goals

- **(A) `int[][] allPairsSummingTo(int[] nums, int target)`** — return
  EVERY unordered pair of indices whose values sum to `target`, not just
  the first one. The map now needs to store *every* index where a value
  appeared (so `Map<Integer, List<Integer>>`), and the loop emits one
  result row per index in the complement's bucket. Same single pass.
- **(B) `boolean hasThreeSum(int[] nums, int target)`** — does any
  triple `(i, j, k)` with `i < j < k` sum to `target`? The hash-map
  trick generalizes: pick `k` outermost, then run the Two-Sum search
  on the slice `[0, k)` for `target - nums[k]`. O(n²).
- **(C) Real JUnit 5.** Pull the `junit-platform-console-standalone`
  jar and follow the migration steps above. Stretch C carried over
  from Days 14, 15, 16.

## Why this challenge today

- **Builds on Day 16** (Factory Method with sealed records): you locked
  in `IllegalArgumentException` for validation and `switch` for
  data-driven dispatch. Today reuses both — IAE on all three failure
  modes (null, too short, no solution), and a `HashMap` lookup replaces
  what would otherwise be a nested loop.
- **Reinforces from Day 15 review:** keep the two-step "validate then
  loop" discipline; resist the `s.toLowerCase()`-style "do the whole
  thing eagerly" reflex (here: don't sort, don't pre-process — let the
  scan-with-state pattern do the work as you go).
- **New stretch:** first time a `HashMap<K, V>` is used **as the
  algorithm** (Days 7 word-frequency and Day 14 LRU used HashMap, but
  there it was a count store / index store; here the map IS the search).
  Introduces the **complement trick** that powers ~10 future
  interview-grade algorithms (Two-Sum, Three-Sum, longest-substring-
  without-repeating, subarray-sum-equals-k).
- **Rotation:** Day 16 was a design pattern (Factory Method). Swing
  back to algorithms.
- **Difficulty calibration:** explicitly easy — single static method,
  ~10 lines of real logic, ~45 min budget. Quick-win day on the
  algorithm axis after the creational-pattern day.
- **Locks in `IllegalArgumentException` for validation** for the 10th
  challenge in a row (Days 7, 8, 10, 11, 12, 13, 14, 15, 16, 17).
- **Continues the JUnit 5 on-ramp.** Same shim setup as Days 14/15/16,
  same migration story. Stretch C remains the real-JUnit swap.

## What to watch out for (common bugs)

- **Put-before-check** — if you do `seen.put(nums[i], i)` BEFORE the
  `containsKey(complement)` check, the `[3, 3]` test returns `[1, 1]`
  (same index twice). CHECK first, THEN put.
- **Returning `[i, j]` instead of `[j, i]`** — the hit happens when
  you are AT index `i` and the complement was stored EARLIER at index
  `j`. Smaller index first means `[j, i]`.
- **Nested loops** — works on the tests but is O(n²) and skips the
  whole lesson. The hash-map technique is the point.
- **Sorting the input first** — destroys the original indices, which
  are exactly what the spec asks you to return.
- **Returning `null` for the no-solution case** — forces callers to
  null-check. Throw `IllegalArgumentException` instead.
- **Validating with `UnsupportedOperationException`** — use
  `IllegalArgumentException`. This habit was extinguished on Days 7-16.
- **Using `int` as the map key** — Java generics need `Integer`.
  Autoboxing does it for you; just be aware of the wrapper.
