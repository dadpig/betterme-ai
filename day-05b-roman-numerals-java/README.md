# Day 5b — Roman Numerals (Java)

A short warm-down challenge after the morning's linear-regression session.
Single-file friendly, no dependencies, no build tool required.

## Problem

Implement two pure functions:

- `String toRoman(int n)` — convert `1..3999` into its Roman-numeral form.
- `int fromRoman(String s)` — parse a valid Roman numeral back into an `int`.

Both must handle the **subtractive** forms: `IV (4)`, `IX (9)`, `XL (40)`,
`XC (90)`, `CD (400)`, `CM (900)`.

### Examples

| n    | Roman      |
|------|------------|
| 1    | `I`        |
| 4    | `IV`       |
| 9    | `IX`       |
| 58   | `LVIII`    |
| 1994 | `MCMXCIV`  |
| 3999 | `MMMCMXCIX`|

## Acceptance criteria

- `toRoman` is correct for every `n` in `1..3999`.
- `fromRoman` is correct for every valid Roman numeral up to `MMMCMXCIX`.
- Out-of-range or malformed input throws `IllegalArgumentException`
  (do not return a sentinel, do not silently coerce).
- The round-trip property holds: `fromRoman(toRoman(n)) == n` for all `n in 1..3999`.
- Solution lives in a single file; idiomatic Java (records / arrays / enhanced switch are fine).

## Stretch (optional)

- Make the symbol table a `record SymbolValue(String sym, int value)` and drive both
  directions from one immutable list.
- Add a tiny self-test in `Main` that exercises the round-trip for all `1..3999`
  and prints `OK` / first failing `n`.

## Build & run

No Maven / Gradle. Plain `javac` + `java`:

```bash
cd /Users/tairone/personal-pocs/betterme-ai/day-05b-roman-numerals-java
javac -d out src/main/java/ai/betterme/*.java
java  -cp out ai.betterme.Main
```

Target: 30–45 minutes.
