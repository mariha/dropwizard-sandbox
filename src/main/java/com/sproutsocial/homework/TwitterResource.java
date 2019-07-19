package com.no-namesocial.homework;

import org.apache.http.client.HttpClient;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/twitter")
public class TwitterResource {

    public TwitterResource(HttpClient client) {
        // todo implement
    }

    /*
    Create a REST API endpoint that fulfills the following requirements:
    - Accept a "twitter_account.id"
    - Lookup the Twitter account by id in the sqlite database
    - Use the credentials associated with that account to fetch their home timeline from Twitter's API
    - Extract the Screen Name, Text, Date, and Profile Image from each object
    - Transform the date into a Unix Timestamp
    - Render the result as a JSON response
     */
    @GET
    @Path("timeline")
    @Produces(MediaType.APPLICATION_JSON)
    public Response fetchTimeline(@QueryParam("twitter_account.id") String twitterId) {
        return Response.ok("", MediaType.APPLICATION_JSON).build();
    }

    /*
    Create a second endpoint that does the following:
    - Accept a "twitter_account.id"
    - Lookup the Twitter account by id in the sqlite database
    - Accept a text parameter
    - Use the credentials associated with the twitter account to send that text as a tweet using Twitter's API
     */
    @POST
    @Path("tweet")
    public Response postMessage(@QueryParam("twitter_account.id") String twitterId, @QueryParam("message") String message) {
        return Response.noContent().build();
    }
}
