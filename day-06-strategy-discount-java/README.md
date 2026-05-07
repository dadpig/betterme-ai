# Day 6 — Strategy Pattern: Checkout Discounts (Java)

Java track, day 2. Rotates from algorithm fundamentals (Day 1: Roman numerals)
into **design patterns**. Today: GoF **Strategy**, in idiomatic modern Java.

Single-folder, no Maven/Gradle, no dependencies. Plain `javac` + `java`.

## Learning outcome

After this challenge you should be able to:

- Recognise a Strategy when you see one (and what makes it different from
  Template Method or Decorator).
- Implement Strategy two ways in Java: (a) classes implementing an interface,
  (b) `@FunctionalInterface` + lambdas / method references.
- Pick between an `enum` of strategies and a free `Map<String, Strategy>`
  registry, and explain when each fits.
- Follow the **Open/Closed Principle**: new discount kinds must be addable
  without editing existing classes.

## Problem

You are building the discount engine for a tiny checkout. A `Cart` has a list
of `LineItem(name, unitPriceCents, quantity)`. The total before discount is
the sum of `unitPriceCents * quantity` over all line items.

A `DiscountStrategy` takes a `Cart` and returns the **discount amount in cents**
(a non-negative `long`, capped at the cart subtotal — the final price never
goes below zero).

Implement at least these strategies:

1. `NoDiscount` — always returns `0`.
2. `PercentageOff(int percent)` — e.g. `10` means 10% off the subtotal.
   Reject `percent` outside `0..100` at construction time.
3. `FixedAmountOff(long amountCents)` — flat discount, capped at subtotal.
   Reject negative `amountCents`.
4. `BuyNGetOneFree(String itemName, int n)` — for every `n` units of `itemName`
   in the cart, one unit is free. (E.g. `n=2` and quantity `5` → 2 free units;
   `n=3` and quantity `2` → 0 free units.)

Then write a `Checkout` class that:

- Takes any `DiscountStrategy` in its constructor (composition, not inheritance).
- Exposes `long finalPriceCents(Cart cart)` returning `subtotal - discount`,
  never negative.

## Examples

```
Cart: [("apple", 100, 3), ("bread", 250, 2)]
Subtotal = 100*3 + 250*2 = 800

NoDiscount               -> final = 800
PercentageOff(10)        -> discount = 80,  final = 720
FixedAmountOff(150)      -> discount = 150, final = 650
FixedAmountOff(10_000)   -> discount = 800 (capped), final = 0
BuyNGetOneFree("apple", 2) -> 1 free apple = 100, final = 700
```

## Acceptance criteria

- `DiscountStrategy` is a `@FunctionalInterface` with a single
  `long discountCents(Cart cart)` method.
- All four named strategies above are implemented and validate their inputs
  in the constructor (throw `IllegalArgumentException` on bad args).
- `Checkout.finalPriceCents` never returns a negative value.
- Adding a new strategy requires **zero edits** to `Checkout`, `Cart`, or
  any existing strategy — only a new class (or lambda).
- A demo `Main` runs the four scenarios above and prints expected vs. actual.

## Stretch (optional, pick one)

- **A. Lambda-based registry.** Build a `Map<String, DiscountStrategy>` whose
  values are lambdas / method references — show the same pattern with zero
  named subclasses.
- **B. Composite strategy.** Add `CompositeDiscount(List<DiscountStrategy>)`
  that sums the discounts from its children (still capped at subtotal).
  Demonstrates that Strategy composes naturally.
- **C. Sealed hierarchy.** Make `DiscountStrategy` a `sealed interface` with
  the four implementations as `permits`. Compare ergonomics vs. the open
  functional interface — when does each fit?

## Idioms to apply

- `record LineItem(String name, long unitPriceCents, int quantity)` —
  immutable value class with built-in `equals`/`hashCode`.
- `record Cart(List<LineItem> items)` with a compact constructor that
  defensively copies the list (`List.copyOf`).
- `@FunctionalInterface` on `DiscountStrategy` so lambdas work.
- `Math.min` / `Math.max` instead of branchy clamping.
- Validate in the constructor, never in the hot path.

## Anti-patterns to avoid

- A giant `if/else` or `switch` over a `DiscountType` enum inside
  `Checkout` — that *is* the smell Strategy was invented to fix.
- Mutating `Cart` from inside a strategy. Strategies are pure functions of
  the cart.
- Using `double` for money. Stick to `long` cents.

## Build and run

```bash
cd /Users/tairone/personal-pocs/betterme-ai/day-06-strategy-discount-java
javac -d out src/main/java/ai/betterme/*.java
java  -cp out ai.betterme.Main
```

Time budget: 30–45 minutes for the core; +20–30 min if you tackle a stretch.

## Reflection (after you finish)

1. Where did the Open/Closed Principle actually pay off here? Where would it
   have broken if you had used a `switch` on a `DiscountType` enum instead?
2. When would you reach for the lambda form, and when for a named class
   implementing the interface?
3. If discounts had to be persisted to a database (so you could reload them
   on startup), which form survives serialization better — and what does
   that tell you about pattern choice in real systems?
