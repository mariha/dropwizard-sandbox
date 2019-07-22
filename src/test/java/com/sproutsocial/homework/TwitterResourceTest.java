package com.no-namesocial.homework;

import io.dropwizard.testing.junit5.DropwizardClientExtension;
import io.dropwizard.testing.junit5.ResourceExtension;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Starts in-memory Jersey HTTP server, without opening a port or deploying the app. The tests perform all the serialization,
 * deserialization, and validation that normally happens inside of the HTTP process.
 *
 * Also starts another in-memory server with stubbed twitter endpoints.
 */
class TwitterResourceTest {

    @Path("/statuses/home_timeline.json")
    public static class TwitterTimelineStub {
        @GET
        @Produces(MediaType.APPLICATION_JSON)
        public List<ServiceTweetDTO> homeTimeline() {
            return Collections.emptyList();
        }
    }

    /*
     * dropwizard extensions for unit testing
     * <p/>
     * 'resources' constructor depends on 'remoteEndpoint' construction and initialization done by #before() method.
     * JUnit engine instrumented with @ExtendWith(DropwizardExtensionsSupport.class) creates both of them first and then calls before()
     * methods. Because of this, we manage lifecycle by ourselves in @BeforeAll and @AfterAll methods.
     */
    private static DropwizardClientExtension remoteEndpoints;
    private static ResourceExtension resources;

    @BeforeAll
    static void initExtensions() throws Throwable {
        remoteEndpoints = new DropwizardClientExtension(new TwitterTimelineStub());
        remoteEndpoints.before();

        resources = new ResourceExtension.Builder()
                .addResource(new TwitterResource(remoteEndpoints.baseUri().toString(), ClientBuilder.newClient()))
                .build();
        resources.before();
    }

    @AfterAll
    static void deinitializeExtensions() throws Throwable {
        remoteEndpoints.after();
        resources.after();
    }

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