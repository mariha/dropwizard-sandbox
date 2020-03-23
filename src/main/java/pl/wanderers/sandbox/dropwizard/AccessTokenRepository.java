package pl.wanderers.sandbox.dropwizard;

import org.glassfish.jersey.client.oauth1.AccessToken;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;

import java.util.NoSuchElementException;
import java.util.Optional;

public class AccessTokenRepository {

    private final Jdbi jdbi;

    public AccessTokenRepository(Jdbi jdbi) {
        this.jdbi = jdbi;
    }

    Optional<AccessToken> getById(long id) {
        try (Handle handle = jdbi.open()) {
            return handle.createQuery(
                    "SELECT oauth_token, oauth_secret " +
                            "FROM twitter_accounts " +
                            "WHERE twitter_id = :id")
                    .bind("id", id)
                    .map((rs, ctx) -> new AccessToken(rs.getString("oauth_token"), rs.getString("oauth_secret")))
                    .findFirst();
        }
    }

    void insert(long id, AccessToken entity) {
        try (Handle handle = jdbi.open()) {
            int insertedRows = handle.createUpdate(
                    "INSERT INTO twitter_accounts (twitter_id, oauth_token, oauth_secret) " +
                            "VALUES (:id, :token, :secret)")
                    .bind("id", id)
                    .bind("token", entity.getToken())
                    .bind("secret", entity.getAccessTokenSecret())
                    .execute();

            if (insertedRows == 0) {
                throw new IllegalStateException(String.format("Couldn't insert access token with id=%d, has it already existed?", id));
            }
        }
    }

    void delete(long id) {
        try (Handle handle = jdbi.open()) {
            int deletedRows = handle.createUpdate(
                    "DELETE FROM twitter_accounts " +
                            "WHERE twitter_id = :id")
                    .bind("id", id)
                    .execute();

            if (deletedRows == 0) {
                throw new NoSuchElementException(String.format("Couldn't delete an entity with id=%d, was it already deleted?", id));
            }
        }
    }

    void update(long id, AccessToken entity) {
        delete(id);
        insert(id, entity);
    }
}
