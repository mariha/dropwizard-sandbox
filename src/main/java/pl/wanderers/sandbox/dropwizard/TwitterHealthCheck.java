package pl.wanderers.sandbox.dropwizard;

import com.codahale.metrics.health.HealthCheck;
import org.glassfish.jersey.client.oauth1.AccessToken;
import org.glassfish.jersey.client.oauth1.OAuth1ClientSupport;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.Response;
import java.util.Optional;

import static javax.ws.rs.core.Response.Status.OK;
import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;

/**
 * Verifies Twitter service. The check will pass if:
 *  - twitter api service is up and the endpoints are reachable,
 *  - credentials are ok (app and user),
 *  - user authorized the app,
 *  - request rate limits are not exceeded
 */
// see: https://oauth.net/core/1.0a/#anchor9
// todo why not OAuth 2.0?
public class TwitterHealthCheck extends HealthCheck {

    // todo from config
    private static final String verificationEndpoint = "https://api.twitter.com/1.1/account/verify_credentials.json";
    private static final String rateLimitEndpoint = "https://api.twitter.com/1.1/application/rate_limit_status.json";

    private final Client client;
    private final long functionalUserId;
    private final AccessTokenService tokenService;

    public TwitterHealthCheck(Client client, long functionalUserId, AccessTokenService tokenService) {
        this.client = client;
        this.functionalUserId = functionalUserId;
        this.tokenService = tokenService;
    }

    // invalid authentication credentials (app or user) / unauthorized / app request rate limit exceeded / endpoint is down
    @Override
    protected Result check() {
        final Optional<AccessToken> accessToken = tokenService.getByTwitterId(functionalUserId);
        if (!accessToken.isPresent()) {
            return Result.unhealthy("No credentials found for functional user, health unverified.");
        }

        // request to protected resources
        final Response credentialsVerificationResponse = client.target(verificationEndpoint).request()
                .property(OAuth1ClientSupport.OAUTH_PROPERTY_ACCESS_TOKEN, accessToken.get())
                .get();
        // todo log

        if (credentialsVerificationResponse.getStatus() != OK.getStatusCode()) {
            if (credentialsVerificationResponse.getStatus() == UNAUTHORIZED.getStatusCode()) {
                return Result.unhealthy("Credentials verification failed: "+ credentialsVerificationResponse);
            } else {
                return Result.unhealthy("Check twitter API endpoints statuses: https://api.twitterstat.us/#");
            }
        }

        final Response rateLimitResponse = client.target(rateLimitEndpoint).request()
                .property(OAuth1ClientSupport.OAUTH_PROPERTY_ACCESS_TOKEN, accessToken.get())
                .get();
        // todo log

        if (rateLimitResponse.getStatus() != OK.getStatusCode()) {
            return Result.unhealthy("Requests rate limit exceeded: "+ rateLimitResponse);
        }

        return Result.healthy();
    }
}
