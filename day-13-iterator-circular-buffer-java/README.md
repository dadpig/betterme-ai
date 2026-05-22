# Day 13 — Iterator Pattern + Circular Buffer

> **Axis:** design patterns (GoF Iterator) + a touch of data structures
> **Difficulty:** easy (~45 min)
> **Language:** Java 21+ (no build tool, no external deps)

## The challenge

Implement a fixed-capacity **circular buffer** (a.k.a. ring buffer) of generic
type `T` that overwrites the oldest element when full, and make it iterable in
**insertion order from oldest to newest** by writing your own `Iterator<T>`.

```
CircularBuffer<Integer> buf = new CircularBuffer<>(3);
buf.add(1); buf.add(2); buf.add(3);     // buffer is now [1, 2, 3]
for (int v : buf) System.out.print(v);  // prints: 1 2 3

buf.add(4);                             // overflow! 1 is evicted, 4 takes its slot
for (int v : buf) System.out.print(v);  // prints: 2 3 4

buf.add(5); buf.add(6);                 // two more overflows
for (int v : buf) System.out.print(v);  // prints: 4 5 6
```

## Why this is an Iterator-pattern challenge, not a data-structure challenge

The GoF **Iterator** pattern is the answer to one question: *how do you let
clients walk a collection without exposing its internal layout?*

A circular buffer stores its elements in a plain array, but the **logical
order** ("oldest to newest") is decoupled from the **physical layout** (where
the head pointer happens to land after a few overflows). If a client iterated
the raw array it would see the elements in the wrong order, with possibly
stale slots in the middle.

So the iterator's job is to translate physical indices to logical order. That
is the **whole pattern**: an object that holds the traversal state on behalf
of the client, so the collection itself stays a black box.

## What you write

In `src/main/java/ai/betterme/CircularBuffer.java`:

- `class CircularBuffer<T> implements Iterable<T>`
- Constructor `CircularBuffer(int capacity)`
- `void add(T element)` — appends; on overflow, overwrites the oldest
- `int size()` — number of elements currently stored
- `int capacity()` — the fixed capacity
- `boolean isEmpty()`, `boolean isFull()` — convenience flags
- `Iterator<T> iterator()` — returns a fresh iterator that walks oldest -> newest

The file ships with the fields, the constructor, `size()`, `capacity()`,
`isEmpty()`, `isFull()` **already implemented**, plus a step-by-step guide
written as in-method comments for `add(...)` and `iterator()`. Read top to
bottom, then replace the `UnsupportedOperationException` throws with the
real logic.

## Acceptance

- `CircularBufferTest` (16 named scenarios) all pass.
- `CircularBuffer<T>` implements `Iterable<T>` so it works in an enhanced
  `for (T x : buffer)` loop.
- Constructor throws `IllegalArgumentException` if `capacity <= 0`.
- `add(null)` is rejected with `IllegalArgumentException` (we don't want
  ambiguity between "empty slot" and "null element").
- The returned `Iterator<T>`:
  - Walks elements in **oldest -> newest** order.
  - `next()` on an exhausted iterator throws `NoSuchElementException` (the
    documented contract — **not** `ArrayIndexOutOfBoundsException`, **not**
    `null`).
  - `remove()` is left at its default behavior (throws
    `UnsupportedOperationException` — this is the **one** place where UOE is
    actually correct, since it really does mean "this operation is not
    supported" rather than "I haven't implemented it yet").
- Iterating an empty buffer is legal — `hasNext()` returns `false` immediately.
- O(1) `add`, O(1) `size`/`isEmpty`/`isFull`. Iteration is O(n) over the
  current elements (not the full capacity).

## Build and run

From inside this directory.

### Compile

```sh
mkdir -p out
javac -d out \
  src/main/java/ai/betterme/CircularBuffer.java \
  src/test/java/ai/betterme/TestRunner.java \
  src/test/java/ai/betterme/CircularBufferTest.java
```

### Run the demo (`main` smoke-check inside `CircularBuffer`)

```sh
java -cp out ai.betterme.CircularBuffer
```

### Run the test suite

```sh
java -cp out ai.betterme.TestRunner
```

You should see 16 lines of `PASS` and exit code 0 once your implementation is
complete. Before you implement, the non-trivial tests will FAIL with
`UnsupportedOperationException` — that is expected.

## Stretch goals

- **(A) JUnit 5 for real.** Pull `junit-platform-console-standalone` jar and
  rename `TestRunner`'s methods to be `@Test`-annotated. The assertion API
  in `TestRunner` already matches Jupiter — the swap is mechanical. (This is
  the on-ramp that has been deferred since Day 7. The iterator surface is
  small enough that today is, again, the right moment.)
- **(B) `Iterable<T>` from a snapshot.** Today's iterator reads from the live
  buffer. Add a `snapshot()` method that returns an `Iterable<T>` decoupled
  from later `add(...)` calls. Forces you to think about which state the
  iterator closes over.
- **(C) Fail-fast on concurrent modification.** Track a `modCount` field on
  the buffer, snapshot it in the iterator's constructor, and throw
  `ConcurrentModificationException` on `next()` if the buffer was modified
  since iteration started. This is exactly what `ArrayList.iterator()` does,
  and seeing it from the inside is half the lesson of the Iterator pattern.

## Why this challenge today

- **Rotation:** Day 12 was algorithms (stack). This swings back to design
  patterns. We have not yet done **Iterator** — Days 6, 8, 11 covered
  Strategy / Decorator / Observer, and Iterator is the natural next GoF.
- **Builds on Day 9 vocabulary.** You used `ArrayDeque` for iteration order
  there; here you build the same shape (FIFO walking) from scratch on top
  of a plain array. Demystifies what `ArrayDeque.iterator()` is actually
  doing under the hood.
- **Tiny, well-defined surface.** Two methods (`add`, `iterator`) plus a
  ~20-line nested iterator class. Leaves real budget for the inline comments
  and, if you want, the JUnit 5 stretch.
- **Reinforces the data-driven idiom.** The iterator's state — three ints:
  `cursor`, `remaining`, and the buffer's `head`/`size` — *is* the algorithm.
  No branching on element values, no special cases. Same lesson as Day 12's
  `CLOSER_TO_OPENER` map: let the data shape control flow.
- **Generics gentle on-ramp.** `class CircularBuffer<T>` is the simplest
  useful generic class. The one cast you need (`(T[]) new Object[capacity]`)
  is a chance to surface why Java generics are erased without making it the
  headline.
