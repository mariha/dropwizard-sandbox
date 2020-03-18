package pl.wanderers.sandbox.dropwizard;

import org.glassfish.jersey.client.oauth1.AccessToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AccessTokenServiceTest {

    private AccessTokenRepository repo = mock(AccessTokenRepository.class);
    private AccessTokenService service = new AccessTokenService(repo);

    @BeforeEach
    void setUp() {
        when(repo.getById(anyLong())).thenReturn(Optional.empty());
        when(repo.getById(123L)).thenReturn(Optional.of(new AccessToken("token", "secret")));
    }

    @Test
    void getExistingToken() {
        assertThat(service.getByTwitterId(123L).isPresent()).isTrue();
    }

    @Test
    void getNonExistingToken() {
        assertThat(service.getByTwitterId(12121).isPresent()).isFalse();
    }
}