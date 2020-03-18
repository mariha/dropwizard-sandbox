package pl.wanderers.sandbox.dropwizard;

import org.glassfish.jersey.client.oauth1.AccessToken;

import java.util.Optional;

public class AccessTokenService {

    private final AccessTokenRepository repo;
    private final byte[] dbEncryptionKey;

    public AccessTokenService(AccessTokenRepository repo, byte[] dbEncryptionKey) {
        this.repo = repo;
        this.dbEncryptionKey = dbEncryptionKey;
    }

    Optional<AccessToken> getByTwitterId(long id) {
        return repo.getById(id);
    }
}
