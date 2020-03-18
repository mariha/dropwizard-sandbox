package pl.wanderers.sandbox.dropwizard;

import org.glassfish.jersey.client.oauth1.AccessToken;

import java.util.Optional;

public class AccessTokenService {

    private final AccessTokenRepository repo;

    public AccessTokenService(AccessTokenRepository repo) {
        this.repo = repo;
    }

    Optional<AccessToken> getByTwitterId(long id) {
        return repo.getById(id);
    }
}
