package com.no-namesocial.homework;

import com.codahale.metrics.annotation.Timed;
import org.glassfish.jersey.client.oauth1.AccessToken;
import org.glassfish.jersey.client.oauth1.OAuth1ClientSupport;

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
    @Produces(MediaType.APPLICATION_JSON)
    public Response fetchTimeline(@PathParam("user-id") long userId) {
        final Response response = request(twitterHomeTimelineEndpoint, getAccessToken(userId)).get();
        System.out.println(response.toString());
        if (response.getStatus() != 200) {
            String errorEntity = response.hasEntity() ? response.readEntity(String.class) : null;
            throw new WebApplicationException("Request to Twitter was not successful. Response code: "
                    + response.getStatus() + ", reason: " + response.getStatusInfo().getReasonPhrase()
                    + ", entity: " + errorEntity);
        }

        final List<ServiceTweetDTO> tweets = response.readEntity(new GenericType<List<ServiceTweetDTO>>() {});
        // todo translate twitter response to our response
        System.out.println("Tweets:\n");
        for (final ServiceTweetDTO tweet : tweets) {
            System.out.println(tweet.getText());
            System.out.println("[posted by " + tweet.getUser().getName() + " at " + tweet.getCreatedAt() + "]");
        }

        return Response.ok(tweets.size(), MediaType.APPLICATION_JSON).build();
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

    /*
    Create a second endpoint that does the following:
    + Accept a "twitter_account.id"
    - Lookup the Twitter account by id in the sqlite database
    + Accept a text parameter
    - Use the credentials associated with the twitter account to send that text as a tweet using Twitter's API
     */
    @POST
    @Path("{user-id}/tweets")
    @Timed
    public void postMessage(@PathParam("user-id") long userId, @FormParam("message") String message) {
        // todo log
        final Response response = request(twitterUpdateStatusEndpoint, getAccessToken(userId))
                .post(Entity.form(new Form().param("status", message)));
        System.out.println(response.toString());

        if (Status.Family.familyOf(response.getStatus()) != Status.Family.SUCCESSFUL) {
            String errorEntity = response.hasEntity() ? response.readEntity(String.class) : null;
            throw new WebApplicationException("Request to Twitter was not successful. Response code: "
                    + response.getStatus() + ", reason: " + response.getStatusInfo().getReasonPhrase()
                    + ", entity: " + errorEntity, Status.BAD_REQUEST);
        }
    }
}
