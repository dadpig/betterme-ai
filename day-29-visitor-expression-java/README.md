# Day 29 — Visitor Pattern over an Arithmetic Expression AST (Java)

**Topic:** GoF **Visitor** pattern (behavioral) + Java 21 sealed-ADT double dispatch
**Difficulty:** intermediate · ~60–90 min for core
**Axis:** design patterns (rotating off three straight sliding-window days)

This is challenge #25 — the every-5th **spaced-revisit** slot. It re-opens the
**Day 12 BalancedBrackets** lineage ("stack-based parsing → nested expression
evaluation") but now the nested structure is a real tree you walk with a visitor
instead of a bracket stack.

## Problem

You are given a small arithmetic-expression AST — a `sealed interface Expr`
with six record node types: `Num`, `Add`, `Sub`, `Mul`, `Div`, `Neg`. **The tree
is handed to you fully built; there is no parser to write.**

Implement the **Visitor pattern** so two distinct operations can run over the
same tree without modifying the node classes for each new operation:

1. **`EvalVisitor`** — evaluates the tree to a `double`.
2. **`PrintVisitor`** — renders the tree as a fully-parenthesised infix string.

### What you implement

- **`Expr.*.accept(ExprVisitor<R>)`** — the double-dispatch hop. One line per
  record: `return visitor.visitXxx(this);`. (6 stubs)
- **`EvalVisitor`** — six `visitXxx` methods. Leaves return the value; binary
  nodes recurse into both children via `child.accept(this)` and combine;
  `visitDiv` throws `ArithmeticException` on a zero divisor. (6 stubs)
- **`PrintVisitor`** — six `visitXxx` methods producing `"(a OP b)"`,
  `"(-a)"`, and `formatNumber(value)` for leaves. (6 stubs)

### Given (do not edit)

- `Expr` (the sealed ADT, record shapes), `ExprVisitor<R>` (the visitor
  interface), `Expressions` (public API + null validation + terse factories),
  `PrintVisitor.formatNumber`, `Main`, the test suite, the Jupiter shim.

## Acceptance criteria

- All **36 tests** green (`TestRunner`).
- `Expressions.evaluate(null)` / `Expressions.print(null)` throw
  `IllegalArgumentException`. Null validation lives at the **boundary**
  (`Expressions`), NOT in each node's `accept`.
- Division by a subtree that evaluates to `0.0` throws `ArithmeticException`
  (NOT IAE, NOT UOE — it is a runtime arithmetic fact).
- `PrintVisitor` output is fully parenthesised; whole numbers print without a
  trailing `.0` (use the given `formatNumber`).
- `accept` methods stay one line each — the recursion lives in the visitors,
  not in the nodes.
- The AST is immutable; evaluating then printing the same tree must agree on
  repeated calls (idempotency) and across threads (the visitors hold no shared
  mutable state).

### Required test categories (all present, 36 tests)

happy path (eval + print) · boundary/edge (zero, zero-numerator, negative
literals, single node) · error/failure (null tree → IAE, div-by-zero →
ArithmeticException incl. nested) · idempotency (repeated eval/print agree,
eval-then-print no interference) · **concurrency** (8 threads × 200 iterations) ·
**property-based** (1000 random trees vs an independent `switch`-with-record-
patterns oracle + a parenthesis-balance structural invariant).

## Run

```bash
cd day-29-visitor-expression-java
mkdir -p out
javac -d out $(find src/main/java src/test/java -name '*.java')
java -cp out ai.betterme.TestRunner   # test suite
java -cp out ai.betterme.Main         # demo
```

**Coverage report:** the `TestRunner` summary line (`36 / 36 tests passed`) is
the pass/fail gate. For formal line+branch coverage, see stretch C (real JUnit 5
+ JaCoCo) — every production branch (the `right == 0.0` arm in `visitDiv`, every
node type, both visitors) is exercised by the 36 tests above.

## Builds on

**Day 12 BalancedBrackets** — reusing the "nested structure has to be walked
recursively" idea, now on a real tree instead of a bracket stack; **Day 11
EventBus / Day 16 Factory** sealed-interface + record ADT vocabulary.

## Reinforces

1. **`IllegalArgumentException` for null at the boundary** (not UOE, and not
   re-checked in every node) — the Day-16 "validate where the value enters"
   lesson.
2. **`UnsupportedOperationException` belongs only in the stubbed bodies** — the
   same UOE-as-"not implemented" vs IAE-as-"bad argument" contrast from Days
   10/12/13/14.
3. **Let the type system drive** — the sealed `permits` clause makes the visitor
   set complete and the stretch `switch` exhaustive without a `default`.

## New stretch

**Double dispatch via the Visitor pattern** — first time in the routine. The new
exception type is **`ArithmeticException`** for division by zero (a genuine
runtime fact, distinct from IAE/ISE/UOE). The headline contrast: classic Visitor
(virtual `accept` + visitor overloads) vs the modern Java 21 alternative — a
`switch` with **record patterns** over the sealed hierarchy (the property-test
oracle is written this way on purpose, as a preview).

## Stretch goals

- **(A) `DepthVisitor implements ExprVisitor<Integer>`** — a THIRD operation
  added with zero edits to `Expr`. This is the payoff of Visitor in one move.
- **(B) Replace the visitor with a `switch` + record patterns** evaluator and
  printer (`case Expr.Add(Expr l, Expr r) -> ...`). Contrast the two styles:
  Visitor makes adding a new *operation* cheap; the sealed `switch` makes it even
  cheaper but couples each operation to the full node set. When does each win?
- **(C) Real JUnit 5 + JaCoCo** — drop `junit-platform-console-standalone`,
  delete the `org.junit.jupiter` shim, run with the JUnit launcher, generate a
  JaCoCo line+branch coverage report. (Carried since Day 14.)

## Reflection questions

1. Visitor makes adding a new **operation** (eval, print, depth) cheap but adding
   a new **node type** expensive (every visitor must grow a method). The sealed
   `switch` has the same trade-off in the opposite shape. Which axis does your
   problem change more often — and how should that pick the style?
2. Why does null-validation live in `Expressions` and not in each `accept`? What
   would break, or just get noisier, if you moved it into the nodes?
3. `visitDiv` throws `ArithmeticException`, not `IllegalArgumentException`. Why is
   that the right type? (Hint: who is at fault — the caller, or the data?)
