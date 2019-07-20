package com.no-namesocial.homework;

import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import io.dropwizard.testing.junit5.ResourceExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Starts in-memory Jersey HTTP server, without opening a port or deploying the app. The tests perform all the serialization,
 * deserialization, and validation that normally happens inside of the HTTP process.
 */
@ExtendWith(DropwizardExtensionsSupport.class)
class TwitterResourceTest {

    private static Client httpClient;

    private static ResourceExtension resources = new ResourceExtension.Builder()
            .addResource(new TwitterResource(null, httpClient))
            .build();

    @Test
    void testGetTimeline() {
        // given
        String path = "v1/twitter/123/tweets";

        // when
        Response response = resources.target(path).request().get();

        // then
        assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
        assertThat(response.getMediaType()).isEqualTo(MediaType.APPLICATION_JSON_TYPE);
    }
}