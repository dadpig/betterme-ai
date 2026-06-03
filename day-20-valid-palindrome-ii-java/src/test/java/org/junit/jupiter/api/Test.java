package org.junit.jupiter.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Day 20 - JUnit 5 (Jupiter) on-ramp shim (continued from Day 14/15/16/17/18/19).
 *
 * <p>Minimal, source-compatible stand-in for {@code org.junit.jupiter.api.Test}.
 * The test file in this project uses real JUnit 5 import paths and the real
 * annotation name; this shim lets it run with plain {@code javac}/{@code java}
 * and zero external jars.
 *
 * <p><b>Migration to real JUnit 5</b> is the same mechanical 3-step swap
 * documented since Day 14 - delete this shim, drop Jupiter jars on the
 * classpath, swap {@code TestRunner} for {@code junit-platform-console-standalone}.
 * Source files in {@code ai.betterme} need no changes.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Test {
}
