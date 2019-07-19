package com.no-namesocial.homework;

import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit.DropwizardAppRule;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import javax.ws.rs.core.Response;

import static io.restassured.RestAssured.given;
import static io.restassured.config.HttpClientConfig.httpClientConfig;

public class TwitterResourceTest {

    @ClassRule
    public static final DropwizardAppRule<HomeworkConfiguration> dropwizardApp =
            new DropwizardAppRule<>(HomeworkApplication.class, ResourceHelpers.resourceFilePath("config.yml"));

    @BeforeClass
    public static void initEndpoint() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = dropwizardApp.getLocalPort();
    }

    @Before
    public void setUp() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.config = RestAssured.config().httpClient(httpClientConfig().reuseHttpClientInstance());
    }

    @Test
    public void showTimeline() {

        given()
            .param("twitter_account.id", 123).
        when()
            .get("twitter/timeline").
        then()
            .statusCode(Response.Status.OK.getStatusCode())
            .contentType(ContentType.JSON);
    }

    @Test
    public void tweetMessage() {
        given()
            .param("twitter_account.id", 123)
            .param("message", "Hello, world!").
        when()
            .post("twitter/tweet").
        then()
            .statusCode(Response.Status.NO_CONTENT.getStatusCode());
    }
}