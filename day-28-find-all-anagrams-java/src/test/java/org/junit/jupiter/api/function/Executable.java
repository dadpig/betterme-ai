package org.junit.jupiter.api.function;

/**
 * Day 28 - JUnit 5 (Jupiter) on-ramp shim (continued from Day 14-27).
 *
 * <p>Source-compatible stand-in for {@code org.junit.jupiter.api.function.Executable}.
 * A functional interface for an action that may throw any {@code Throwable},
 * used by {@code Assertions.assertThrows(...)}.
 */
@FunctionalInterface
public interface Executable {
    void execute() throws Throwable;
}
