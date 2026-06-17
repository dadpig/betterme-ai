package org.junit.jupiter.api;

import java.util.Objects;

import org.junit.jupiter.api.function.Executable;

/**
 * In-tree stand-in for {@code org.junit.jupiter.api.Assertions}.
 *
 * <p>Method names and signatures match real Jupiter so the test file is
 * source-compatible. Only the subset used by the test suite is implemented.
 */
public final class Assertions {

    private Assertions() {
        throw new AssertionError("No instances.");
    }

    public static void assertEquals(Object expected, Object actual) {
        if (!Objects.equals(expected, actual)) {
            throw new AssertionError("expected: <" + expected + "> but was: <" + actual + ">");
        }
    }

    public static void assertEquals(Object expected, Object actual, String message) {
        if (!Objects.equals(expected, actual)) {
            throw new AssertionError(message + " ==> expected: <" + expected + "> but was: <" + actual + ">");
        }
    }

    public static void assertEquals(long expected, long actual) {
        if (expected != actual) {
            throw new AssertionError("expected: <" + expected + "> but was: <" + actual + ">");
        }
    }

    public static void assertEquals(long expected, long actual, String message) {
        if (expected != actual) {
            throw new AssertionError(message + " ==> expected: <" + expected + "> but was: <" + actual + ">");
        }
    }

    public static void assertEquals(double expected, double actual, double delta) {
        if (Double.isNaN(expected) || Double.isNaN(actual) || Math.abs(expected - actual) > delta) {
            throw new AssertionError("expected: <" + expected + "> but was: <" + actual + "> (delta " + delta + ")");
        }
    }

    public static void assertEquals(double expected, double actual, double delta, String message) {
        if (Double.isNaN(expected) || Double.isNaN(actual) || Math.abs(expected - actual) > delta) {
            throw new AssertionError(message + " ==> expected: <" + expected + "> but was: <" + actual
                    + "> (delta " + delta + ")");
        }
    }

    public static void assertTrue(boolean condition) {
        if (!condition) {
            throw new AssertionError("expected: <true> but was: <false>");
        }
    }

    public static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message + " ==> expected: <true> but was: <false>");
        }
    }

    public static void assertFalse(boolean condition) {
        if (condition) {
            throw new AssertionError("expected: <false> but was: <true>");
        }
    }

    public static void assertFalse(boolean condition, String message) {
        if (condition) {
            throw new AssertionError(message + " ==> expected: <false> but was: <true>");
        }
    }

    public static <T extends Throwable> T assertThrows(Class<T> expectedType, Executable executable) {
        try {
            executable.execute();
        } catch (Throwable actual) {
            if (expectedType.isInstance(actual)) {
                return expectedType.cast(actual);
            }
            throw new AssertionError("Unexpected exception type thrown ==> expected: <"
                    + expectedType.getName() + "> but was: <" + actual.getClass().getName() + ">");
        }
        throw new AssertionError("Expected " + expectedType.getName() + " to be thrown, but nothing was thrown.");
    }
}
