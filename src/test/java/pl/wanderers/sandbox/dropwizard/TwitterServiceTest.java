package pl.wanderers.sandbox.dropwizard;

import io.dropwizard.testing.junit5.DropwizardAppExtension;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.joda.time.DateTime;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.ws.rs.core.Response;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

@ExtendWith(DropwizardExtensionsSupport.class)
class TwitterServiceTest {

    private static final DropwizardAppExtension<SandboxConfiguration> dropwizardApp =
            new DropwizardAppExtension<>(SandboxApplication.class, "config.yml"); // loads main config, does env variables substitution

    private long userId;

    @BeforeAll
    static void initEndpoint() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = dropwizardApp.getLocalPort();
        RestAssured.basePath = "/api/v1";
    }

    @BeforeEach
    void setUp() {
        userId = dropwizardApp.getConfiguration().getFunctionalUserId();

        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @SmokeTest
    void showTimeline() {
        given()
            .pathParam("user-id", userId)
        .when()
            .get("/twitter/{user-id}/tweets")
        .then()
            .statusCode(Response.Status.OK.getStatusCode())
            .contentType(ContentType.JSON);
    }

    @SmokeTest
    void tweetMessage() {
        given()
            .pathParam("user-id", userId)
            .param("message", "Hello, world! " + DateTime.now())
        .when()
            .post("/twitter/{user-id}/tweets")
        .then()
            .log().all()
            .statusCode(Response.Status.NO_CONTENT.getStatusCode());
    }

    @Test
    void tweetNeedsToHaveMessage() {
        given()
            .pathParam("user-id", userId)
        .when()
            .post("/twitter/{user-id}/tweets")
        .then()
            .log().all()
            .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
            .body("errors[0]", equalTo("form field message may not be null"));
    }

    @Test
    void tryToUpdateTweets() {
        given()
            .pathParam("user-id", userId)
            .param("message", "Hello, world!")
        .when()
            .put("/twitter/{user-id}/tweets")
        .then()
            .statusCode(Response.Status.METHOD_NOT_ALLOWED.getStatusCode());
    }

    @Test
    void tryToDeleteTweets() {
        given()
            .pathParam("user-id", userId)
        .when()
            .delete("/twitter/{user-id}/tweets")
        .then()
            .statusCode(Response.Status.METHOD_NOT_ALLOWED.getStatusCode());
    }
}