# Day 22 — Number of 1 Bits + Counting Bits (LeetCode 191 & 338)

Bit-manipulation, part two. Day 21 used XOR to cancel pairs; today you reach for
the single most important bit trick — `n & (n - 1)` clears the lowest set bit —
and then use it to drive a dynamic-programming table.

## The problem

Two static methods in `CountingBits`:

- `int hammingWeight(int n)` — return the number of 1-bits in `n`'s 32-bit
  two's-complement representation (popcount). Accepts ANY int. `hammingWeight(-1)`
  is `32`.
- `int[] countBits(int n)` — return an array of length `n + 1` where
  `result[i]` is the popcount of `i`, for every `i` from `0` to `n`. `n` must be
  `>= 0` (`0` is valid → `[0]`); `n < 0` throws `IllegalArgumentException`.

```
hammingWeight(0)  -> 0
hammingWeight(11) -> 3      // binary 1011
hammingWeight(-1) -> 32     // all 32 bits set

countBits(0) -> [0]
countBits(5) -> [0, 1, 1, 2, 1, 2]
countBits(-1) -> IllegalArgumentException
```

## Acceptance criteria

- All **29** tests green.
- `hammingWeight`: O(k) time (k = set-bit count) via `n & (n - 1)`, O(1) space;
  correct on negatives and `Integer.MIN_VALUE`/`MAX_VALUE` (no infinite loop).
- `countBits`: O(n) time via the recurrence `result[i] = result[i >> 1] + (i & 1)`
  (NOT `hammingWeight` in a loop), O(n) space; array length is `n + 1`.
- `countBits(n < 0)` throws `IllegalArgumentException`; `hammingWeight` validates
  nothing (every int is valid).
- `UnsupportedOperationException` lives ONLY in the stub bodies you replace.

## Mandatory test suite

Coverage target: 100% line + branch + condition on `CountingBits`. The shipped
suite (`CountingBitsTest`, 29 `@Test` methods) covers, for BOTH methods:

- **happy path** — canonical values, LeetCode examples;
- **boundary/edge** — zero, single bit, all bits (`-1`), sign bit
  (`Integer.MIN_VALUE`), `Integer.MAX_VALUE`, power-of-two boundaries, off-by-one
  array length, fresh-array-per-call;
- **error/failure** — `countBits(-1)` and `countBits(Integer.MIN_VALUE)` → IAE;
- **idempotency** — repeated calls agree; returned arrays are independent;
- **concurrency** — both methods hammered from 8 threads × 200 iterations (pure
  static functions, no shared state);
- **property-based** — `hammingWeight` vs. `Integer.bitCount` over 1000 random
  full-range ints + all 32 single-bit values; `countBits` vs. `Integer.bitCount`
  over 200 random bounds.

`Integer.bitCount` is used as the oracle deliberately: it is the JDK's *different*
(parallel bit-summing) popcount, so a shared bug cannot hide.

### Run command

```bash
cd day-22-counting-bits-java
find src -name '*.java' > sources.txt
javac -d out @sources.txt
java -cp out ai.betterme.TestRunner      # tests
java -cp out ai.betterme.CountingBits    # demo main
```

Coverage report (when you wire up JaCoCo or run under IntelliJ's coverage runner):
`out/coverage/` — both methods should hit 100% line/branch/condition; the
property-based tests exercise every arm.

## About the JUnit 5 setup

The test file is a byte-for-byte real JUnit 5 file (`org.junit.jupiter.api.Test`,
static-imported `Assertions`). It runs offline via an in-tree shim under
`src/test/java/org/junit/jupiter/api/` + the reflective `TestRunner`. Migration to
real Jupiter is the same 3-step swap documented since Day 14: delete the shim,
drop `junit-platform-console-standalone` on the classpath, swap the launcher. No
changes to `ai.betterme` sources. **(Still stretch C — 9th deferral.)**

## Builds on / Reinforces / New stretch

- **Builds on:** Day 21 Single Number — same "one clean pass, a bitwise op per
  element, O(1)/O(n) space" shape, now with `n & (n-1)` instead of `^=`.
- **Reinforces:** `IllegalArgumentException` for the bad arg (and read the spec —
  `0` is valid, don't default to `> 0`); don't mutate / don't leak shared state;
  one clean pass (no `hammingWeight`-in-a-loop crutch).
- **New stretch:** `n & (n-1)` (Brian Kernighan) + the signed-vs-unsigned shift
  trap (`>>` vs `>>>`) + a bit trick feeding a **DP recurrence**.

## Stretch goals

- **(A)** `int reverseBits(int n)` (LeetCode 190) — reverse the 32-bit order.
- **(B)** `boolean isPowerOfTwo(int n)` in one line via `n > 0 && (n & (n-1)) == 0`.
- **(C)** Real JUnit 5 — drop the standalone jar, delete the shim, swap the runner.

## Reflection

`countBits` could just call `hammingWeight(i)` for each `i`. Why is
`result[i >> 1] + (i & 1)` strictly better, and what general DP principle does it
illustrate (hint: what subproblem does `i >> 1` name)?
