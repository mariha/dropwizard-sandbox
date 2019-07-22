package com.no-namesocial.homework;

import org.glassfish.jersey.client.oauth1.AccessToken;
import org.glassfish.jersey.client.oauth1.OAuth1ClientSupport;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/v1/twitter")
public class TwitterResource {

    private final Client client;
    private final String twitterHomeTimelineEndpoint;

    public TwitterResource(HomeworkConfiguration configuration, Client client) {
        this.client = client;
        this.twitterHomeTimelineEndpoint = "https://api.twitter.com/1.1/statuses/home_timeline.json";
    }

    public TwitterResource(String uri, Client client) {
        this.client = client;
        this.twitterHomeTimelineEndpoint = uri + "/statuses/home_timeline.json";
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
    @Produces(MediaType.APPLICATION_JSON)
    public Response fetchTimeline(@PathParam("user-id") long userId) {
        // todo from db
        String oauthToken = "946723726867030016-d7WcwvbRmJHmzRt2qVUcMktrlAfwez4";
        String oauthTokenSecret = "kIJThIl0YRI1N4vgIcPtpQpt8UTgRgejkQPzndLOwIk8y";
        AccessToken accessToken = new AccessToken(oauthToken, oauthTokenSecret);

        final Response response = request(twitterHomeTimelineEndpoint, accessToken).get();
        if (response.getStatus() != 200) {
            String errorEntity = response.hasEntity() ? response.readEntity(String.class) : null;
            // todo return http response to user and log
            throw new RuntimeException("Request to Twitter was not successful. Response code: "
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
    public Response postMessage(@PathParam("user-id") String userId, @QueryParam("message") String message) {
        return Response.noContent().build();
    }
}
