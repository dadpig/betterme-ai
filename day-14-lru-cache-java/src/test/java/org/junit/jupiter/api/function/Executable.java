package org.junit.jupiter.api.function;

/**
 * Day 14 - JUnit 5 (Jupiter) on-ramp shim.
 *
 * <p>Source-compatible stand-in for {@code org.junit.jupiter.api.function.Executable}.
 * A functional interface for an action that may throw any {@code Throwable},
 * used by {@code Assertions.assertThrows(...)}.
 *
 * <p>Signature matches real Jupiter so {@code assertThrows(IAE.class, () -> cache.get(null))}
 * compiles identically against either this shim or the real library.
 */
@FunctionalInterface
public interface Executable {
    void execute() throws Throwable;
}
