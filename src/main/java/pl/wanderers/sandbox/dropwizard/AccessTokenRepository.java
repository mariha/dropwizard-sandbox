package pl.wanderers.sandbox.dropwizard;

import org.glassfish.jersey.client.oauth1.AccessToken;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;

import java.util.Optional;

public class AccessTokenRepository {

    private final Jdbi jdbi;

    public AccessTokenRepository(Jdbi jdbi) {
        this.jdbi = jdbi;
    }

    Optional<AccessToken> getById(long id) {
        try (Handle handle = jdbi.open()) {
            return handle.createQuery("SELECT oauth_token, oauth_secret FROM twitter_accounts WHERE twitter_id = :id")
                    .bind("id", id)
                    .map((rs, ctx) -> new AccessToken(rs.getString("oauth_token"), rs.getString("oauth_secret")))
                    .findFirst();
        }
    }

}
