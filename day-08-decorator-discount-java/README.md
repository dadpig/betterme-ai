# Day 8 — Decorator: Stackable Discounts (Java)

Java track, day 4. Rotates back to **design patterns** after Day 7's
collections/streams detour. Today: the GoF **Decorator** pattern, applied to
the same `Cart` / discount domain you built on Day 6.

Single-folder, no Maven/Gradle, no dependencies. Plain `javac` + `java`.

## Why Decorator, why now

On Day 6 you built **Strategy**: the `Checkout` holds *one* `DiscountStrategy`
and delegates. That works for a single coupon but falls over the moment the
business says "stack the 10% sale on top of the loyalty $5-off, then apply
buy-one-get-one." Adding a new "CombinedDiscount" class for every combination
is a combinatorial explosion — exactly the problem **Decorator** solves.

Decorator lets you **compose behavior at runtime by wrapping** an object in
another object that implements the same interface. The wrapper adds something
to the wrapped object's result, then returns the combined value. Each layer
is independent, so you can chain them in any order, any depth, without
changing the base classes.

You will also fix the **two latent bugs in Day 6's `Checkout`** that the
log flagged but didn't force you to address:

1. `Checkout.finalPriceCents` did not reject a negative discount.
2. It did not cap the discount at the subtotal — a lambda strategy could
   return a value larger than the cart and produce a negative final price.

Both fixes belong here because once discounts compose, those guards matter
much more.

## Learning outcome

After this challenge you should be able to:

- Recognize the Decorator shape: a class that **implements an interface and
  also holds a reference to that same interface**, delegating to the wrapped
  instance and adding to its result.
- Distinguish **Decorator** ("add to the same operation, recursively") from
  **Strategy** ("swap one operation for another") and from **Chain of
  Responsibility** ("pass the request along until someone handles it").
- Compose discounts in arbitrary order using `static` factory methods like
  `cap(...)`, `floor(...)`, `then(...)` — the same fluent shape `Stream` and
  `Comparator` use.
- Spot the **Open/Closed** payoff in your own code: adding a new decorator
  does not require editing any existing class.

## Problem

Extend the Day 6 discount engine so multiple discounts **stack**. A composed
discount is itself a `DiscountStrategy` — so `Checkout` does not change shape,
only its guards get hardened.

You will implement:

```java
// Decorator base — wraps another DiscountStrategy and adds to its discount.
public abstract class DiscountDecorator implements DiscountStrategy { ... }

// Concrete decorators:
public final class PercentageOffDecorator   extends DiscountDecorator { ... }
public final class FixedAmountOffDecorator  extends DiscountDecorator { ... }
public final class CapDecorator             extends DiscountDecorator { ... } // ceiling on total discount
public final class MinSubtotalDecorator     extends DiscountDecorator { ... } // disables stack if subtotal < threshold
```

And the **no-op base** that decorators wrap when there is no underlying
discount yet:

```java
public final class NoDiscount implements DiscountStrategy {
    public long discountCents(Cart cart) { return 0L; }
}
```

A `Discounts` factory class provides fluent composition:

```java
DiscountStrategy stack = Discounts.none()
        .then(Discounts.percentageOff(10))
        .then(Discounts.fixedAmountOff(500))   // 500 cents = $5.00
        .then(Discounts.capAt(2000));          // never discount more than $20
```

`.then(...)` reads left-to-right: each call wraps the prior strategy.

You will also **harden `Checkout`**:

```java
public long finalPriceCents(Cart cart) {
    long subtotal = cart.subtotalCents();
    long discount = strategy.discountCents(cart);
    if (discount < 0) throw new IllegalStateException(...);     // <-- new
    long capped   = Math.min(discount, subtotal);                // <-- new
    return subtotal - capped;
}
```

### Discount rules (same domain as Day 6)

- `PercentageOffDecorator(p)` adds `floor(subtotal * p / 100)` to the wrapped
  discount, where `p` is in `[0, 100]`. `p == 0` is a valid no-op.
- `FixedAmountOffDecorator(c)` adds `c` cents, where `c >= 0`. `c == 0` is a
  valid no-op.
- `CapDecorator(maxCents)` returns `Math.min(wrapped.discountCents(cart), maxCents)`,
  where `maxCents >= 0`.
- `MinSubtotalDecorator(thresholdCents)` returns `wrapped.discountCents(cart)`
  if `cart.subtotalCents() >= thresholdCents`, otherwise returns `0`
  (the whole stack is disabled when the cart is too small).
- Invalid inputs throw **`IllegalArgumentException`**, with a message.
  Not `UnsupportedOperationException`. Not `IllegalStateException`.

## Examples

Cart subtotal = `$30.00` = `3000` cents.

| Stack                                                       | Discount | Final  |
|-------------------------------------------------------------|----------|--------|
| `none()`                                                    | `$0.00`  | `$30.00` |
| `none().then(percentageOff(10))`                            | `$3.00`  | `$27.00` |
| `none().then(percentageOff(10)).then(fixedAmountOff(500))`  | `$8.00`  | `$22.00` |
| above `.then(capAt(700))`                                   | `$7.00`  | `$23.00` |
| `none().then(fixedAmountOff(5000))`  (bigger than cart)     | `$30.00` | `$0.00`  |
| `none().then(percentageOff(10)).then(minSubtotal(10000))`   | `$0.00`  | `$30.00` |
| `percentageOff(-5)` (invalid)                               | -        | throws `IllegalArgumentException` |

Order matters: `capAt(700)` *after* the two discounts limits the total to
`$7.00`. If you flipped it (`capAt(700)` *before* the others), the cap would
apply to `$0` and become irrelevant — `then` chains, so put guards last.

## Acceptance criteria

- `DiscountDecorator` holds a `protected final DiscountStrategy wrapped` and
  has a single constructor that **rejects null**.
- Every concrete decorator's `discountCents(cart)` is **one expression**: it
  delegates to `wrapped.discountCents(cart)` and adds/caps/zeroes. No `if`
  trees with side effects, no local mutables.
- `Discounts.none()` returns a `NoDiscount` and exposes a `.then(next)`
  default method on `DiscountStrategy` itself (see Idioms below) so callers
  can chain without naming the decorator classes.
- `Checkout.finalPriceCents` is **hardened**:
  - Throws `IllegalStateException` if the strategy returns a negative discount.
  - Caps the discount at `cart.subtotalCents()`.
  - Returns a non-negative final price for every legal stack.
- All validation throws `IllegalArgumentException` (not UOE, not ISE) with
  a useful message. The Day 7 lesson sticks.
- The `Main` demo runs the seven scenarios above and prints PASS / FAIL.

Complexity target: each `discountCents` call is `O(depth)` where depth is
the number of stacked decorators — i.e. linear in stack size, no surprises.

## Stretch (optional, pick one)

- **A. JUnit 5 for real.** Bring in
  `junit-platform-console-standalone-1.10.x.jar`, drop it next to `out/`,
  write a `WhenStackedDecoratorsTest` with one `@Test` per scenario, and run
  with `java -jar junit-platform-console-standalone.jar --class-path out
  --scan-class-path`. This is the JUnit on-ramp Day 7's plan promised but
  didn't deliver.
- **B. `BuyNGetOneFree` as a decorator.** Port Day 6's `BuyNGetOneFree`
  into the new decorator world: it should be a decorator that *adds* the
  cheapest-of-every-N-items discount to whatever it wraps. This exercises
  the "decorator over an aggregating strategy" pattern.
- **C. Sealed permits.** Make `DiscountStrategy` a `sealed` interface that
  `permits NoDiscount, DiscountDecorator`, and `DiscountDecorator` itself
  `sealed permits PercentageOffDecorator, FixedAmountOffDecorator,
  CapDecorator, MinSubtotalDecorator`. Now an exhaustive `switch` on a
  strategy is a compile-time check — adding a fifth decorator without
  updating the sealed hierarchy fails to compile. Java 21 superpower.

## Idioms to apply

- **Default method on the interface for chaining.** The shape on
  `DiscountStrategy` is:

  ```java
  default DiscountStrategy then(UnaryOperator<DiscountStrategy> decorator) {
      // Reject null `decorator`, then return decorator.apply(this).
  }
  ```

  The `Discounts` factories (`percentageOff`, `fixedAmountOff`, `capAt`,
  `minSubtotal`) each return a `UnaryOperator<DiscountStrategy>` —
  literally "given the wrapped strategy, here is the decorated strategy."
  `then(...)` just applies that function with `this` as the input. This is
  the same idiom `Function.andThen`, `Predicate.and`, and
  `Comparator.thenComparing` use, lifted into decorator composition.

- **`final` fields, `protected` only when subclasses need them.**
  `wrapped` is `protected final` in `DiscountDecorator` (subclasses read it),
  but each concrete decorator's `percent` / `amountCents` / `maxCents` is
  `private final` — subclasses do not need them.

- **Validate in the constructor, then trust `final` fields.** Once the
  constructor checks `percent ∈ [0, 100]` and `amountCents >= 0`, the rest of
  the class can read those fields without re-validating.

- **Method references over lambdas where they fit:** `Math::min` is a real
  `LongBinaryOperator`.

## Anti-patterns to avoid

- Subclassing `PercentageOffDecorator` from Day 6's `PercentageOff`. The two
  hierarchies live in different folders — Day 8 is self-contained. Cross-day
  reuse is for retrospectives, not new chapters.
- A `CombinedDiscount` class that takes a `List<DiscountStrategy>` and sums
  them. That is the **Composite** pattern, which works but defeats today's
  goal of internalizing Decorator's recursive shape. If you want Composite
  later, add a fourth stretch.
- Mutable decorators (e.g. an `add(DiscountStrategy)` method on the base).
  Every decorator must be immutable once constructed.
- `if (next == null) return this;` in `then(...)`. Reject nulls loudly with
  `IllegalArgumentException`. Silent fallbacks hide bugs.
- Re-introducing `UnsupportedOperationException` for input validation. You
  just fixed this habit on Day 7 — do not regress.

## Build and run

```bash
cd /Users/tairone/personal-pocs/betterme-ai/day-08-decorator-discount-java
javac -d out src/main/java/ai/betterme/*.java
java  -cp out ai.betterme.Main
```

Time budget: 60–90 minutes for the core; +30 min for a stretch.

## Reflection (after you finish)

1. On Day 6, `Checkout` held *one* strategy and the design forced you to
   write a new class for every combination. Today, `Checkout` still holds
   *one* strategy — but you can express any combination. What did Decorator
   actually change? Where did the combinatorial complexity go?
2. `then(...)` lives on the **interface** as a default method, not on the
   `Checkout` context. Why is that the right home for it? What would go
   wrong if `Checkout` had a `chainWith(DiscountStrategy)` method instead?
3. Open the Java standard library's `java.io` package and look at
   `BufferedReader`, `InputStreamReader`, `FileReader`. Sketch in one
   sentence why that pile of "Readers wrapping Readers" is the textbook
   Decorator example.
4. Where in your code today is a decorator's behavior **order-sensitive**?
   Where is it commutative? Why does `capAt(700)` care about position but
   two `fixedAmountOff(...)` decorators do not?
