package org.junit.jupiter.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * In-tree stand-in for {@code org.junit.jupiter.api.Test}.
 *
 * <p>This is a byte-for-byte source-compatible shim so the test file below is a
 * <em>real</em> JUnit 5 test file. To migrate to real Jupiter: delete this shim
 * package, drop {@code junit-platform-console-standalone} on the classpath, and
 * run with the JUnit launcher. No test code changes required.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Test {
}
