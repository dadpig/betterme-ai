# Day 5 — Linear Regression from Scratch (Java)

**Date:** 2026-05-05
**Language:** Java 21+ (records, sealed types, switch expressions, streams)
**Topic:** Algorithms / Paradigms — gradient descent, OOP design for numeric code
**Time budget:** ~2 hours (45-min fallback at the bottom)

---

## Concept recap (10–15 min reading)

Linear regression with one or more features:

- Hypothesis: `h(x) = w · x + b` (weights vector `w`, scalar bias `b`)
- Loss (MSE): `J(w,b) = (1/2n) Σ (h(x_i) − y_i)²`
- Gradients:
  - `∂J/∂w_j = (1/n) Σ (h(x_i) − y_i) · x_ij`
  - `∂J/∂b   = (1/n) Σ (h(x_i) − y_i)`
- Update rule (batch gradient descent): `θ ← θ − α · ∂J/∂θ`

Key questions to answer for yourself:

1. Why divide the loss by `2n` instead of `n`? (Hint: gradient cleanup.)
2. What does the learning rate `α` control, and what fails if it's too large vs too small?
3. How do feature scaling and feature normalization affect convergence?

Canonical reference: Andrew Ng CS229 notes Part I §1.1–1.2, or ISLR Chapter 3.

---

## Today's challenge

Implement **batch gradient descent linear regression from scratch** in idiomatic modern Java. No external numeric libraries (no ND4J, no EJML, no Breeze). Use only `java.lang`, `java.util`, `java.util.stream`.

### Acceptance criteria

1. **Correctness.** On a synthetic dataset `y = 3·x + 7 + ε`, the trained model recovers `w ≈ 3` and `b ≈ 7` (within tolerance `0.1`) given enough epochs.
2. **Multivariate.** The same code handles `n` features, not just one — `w` is a vector.
3. **Idiomatic Java.** At minimum:
   - A `record Dataset(double[][] xs, double[] ys)` (or equivalent) — immutable input.
   - A `record Model(double[] weights, double bias)` returned from training — immutable trained state.
   - A `record TrainingConfig(double learningRate, int epochs, double tolerance)` — no method with 5 raw `double` params.
   - A `LinearRegression` class (or sealed interface + impl) exposing `train(Dataset, TrainingConfig) -> Model` and `predict(Model, double[]) -> double`.
   - At least one use of the **Streams API** in a non-trivial way (e.g., loss aggregation, prediction batch).
4. **Observability.** Print loss every `K` epochs (configurable). Stop early if `|loss_prev − loss_curr| < tolerance`.
5. **Tests.** A simple `main` or JUnit test that:
   - Generates a synthetic dataset (univariate).
   - Trains and asserts recovered parameters within tolerance.
   - Bonus: a 3-feature synthetic dataset asserting all weights recovered.

### Edge cases to think about

- Empty dataset → throw `IllegalArgumentException` early.
- Mismatched row counts between `xs` and `ys`.
- NaN/Inf appearing in the loss (sign of divergence — learning rate too high). Detect and fail loudly.
- Single feature vs multi-feature should not require two code paths.

### Stretch goals (pick 0–2 if you have time)

- **A. Pluggable optimizer (Strategy pattern).** Introduce a sealed interface `Optimizer` with implementations `BatchGradientDescent` and `StochasticGradientDescent`. Train accepts an `Optimizer`. Bonus points for using a `sealed` hierarchy and exhaustive pattern-matching `switch`.
- **B. Feature normalization.** Add a `Normalizer` (mean/std per column) that transforms a `Dataset`. Show convergence speedup on a feature with very different scale.
- **C. Concurrent gradient computation.** Parallelize the per-sample gradient sum using `IntStream.parallel()` or a `ForkJoinPool`. Benchmark vs sequential on a 100k-row dataset and reflect on whether it actually helps for this workload (it usually doesn't until `n` is large — that's the lesson).

### Idioms to apply (Java 21+)

- `record` for immutable data carriers
- `sealed interface` for closed hierarchies (e.g., optimizer)
- Pattern-matching `switch` for dispatching on optimizer type
- `Stream` / `IntStream` for aggregations — but be honest about when a plain `for` is clearer
- Throw `IllegalArgumentException` with helpful messages for bad input
- Use `Math.fma` for `w*x + b` if you want to be cheeky about numerical accuracy

---

## Suggested file layout

```
day-05-linear-regression-java/
├── README.md                               (this file)
├── src/main/java/ai/betterme/linreg/
│   ├── Dataset.java                        record
│   ├── Model.java                          record
│   ├── TrainingConfig.java                 record
│   ├── LinearRegression.java               core algorithm
│   └── Main.java                           demo / synthetic-data driver
└── src/test/java/ai/betterme/linreg/
    └── LinearRegressionTest.java           JUnit (optional — `Main` asserts also fine)
```

You can compile with plain `javac` (no Maven/Gradle needed for a single-day POC):

```bash
cd day-05-linear-regression-java
find src/main/java -name '*.java' | xargs javac -d out
java -cp out ai.betterme.linreg.Main
```

If you prefer Maven/Gradle, that's fine too — but don't let build setup eat the session.

---

## Reflection prompt (5 min, end of session)

Pick one to journal on:

1. You just wrote linear regression in Java. Where did the **type system** help you catch a bug or clarify intent? Where did it just feel like ceremony compared to how you'd write this in Rust or Scala?
2. If you did stretch A (Strategy pattern for optimizer), what would the equivalent shape look like in Scala (sealed trait + case classes) or Rust (enum + match)? Which feels most natural for this problem?
3. Gradient descent has two knobs that fight each other: learning rate and epochs. What did you observe when you cranked α up by 10x? Down by 10x?

---

## 45-minute fallback (if today is short)

Skip stretch goals. Skip multivariate. Just:

1. Univariate `y = wx + b` with hard-coded synthetic data in `main`.
2. One `record Model(double w, double b)`.
3. 1000 epochs, learning rate `0.01`, print loss every 100 epochs.
4. Assert `|w − 3| < 0.1 && |b − 7| < 0.1` at the end.

That alone exercises records, gradient descent, and gives you a baseline to extend tomorrow.
