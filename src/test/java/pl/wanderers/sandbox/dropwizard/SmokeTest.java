package pl.wanderers.sandbox.dropwizard;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Tag used to mark tests which build and deploy the app, and then use standard http communication to talk with clients and external
 * services. Delays, requests and responses are real, nothing is mocked or stubbed. They verify that the app integrates well with all other
 * services in the ecosystem.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Tag("smoke-test")
@Test
public @interface SmokeTest {
}
