package com.no-namesocial.homework;

import com.codahale.metrics.health.HealthCheck;
import org.glassfish.jersey.client.oauth1.AccessToken;
import org.glassfish.jersey.client.oauth1.OAuth1ClientSupport;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.Response;

/**
 * Verifies Twitter service. The check will pass if:
 *  - twitter api service is up and the endpoints are reachable,
 *  - credentials are ok (app and user),
 *  - user authorized the app,
 *  - request rate limits are not exceeded
 */
// see: https://oauth.net/core/1.0a/
// todo why not OAuth 2.0?
public class TwitterHealthCheck extends HealthCheck {

    // todo from config
    private static final String verificationEndpoint = "https://api.twitter.com/1.1/account/verify_credentials.json";
    private static final String rateLimitEndpoint = "https://api.twitter.com/1.1/application/rate_limit_status.json";

    private final Client client;
    private final AccessToken accessToken;

    public TwitterHealthCheck(HomeworkConfiguration config, Client client) {
        this.client = client;

        // todo from config
        String oauthToken = "946723726867030016-d7WcwvbRmJHmzRt2qVUcMktrlAfwez4";
        String oauthTokenSecret = "kIJThIl0YRI1N4vgIcPtpQpt8UTgRgejkQPzndLOwIk8y";
        this.accessToken = new AccessToken(oauthToken, oauthTokenSecret);
    }

    // invalid authentication credentials (app or user) / unauthorized / app request rate limit exceeded / endpoint is down
    @Override
    protected Result check() {
        // request to protected resources
        final Response credentialsVerificationResponse = client.target(verificationEndpoint).request()
                .property(OAuth1ClientSupport.OAUTH_PROPERTY_ACCESS_TOKEN, accessToken)
                .get();

        // todo log
        System.out.println(credentialsVerificationResponse.toString());
        System.out.println(credentialsVerificationResponse.readEntity(String.class));

        if (credentialsVerificationResponse.getStatus() != Response.Status.OK.getStatusCode()) {
            return Result.unhealthy("Credentials verification failed: "+ credentialsVerificationResponse);
        }

        final Response rateLimitResponse = client.target(rateLimitEndpoint).request()
                .property(OAuth1ClientSupport.OAUTH_PROPERTY_ACCESS_TOKEN, accessToken)
                .get();

        // todo log
        System.out.println(rateLimitResponse.toString());
        System.out.println(rateLimitResponse.readEntity(String.class));

        if (rateLimitResponse.getStatus() != Response.Status.OK.getStatusCode()) {
            return Result.unhealthy("Requests rate limit exceeded: "+ rateLimitResponse);
        }

        return Result.healthy();
    }
}
