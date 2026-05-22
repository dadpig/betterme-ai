package org.junit.jupiter.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Day 14 - JUnit 5 (Jupiter) on-ramp shim.
 *
 * <p>This is a minimal, source-compatible stand-in for the real
 * {@code org.junit.jupiter.api.Test} annotation. It exists so today's test
 * file is written exactly as it would be against real JUnit 5 - no custom
 * naming convention, real annotations - while keeping the project runnable
 * with plain {@code javac}/{@code java} and zero external jars.
 *
 * <p><b>Migration to real JUnit 5</b> is mechanical:
 * <ol>
 *   <li>Drop {@code junit-jupiter-api}, {@code junit-jupiter-engine},
 *       {@code junit-platform-console-standalone} jars on the classpath.</li>
 *   <li>Delete this shim file (and the matching {@code Assertions.java}).</li>
 *   <li>Replace the project-local {@code TestRunner} with
 *       {@code java -jar junit-platform-console-standalone.jar --class-path out -p ai.betterme}.</li>
 * </ol>
 *
 * <p>Source files in {@code ai.betterme} need <b>no changes</b> - the
 * import paths and annotation signatures match real JUnit 5 byte-for-byte.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Test {
}
