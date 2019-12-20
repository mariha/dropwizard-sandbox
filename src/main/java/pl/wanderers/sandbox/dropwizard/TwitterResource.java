package pl.wanderers.sandbox.dropwizard;

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

@Path("/v1/twitter/{user-id}/tweets")
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
     * - int count	optional	[0,200]	20
     *   Specifies the number of records to retrieve. The value of count is best thought of as a limit to the number of tweets to return because suspended or deleted content is removed after the count has been applied.
     * - long since_id	optional	exclusive
     *   Returns results with an ID greater than (that is, more recent than) the specified ID. There are limits to the number of Tweets which can be accessed through the API. If the limit of Tweets has occured since the since_id, the since_id will be forced to the oldest ID available.
     * - long max_id	optional	inclusive
     *   Returns results with an ID less than (that is, older than) or equal to the specified ID.
     */
    // https://developer.twitter.com/en/docs/tweets/timelines/api-reference/get-statuses-home_timeline.html
    // https://developer.twitter.com/en/docs/tweets/timelines/guides/working-with-timelines
    @GET
    @Timed
    public List<TweetDTO> fetchTimeline(@PathParam("user-id") long userId) {
        final Response response = request(twitterHomeTimelineEndpoint, getAccessToken(userId)).get();
        if (response.getStatus() != 200) {
            String errorEntity = response.hasEntity() ? response.readEntity(String.class) : null;
            throw new WebApplicationException("Request to Twitter was not successful. Response code: "
                    + response.getStatus() + ", reason: " + response.getStatusInfo().getReasonPhrase()
                    + ", answer from twitter: " + errorEntity);
        }

        return response.readEntity(new GenericType<List<TweetDTO>>() {});
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
