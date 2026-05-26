---
name: User Java skill level and idiomatic gaps
description: Calibrated Java skill profile based on day-05/05b/06 code review — what's solid, what needs work
type: user
---

Self-described level: **beginner**. Observed level after 3 Java POCs (Day 5 linear regression, Day 5b Roman numerals, Day 6 Strategy pattern): **advanced-beginner / early-intermediate**.

**What is solid:**
- Records with compact constructors and defensive copies (consumes scaffolds correctly).
- `@FunctionalInterface` + lambda usage — accepts the contract without confusion.
- Stream pipelines: `mapToLong`, `filter`, `sum`, `Collectors.toMap` all in active vocabulary.
- `Map.ofEntries(entry(...))` for immutable lookup tables.
- Package layout (`src/main/java/ai/betterme/...`) and IntelliJ `.iml` project structure.

**Idiomatic gaps to address (good targets for future challenges):**
1. **Wrong exception type for validation.** Reaches for `UnsupportedOperationException` where `IllegalArgumentException` is correct. Pattern repeats across Roman numerals and all four Discounts strategies. Surface this in any future input-validation challenge.
2. **Off-by-one validation logic.** `PercentageOff(0)` and `FixedAmountOff(0)` are rejected, but `0` is a valid no-op per the contract. Sign of treating `> 0` as a default instead of reading the spec carefully.
3. **Control flow over data.** Roman numerals `toRoman` built a `Map<Integer,String>` lookup table — then ignored it and hand-coded length-of-digits branches. The clean version walks the descending table once. Strong teaching opportunity: "let the data drive the algorithm."
4. **No tests yet.** Validates via a `Main` runner printing PASS/FAIL. Introducing JUnit 5 + a couple of `@Test` methods would be a natural next step without breaking their workflow.

**How to apply:**
- When proposing the next Java challenge, gently surface one of these gaps (don't pile them all on). Day 7 is a good moment to introduce **JUnit 5** and reinforce **data-driven iteration** while keeping conceptual load light.
- They consume scaffolded code well — keep scaffolding the structure and let them fill the logic.
- Avoid heavy generics, advanced concurrency, or framework setup. Plain `java --version 21+` from CLI is their happy path.
