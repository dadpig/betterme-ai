# Day 21 — Single Number (find the lone element via XOR)

> **Axis:** algorithms (bit manipulation — the XOR self-cancelling trick)
> **Difficulty:** easy (~30–45 min)
> **Language:** Java 21+ (no build tool, no external deps)
> **JUnit 5 setup:** same in-tree Jupiter shim as Days 14/15/16/17/18/19/20 — the on-ramp continues

## The challenge

Implement `SingleNumber.singleNumber(int[] nums)`: given a non-empty array in
which every element appears **exactly twice** except for one element that appears
**exactly once**, return that single element. (LeetCode 136.)

```
singleNumber(new int[]{2, 2, 1})       -> 1
singleNumber(new int[]{4, 1, 2, 1, 2}) -> 4
singleNumber(new int[]{7})             -> 7    (the only element)
singleNumber(new int[]{-1, -1, -3})    -> -3   (negatives work too)
singleNumber(new int[]{5, 0, 5})       -> 0    (zero works too)
singleNumber(new int[]{})              -> IllegalArgumentException (empty)
singleNumber(null)                     -> IllegalArgumentException
```

Target performance: **O(n) time, O(1) extra space**. No `HashMap` of counts, no
sorting, no `Set` — one `int` accumulator and one loop.

## Why XOR is the whole algorithm

Exclusive-or (`^`) has three properties that, combined, solve this in one pass:

- **Identity:** `x ^ 0 == x` — so `0` is the safe starting accumulator.
- **Self-inverse:** `x ^ x == 0` — every value XOR-ed with itself cancels.
- **Commutative + associative:** order does not matter — so XOR-ing the WHOLE
  array leaves only the bits that appear an odd number of times. Exactly one
  element is odd-count (appears once); every other element is even-count (twice)
  and cancels.

```
result = 0
for n in nums:
    result ^= n     // pairs cancel to 0; the lone value survives
return result
```

That is O(n) time and O(1) extra space. Contrast with "count occurrences in a map
and find the one with count 1": correct, but O(n) space and slower. XOR is the
textbook "the right primitive collapses the whole problem" lesson.

## What you write

In `src/main/java/ai/betterme/SingleNumber.java`:

- `public static int singleNumber(int[] nums)` — implement it.

The file ships with detailed step-by-step `STEP 1..STEP 4` comments, a list of
edge cases to trace by hand, and a "common bugs" section. The method body is
stubbed with `throw new UnsupportedOperationException("TODO: implement ...")` —
**replace that one line with the real logic.**

> Deliberate teaching contrast in exception types:
> `UnsupportedOperationException` in the stub = "I haven't implemented it yet";
> `IllegalArgumentException` in validation = "you gave me a bad argument".
> Note: `null` AND an empty array are both errors here (the spec guarantees a
> non-empty input). There is no lone element to return for an empty array.

## Acceptance

- All **22 tests** in `SingleNumberTest` pass.
- O(n) time, O(1) extra space (one `int` accumulator — no map, no sort, no set).
- `null` and empty input both throw `IllegalArgumentException`.
- Single-element array returns that element.
- Works for negatives, `0`, and `Integer.MIN_VALUE`/`MAX_VALUE` as the lone value.
- Does **not** mutate the input array (the test asserts it).

## Build and run

From inside this directory.

### Compile

```sh
mkdir -p out
javac -d out \
  src/main/java/ai/betterme/SingleNumber.java \
  src/test/java/org/junit/jupiter/api/function/Executable.java \
  src/test/java/org/junit/jupiter/api/Test.java \
  src/test/java/org/junit/jupiter/api/Assertions.java \
  src/test/java/ai/betterme/TestRunner.java \
  src/test/java/ai/betterme/SingleNumberTest.java
```

### Run the demo

```sh
java -cp out ai.betterme.SingleNumber
```

### Run the test suite

```sh
java -cp out ai.betterme.TestRunner
```

You should see 22 lines of `PASS` and exit code 0 once your implementation is
complete. (The coverage report for this build-tool-free setup is the `TestRunner`
summary line `22 passed, 0 failed, 22 total` — every branch of `singleNumber`,
including the null-validation throw, the empty-array throw, the XOR accumulation
loop, and the return, is exercised by the listed tests plus the two count-oracle
property tests.)

## About the JUnit 5 setup

Identical to Days 14/15/16/17/18/19/20: the test file is written **exactly as it
would be against real JUnit 5** (`import org.junit.jupiter.api.Test;`, `@Test`,
static-imported `Assertions`). The project ships a tiny in-tree shim of `Test`,
`Assertions`, and `Executable` under `src/test/java/org/junit/jupiter/api/` so it
runs offline with plain `javac`/`java`.

**Migrating to real JUnit 5** later is mechanical:

1. Delete the three shim files in `src/test/java/org/junit/jupiter/api/`.
2. Put the real Jupiter jars on the classpath (`junit-jupiter-api`,
   `junit-jupiter-engine`, `junit-platform-console-standalone`).
3. Run `java -jar junit-platform-console-standalone.jar --class-path out
   --select-class ai.betterme.SingleNumberTest`.

`SingleNumberTest.java` itself needs **zero changes**.

## Stretch goals

- **(A) Single Number II** — `singleNumberII(int[] nums)` where every element
  appears **three** times except one (LeetCode 137). The two-XOR trick no longer
  works; the clean answer counts set bits per position mod 3 (or uses two
  accumulators `ones`/`twos`). First taste of bit-counting per position.
- **(B) Single Number III** — `int[] singleNumberIII(int[] nums)` where exactly
  **two** elements appear once and all others twice (LeetCode 260). XOR everything
  to get `a ^ b`, then use the lowest set bit to partition the array into two
  groups and XOR each separately. A genuinely clever escalation.
- **(C) Real JUnit 5.** Pull the `junit-platform-console-standalone` jar and follow
  the migration steps above. Carried over from Days 14, 15, 16, 17, 18, 19, 20.

## Why this challenge today

- **Builds on Day 17 (Two-Sum):** the same "one clean single pass over the array"
  shape, but the per-element work is now a bitwise `^=` instead of a `HashMap`
  lookup — and the extra space drops from O(n) to O(1).
- **Reinforces from prior reviews:** (a) `IllegalArgumentException` for bad input
  (`null` AND empty), never `UnsupportedOperationException`; (b) do NOT mutate the
  input array (the Day 17 `nums[i] = complement` bug — there is a dedicated
  `inputArrayIsNotMutated` test today); (c) one clean single pass, no redundant
  second loop.
- **New stretch:** **bit manipulation** — the first challenge in the routine that
  reaches below the level of collections/indices to the bitwise operators
  themselves. XOR's self-cancelling property is brand-new vocabulary and the
  gateway to Single Number II/III, bitmask DP, and `n & (n - 1)` tricks.
- **Rotation:** swings the *technique* away from the recent two-pointer /
  hashmap streak (Days 15, 17, 18, 19, 20) to a fresh axis, while staying easy
  and in Java as requested.
- **Locks in `IllegalArgumentException` for validation** for the 14th challenge
  in a row (Days 7, 8, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21).

## What to watch out for (common bugs)

- **Using `|` or `&` instead of `^`** — only exclusive-or self-cancels. OR
  accumulates set bits and never clears them; AND collapses toward zero.
- **Seeding the accumulator with the wrong value** — start at `0` (the XOR
  identity). Seeding with `nums[0]` and looping from 0 cancels the first element
  out; seeding with `1` flips the answer's low bit.
- **Reaching for a `HashMap` of counts** — correct but O(n) space and misses the
  lesson. Use the XOR primitive.
- **Returning a sentinel (`-1`) for empty/null** — `-1` is a valid element, so it
  cannot signal an error. Throw `IllegalArgumentException`.
