package org.junit.jupiter.api.function;

/** In-tree stand-in for {@code org.junit.jupiter.api.function.Executable}. */
@FunctionalInterface
public interface Executable {
    void execute() throws Throwable;
}
