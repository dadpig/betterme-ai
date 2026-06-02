# Day 19 — 3-Sum (sort + converging two-pointer + dedup)

> **Axis:** algorithms (sort + converging two-pointer + duplicate skipping)
> **Difficulty:** intermediate (~60–75 min)
> **Language:** Java 21+ (no build tool, no external deps)
> **JUnit 5 setup:** same in-tree Jupiter shim as Days 14/15/16/17/18 — the on-ramp continues

## The challenge

Implement `ThreeSum.threeSum(int[] nums)`: return **all unique triplets**
`[a, b, c]` such that `a + b + c == 0`. No duplicate triplets in the result,
and within each triplet the values are in non-decreasing order.

```
threeSum(new int[]{-1, 0, 1, 2, -1, -4}) -> [[-1, -1, 2], [-1, 0, 1]]
threeSum(new int[]{0, 0, 0})              -> [[0, 0, 0]]
threeSum(new int[]{0, 0, 0, 0})           -> [[0, 0, 0]]   (still ONE triplet, deduped)
threeSum(new int[]{1, 2, 3})              -> []
threeSum(new int[]{})                     -> []
threeSum(null)                            -> IllegalArgumentException
```

Target performance: **O(n²) time, O(n) extra space**. Do NOT use three nested
loops (O(n³)). Do NOT use a `Set` of triplets as your dedup crutch — let the
sorted order do the deduping.

## Why this is a sort + two-pointer challenge

Brute force is three nested loops — O(n³) — try every triplet and keep the
zero-sum ones, then dedupe. Correct, but cubic and clumsy.

The O(n²) solution **sorts first**, then fixes one element (the *anchor*) with
an outer loop and runs a **converging two-pointer** scan over the sorted suffix
to find the other two:

```
sort(nums)
for i in 0 .. n - 3:
    if i > 0 and nums[i] == nums[i - 1]: continue   // skip duplicate anchor
    left = i + 1, right = n - 1
    while left < right:
        sum = nums[i] + nums[left] + nums[right]
        if sum < 0:      left++          // too small -> need a bigger value
        elif sum > 0:    right--         // too large -> need a smaller value
        else:
            record [nums[i], nums[left], nums[right]]
            left++; right--
            while left < right and nums[left]  == nums[left - 1]:  left++   // skip dup
            while left < right and nums[right] == nums[right + 1]: right--  // skip dup
return result
```

Sorting buys two superpowers: (1) the **converging two-pointer** search (same
skeleton as Day 18 container-with-most-water, now comparing a running *sum* to a
target instead of tracking a max area), and (2) **cheap deduplication** —
duplicates sit next to each other, so you skip them by comparing to the
neighbour, no `Set` required.

## What you write

In `src/main/java/ai/betterme/ThreeSum.java`:

- `public static List<List<Integer>> threeSum(int[] nums)` — implement it.

The file ships with detailed step-by-step `STEP 1..STEP 4` comments, a list of
edge cases to trace by hand, and a "common bugs" section. The method body is
stubbed with `throw new UnsupportedOperationException("TODO: implement ...")` —
**replace that one line with the real logic.**

> Deliberate teaching contrast in exception types:
> `UnsupportedOperationException` in the stub = "I haven't implemented it yet";
> `IllegalArgumentException` in validation = "you gave me a bad argument".
> Note: only `null` is an error here. Empty / short arrays return an empty list.

## Acceptance

- All **26 tests** in `ThreeSumTest` pass.
- O(n²) time, O(n) extra space (no three nested loops, no `Set`-of-triplets dedup).
- `null` input throws `IllegalArgumentException`.
- Empty array and arrays of length `< 3` return an empty list (NOT an error).
- Result contains no duplicate triplets; each triplet sums to zero; each triplet
  is in non-decreasing order.
- Does **not** mutate the input array (sort a `clone()`, not the original).

## Build and run

From inside this directory.

### Compile

```sh
mkdir -p out
javac -d out \
  src/main/java/ai/betterme/ThreeSum.java \
  src/test/java/org/junit/jupiter/api/function/Executable.java \
  src/test/java/org/junit/jupiter/api/Test.java \
  src/test/java/org/junit/jupiter/api/Assertions.java \
  src/test/java/ai/betterme/TestRunner.java \
  src/test/java/ai/betterme/ThreeSumTest.java
```

### Run the demo

```sh
java -cp out ai.betterme.ThreeSum
```

### Run the test suite

```sh
java -cp out ai.betterme.TestRunner
```

You should see 26 lines of `PASS` and exit code 0 once your implementation is
complete. (The coverage report for this build-tool-free setup is the
`TestRunner` summary line `26 passed, 0 failed, 26 total` — every branch of
`threeSum`, including the null-validation throw, the empty/short early exit, both
non-zero pointer-move arms, the zero-sum record path, and both inner
duplicate-skip loops, is exercised by the listed tests plus the two brute-force
property oracles.)

## About the JUnit 5 setup

Identical to Days 14/15/16/17/18: the test file is written **exactly as it
would be against real JUnit 5** (`import org.junit.jupiter.api.Test;`, `@Test`,
static-imported `Assertions`). The project ships a tiny in-tree shim of `Test`,
`Assertions`, and `Executable` under `src/test/java/org/junit/jupiter/api/` so
it runs offline with plain `javac`/`java`.

**Migrating to real JUnit 5** later is mechanical:

1. Delete the three shim files in `src/test/java/org/junit/jupiter/api/`.
2. Put the real Jupiter jars on the classpath (`junit-jupiter-api`,
   `junit-jupiter-engine`, `junit-platform-console-standalone`).
3. Run `java -jar junit-platform-console-standalone.jar --class-path out
   --select-class ai.betterme.ThreeSumTest`.

`ThreeSumTest.java` itself needs **zero changes**.

## Stretch goals

- **(A) `kSum` generalization.** Refactor into a recursive `kSum(nums, target, k)`
  where the base case `k == 2` is the two-pointer scan and larger `k` peels one
  anchor and recurses. 3-Sum and 4-Sum then fall out of the same engine.
- **(B) `threeSumClosest(int[] nums, int target)`** (LeetCode 16) — same sort +
  two-pointer skeleton, but track the triplet whose sum is *closest* to `target`
  instead of exactly zero. A small twist on the comparison.
- **(C) Real JUnit 5.** Pull the `junit-platform-console-standalone` jar and
  follow the migration steps above. Carried over from Days 14, 15, 16, 17, 18.

## Why this challenge today

- **Builds on Day 17 (Two-Sum) + Day 18 (container two-pointer):** 3-Sum is the
  canonical fusion — fix an anchor (the Two-Sum "find the other values" idea) and
  solve the remaining Two-Sum with the converging two-pointer skeleton you locked
  in on Day 18. First challenge that *combines* two previously-learned techniques.
- **Reinforces from prior reviews:** keep `IllegalArgumentException` for the bad
  input (`null`), but do NOT over-validate — empty/short arrays are valid (the
  "read the spec, don't default to `> 0`" lesson from Day 6); and do NOT mutate
  the input array — sort a `clone()` (Day 17's submitted `TwoSum` mutated its
  input; there is a dedicated `inputArrayIsNotMutated` test today).
- **New stretch:** **duplicate skipping on sorted data** is the genuinely new
  idea — three separate dedup guards (anchor, left-after-hit, right-after-hit),
  each comparing to a neighbour rather than reaching for a `Set`. This is the
  "let the data drive the algorithm" idiom (Roman numerals → brackets) applied to
  deduplication for the first time.
- **Rotation:** stays on the algorithms axis but escalates from *easy* (Days 17,
  18) to *intermediate* — the difficulty curve climbs after two quick-win days.
- **Locks in `IllegalArgumentException` for validation** for the 12th challenge
  in a row (Days 7, 8, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19).
- **Spaced-revisit hook:** Day 20 (next session) is the every-5th-challenge
  revisit slot — strong candidate is re-opening Day 12 BalancedBrackets or
  Day 15 Palindrome as a harder variant or a Scala port.

## What to watch out for (common bugs)

- **Mutating the caller's array** — `Arrays.sort` is in-place. Sort a `clone()`.
- **Forgetting the duplicate-anchor skip** — emits the same triplet once per
  repeated anchor.
- **Forgetting the inner duplicate skips after a hit** — `[0,0,0,0]` yields
  `[0,0,0]` multiple times.
- **Skipping inner duplicates too early** (before recording + stepping) — drops
  valid distinct triplets.
- **Moving the wrong pointer on a non-zero sum** — too small → `left++`; too
  large → `right--`. Swap them and you converge to nothing.
- **Loop bound `<=` instead of `<`** — lets a single element pair with itself.
- **Validating with `UnsupportedOperationException`, or rejecting empty/short
  arrays** — use `IllegalArgumentException` only for `null`; empty/short is valid.
```
