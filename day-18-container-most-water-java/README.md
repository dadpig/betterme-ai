# Day 18 — Container With Most Water (converging two-pointer)

> **Axis:** algorithms (converging two-pointer with running max + overflow safety)
> **Difficulty:** easy (~30–45 min)
> **Language:** Java 21+ (no build tool, no external deps)
> **JUnit 5 setup:** same in-tree Jupiter shim as Days 14/15/16/17 — the on-ramp continues

## The challenge

Implement `ContainerWithMostWater.maxArea(int[] heights)`: given vertical
lines where `heights[i]` is the height of the line at x-coordinate `i`,
return the MAXIMUM amount of water any pair of lines can hold with the x-axis.

The water held by the lines at indices `left` and `right` (`left < right`) is:

```
area = (right - left) * min(heights[left], heights[right])
```

Width is the horizontal distance; height is the SHORTER of the two lines
(water spills over the lower wall). Return the single max area.

```
maxArea(new int[]{1, 8, 6, 2, 5, 4, 8, 3, 7}) -> 49   (idx 1 & 8: 7 * min(8,7))
maxArea(new int[]{1, 1})                       -> 1
maxArea(new int[]{4, 3, 2, 1, 4})              -> 16   (the two 4s, width 4)
maxArea(new int[]{1, 2, 1})                     -> 2
maxArea(null)                                   -> IllegalArgumentException
maxArea(new int[]{5})                           -> IllegalArgumentException (need 2 lines)
maxArea(new int[]{-1, 2})                        -> IllegalArgumentException (negative height)
```

Target performance: **O(n) time, O(1) extra space**. Do NOT use two nested
loops — start one pointer at each end and walk them toward each other.

## Why this is a two-pointer challenge

The obvious O(n²) solution checks every pair. Correct, but quadratic.

The O(n) solution starts with the WIDEST container (a pointer at each end)
and walks inward. The width can only shrink as the pointers converge, so the
only way to beat the current container is a TALLER wall. The shorter wall is
the bottleneck, so you discard THAT one (move its pointer inward) and keep the
taller one. Moving the taller pointer can never help — the height is still
capped by the shorter wall, and the width just got smaller.

```
left = 0, right = n - 1, best = 0
while left < right:
    height = min(heights[left], heights[right])
    width  = right - left
    best   = max(best, (long) width * height)
    if heights[left] < heights[right]: left++
    else:                              right--
return best
```

Same converging-two-pointer family as Day 15 (valid palindrome), but with a
twist: instead of comparing the ends and failing fast, you COMPUTE a value at
each step and TRACK the running maximum.

## What you write

In `src/main/java/ai/betterme/ContainerWithMostWater.java`:

- `public static long maxArea(int[] heights)` — implement it.

The file ships with detailed step-by-step `STEP 1..STEP 4` comments, a list of
edge cases to trace by hand, and a "common bugs" section. The method body is
stubbed with `throw new UnsupportedOperationException("TODO: implement ...")` —
**replace that one line with the real logic.**

> Deliberate teaching contrast in exception types:
> `UnsupportedOperationException` in the stub = "I haven't implemented it
> yet"; `IllegalArgumentException` in validation = "you gave me a bad
> argument". Don't confuse the two.

## Acceptance

- All **25 tests** in `ContainerWithMostWaterTest` pass.
- O(n) time, O(1) extra space (no nested loops, no sort).
- `null` input throws `IllegalArgumentException`.
- Array of length `< 2` throws `IllegalArgumentException`.
- Any NEGATIVE height throws `IllegalArgumentException` (but `0` is VALID).
- Returns a `long` and uses `(long) width * height` so large inputs do NOT
  overflow a 32-bit `int` (one test deliberately exceeds `Integer.MAX_VALUE`).
- Always moves the SHORTER wall inward; height is `min`, never `max`.
- Does not mutate the input array.

## Build and run

From inside this directory.

### Compile

```sh
mkdir -p out
javac -d out \
  src/main/java/ai/betterme/ContainerWithMostWater.java \
  src/test/java/org/junit/jupiter/api/function/Executable.java \
  src/test/java/org/junit/jupiter/api/Test.java \
  src/test/java/org/junit/jupiter/api/Assertions.java \
  src/test/java/ai/betterme/TestRunner.java \
  src/test/java/ai/betterme/ContainerWithMostWaterTest.java
```

### Run the demo

```sh
java -cp out ai.betterme.ContainerWithMostWater
```

### Run the test suite

```sh
java -cp out ai.betterme.TestRunner
```

You should see 25 lines of `PASS` and exit code 0 once your implementation is
complete. (The coverage report for this build-tool-free setup is the
`TestRunner` summary line `25 passed, 0 failed, 25 total` — every branch of
`maxArea`, including all three validation throws and both pointer-move arms,
is exercised by the listed tests.)

## About the JUnit 5 setup

Identical to Days 14/15/16/17: the test file is written **exactly as it would
be against real JUnit 5** (`import org.junit.jupiter.api.Test;`,
`@Test`, static-imported `Assertions`). The project ships a tiny in-tree shim
of `Test`, `Assertions`, and `Executable` under
`src/test/java/org/junit/jupiter/api/` so it runs offline with plain
`javac`/`java`.

**Migrating to real JUnit 5** later is mechanical:

1. Delete the three shim files in `src/test/java/org/junit/jupiter/api/`.
2. Put the real Jupiter jars on the classpath (`junit-jupiter-api`,
   `junit-jupiter-engine`, `junit-platform-console-standalone`).
3. Run `java -jar junit-platform-console-standalone.jar --class-path out
   --select-class ai.betterme.ContainerWithMostWaterTest`.

`ContainerWithMostWaterTest.java` itself needs **zero changes**.

## Stretch goals

- **(A) Return the winning pair.** Add
  `int[] maxAreaIndices(int[] heights)` returning `{left, right}` of the best
  container (not just its area). Track the winning indices alongside `best`.
- **(B) `trappingRainWater(int[] heights)`** — the harder cousin (LeetCode 42):
  total water trapped across ALL bars, not just between two. Still two pointers,
  but you track `leftMax`/`rightMax` and accumulate. A genuine step up.
- **(C) Real JUnit 5.** Pull the `junit-platform-console-standalone` jar and
  follow the migration steps above. Carried over from Days 14, 15, 16, 17.

## Why this challenge today

- **Builds on Day 15** (valid palindrome two-pointer): you locked in the
  converging-two-pointer skeleton — `left`/`right` indices walking inward with
  an `O(1)`-space invariant. Today reuses that exact skeleton but swaps
  "compare-and-fail-fast" for "compute-and-track-a-running-max", and adds the
  decision rule "always discard the shorter wall."
- **Reinforces from Day 17 review:** keep `IllegalArgumentException` for every
  bad-input path; do NOT mutate the input array (Day 17's submitted `TwoSum`
  wrote `nums[i] = complement` mid-loop — a bug worth not repeating); and pick
  ONE clean loop shape rather than two passes.
- **New stretch:** **integer-overflow awareness** — the `(long) width * height`
  cast is the headline new idea. First challenge where a naive `int` multiply
  silently produces a WRONG (negative) answer on large inputs; one test
  deliberately exceeds `Integer.MAX_VALUE` to force the lesson.
- **Rotation:** stays on the algorithms axis but rotates the *technique* —
  Day 17 was hash-map/complement, Day 18 is converging two-pointer.
- **Difficulty calibration:** explicitly easy — single static method, ~12 lines
  of real logic, ~45 min budget.
- **Locks in `IllegalArgumentException` for validation** for the 11th challenge
  in a row (Days 7, 8, 10, 11, 12, 13, 14, 15, 16, 17, 18).

## What to watch out for (common bugs)

- **Moving the TALLER pointer** — step past the optimal container. Always move
  the SHORTER wall; it is the bottleneck.
- **Using `max` for the height** — water spills over the LOWER wall, so the
  height is `min(left, right)`, never `max`.
- **32-bit overflow** — `width * height` as an `int` can wrap negative. Cast to
  `long` BEFORE the multiply: `(long) width * height`, not
  `(long)(width * height)`.
- **Computing area after moving** — compute for the current pair first, update
  the max, THEN move. Otherwise you miss the widest container.
- **Loop bound `<=` instead of `<`** — `<=` compares a line with itself
  (width 0) and signals confusion about the invariant.
- **Validating with `UnsupportedOperationException`** — use
  `IllegalArgumentException`. Extinguished since Day 7.
