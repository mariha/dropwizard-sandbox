package com.no-namesocial.homework;

import com.codahale.metrics.annotation.Timed;
import org.glassfish.jersey.client.oauth1.AccessToken;
import org.glassfish.jersey.client.oauth1.OAuth1ClientSupport;

import javax.validation.constraints.NotNull;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.util.List;

@Path("/v1/twitter")
@Produces(MediaType.APPLICATION_JSON)
public class TwitterResource {

    private final Client client;
    private final String twitterHomeTimelineEndpoint;
    private final String twitterUpdateStatusEndpoint;
    private final AccessTokenService accessTokenService;

    public TwitterResource(Client client, TwitterEndpoints endpoints, AccessTokenService accessTokenService) {
        this.client = client;
        this.accessTokenService = accessTokenService;
        this.twitterHomeTimelineEndpoint = endpoints.getTimelineEndpoint();
        this.twitterUpdateStatusEndpoint = endpoints.getUpdateEndpoint();
    }

    /*
    Create a REST API endpoint that fulfills the following requirements:
    + Accept a "twitter_account.id"
    - Lookup the Twitter account by id in the sqlite database
    + Use the credentials associated with that account to fetch their home timeline from Twitter's API
    - Extract the Screen Name, Text, Date, and Profile Image from each object
    - Transform the date into a Unix Timestamp
    - Render the result as a JSON response
     */
    @GET
    @Path("{user-id}/tweets")
    @Timed
    public List<ServiceTweetDTO> fetchTimeline(@PathParam("user-id") long userId) {
        final Response response = request(twitterHomeTimelineEndpoint, getAccessToken(userId)).get();
        if (response.getStatus() != 200) {
            String errorEntity = response.hasEntity() ? response.readEntity(String.class) : null;
            throw new WebApplicationException("Request to Twitter was not successful. Response code: "
                    + response.getStatus() + ", reason: " + response.getStatusInfo().getReasonPhrase()
                    + ", entity: " + errorEntity);
        }

        return response.readEntity(new GenericType<List<ServiceTweetDTO>>() {});
    }

    private AccessToken getAccessToken(long userId) {
        String message = String.format("No access token for user %d", userId);
        return accessTokenService.getByTwitterId(userId)
                .orElseThrow(() -> new WebApplicationException(message, Status.NOT_FOUND));
    }

    private Invocation.Builder request(String endpoint, AccessToken accessToken) {
        return client.target(endpoint).request()
                .property(OAuth1ClientSupport.OAUTH_PROPERTY_ACCESS_TOKEN, accessToken);
    }

    @POST
    @Path("{user-id}/tweets")
    @Timed
    public void postMessage(@PathParam("user-id") long userId, @FormParam("message") @NotNull String message) {
        // todo logging
        final Response response = request(twitterUpdateStatusEndpoint, getAccessToken(userId))
                .post(Entity.form(new Form().param("status", message)));

        if (Status.Family.familyOf(response.getStatus()) != Status.Family.SUCCESSFUL) {
            String errorEntity = response.hasEntity() ? response.readEntity(String.class) : null;
            throw new WebApplicationException("Request to Twitter was not successful. Response code: "
                    + response.getStatus() + ", reason: " + response.getStatusInfo().getReasonPhrase()
                    + ", entity: " + errorEntity, Status.BAD_REQUEST);
        }
    }
}
