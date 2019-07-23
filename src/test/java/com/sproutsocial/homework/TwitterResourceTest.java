package com.no-namesocial.homework;

import io.dropwizard.jersey.errors.ErrorMessage;
import io.dropwizard.testing.junit5.DropwizardClientExtension;
import io.dropwizard.testing.junit5.ResourceExtension;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.oauth1.AccessToken;
import org.glassfish.jersey.filter.LoggingFilter;
import org.joda.time.DateTime;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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

    @Path("/statuses/update.json")
    public static class TwitterStatusUpdateStub {
        @POST
        public Response updateStatus() {
            return Response.status(204).build();
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

    private static AccessTokenService accessTokenService = mock(AccessTokenService.class);

    @BeforeAll
    static void initExtensions() throws Throwable {
        remoteEndpoints = new DropwizardClientExtension(new TwitterTimelineStub(), new TwitterStatusUpdateStub());
        remoteEndpoints.before();

        TwitterEndpoints endpoints = new TwitterEndpoints(
                remoteEndpoints.baseUri().toString(),
                "/statuses/home_timeline.json",
                "/statuses/update.json"
        );

        Client client = ClientBuilder.newClient(new ClientConfig().register(LoggingFilter.class));
        resources = new ResourceExtension.Builder()
                .addResource(new TwitterResource(client, endpoints, accessTokenService))
                .build();
        resources.before();
    }

    @AfterAll
    static void deinitializeExtensions() throws Throwable {
        remoteEndpoints.after();
        resources.after();
    }

    @BeforeEach
    void initMocks() {
        when(accessTokenService.getByTwitterId(anyLong()))
            .thenReturn(Optional.of(new AccessToken("token", "secret")));
    }

    @Test
    void testGetTimeline() {
        // given
        String path = "v1/twitter/123/tweets";

        // when
        Response response = resources.target(path).request().get();

        // then
        assertThat(response.getStatus()).isEqualTo(Status.OK.getStatusCode());
        assertThat(response.getMediaType()).isEqualTo(MediaType.APPLICATION_JSON_TYPE);
    }

    @Test
    void gettingTimelineNeedsAccessToken() {
        // given
        when(accessTokenService.getByTwitterId(12345))
                .thenReturn(Optional.empty());
        String path = "v1/twitter/12345/tweets";

        // when
        Response response = resources.target(path).request().get();

        // then
        assertThat(response.getStatus()).isEqualTo(Status.NOT_FOUND.getStatusCode());
        assertThat(response.readEntity(ErrorMessage.class).getMessage()).isEqualTo("No access token for user 12345");
    }

    @Test
    void testPostTweet() {
        // given
        String path = "v1/twitter/123/tweets";

        // when
        Response response = resources.target(path)
                .queryParam("message", "Hello, world! " + DateTime.now()).request()
                .post(Entity.form((Form) null));

        // then
        assertThat(response.getStatus()).isEqualTo(Status.NO_CONTENT.getStatusCode());
    }
}