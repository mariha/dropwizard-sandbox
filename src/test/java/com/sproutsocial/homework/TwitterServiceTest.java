package com.no-namesocial.homework;

import io.dropwizard.testing.ResourceHelpers;
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
import static io.restassured.config.HttpClientConfig.httpClientConfig;

@ExtendWith(DropwizardExtensionsSupport.class)
class TwitterServiceTest {

    private static final DropwizardAppExtension<HomeworkConfiguration> dropwizardApp =
            new DropwizardAppExtension<>(HomeworkApplication.class, ResourceHelpers.resourceFilePath("config.yml"));

    @BeforeAll
    static void initEndpoint() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = dropwizardApp.getLocalPort();
        RestAssured.basePath = "/api/v1";
    }

    @BeforeEach
    void setUp() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.config = RestAssured.config().httpClient(httpClientConfig().reuseHttpClientInstance());
    }

    @SmokeTest
    void showTimeline() {

        given()
            .pathParam("user-id", 123)
        .when()
            .get("/twitter/{user-id}/tweets")
        .then()
            .statusCode(Response.Status.OK.getStatusCode())
            .contentType(ContentType.JSON);
    }

    @SmokeTest
    void tweetMessage() {
        given()
            .pathParam("user-id", 2305278770L)
            .param("message", "Hello, world! " + DateTime.now())
        .when()
            .post("/twitter/{user-id}/tweets")
        .then()
            .log().all()
            .statusCode(Response.Status.NO_CONTENT.getStatusCode());
    }

    @Test
    void tryToUpdateTweets() {
        given()
            .pathParam("user-id", 2305278770L)
            .param("message", "Hello, world!")
        .when()
            .put("/twitter/{user-id}/tweets")
        .then()
            .statusCode(Response.Status.METHOD_NOT_ALLOWED.getStatusCode());
    }

    @Test
    void tryToDeleteTweets() {
        given()
            .pathParam("user-id", 2305278770L)
        .when()
            .delete("/twitter/{user-id}/tweets")
        .then()
            .statusCode(Response.Status.METHOD_NOT_ALLOWED.getStatusCode());
    }
}