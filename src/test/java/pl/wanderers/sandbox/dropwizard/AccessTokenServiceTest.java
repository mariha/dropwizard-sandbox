package pl.wanderers.sandbox.dropwizard;

import org.apache.commons.codec.binary.Hex;
import org.glassfish.jersey.client.oauth1.AccessToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AccessTokenServiceTest {

    private AccessTokenRepository repo = mock(AccessTokenRepository.class);
    private AccessTokenService service = new AccessTokenService(repo, "Bar12345Bar12345".getBytes());

    @BeforeEach
    void setUp() {
        when(repo.getById(anyLong())).thenReturn(Optional.empty());
    }

    @Test
    void getExistingToken() {
        when(repo.getById(123L)).thenReturn(Optional.of(new AccessToken("token", Hex.encodeHexString("encrypted secret".getBytes()))));

        assertThat(service.getByTwitterId(123L).isPresent()).isTrue();
    }

    @Test
    void getNonExistingToken() {
        assertThat(service.getByTwitterId(12121).isPresent()).isFalse();
    }

    @Test
    void getByTwitterIdDecryptsSecret() {
        // given
        when(repo.getById(123L)).thenReturn(Optional.of(new AccessToken("token", Hex.encodeHexString("encrypted secret".getBytes()))));

        // when
        AccessToken readToken = service.getByTwitterId(123L).get();

        // then
        assertThat(readToken.getToken()).isEqualTo("token");
        assertThat(readToken.getAccessTokenSecret()).doesNotContain("encrypted secret");
    }

    @Test
    void saveEncryptsSecret() {
        // when
        service.save(123L, new AccessToken("token", "plain text secret"));

        // then
        ArgumentCaptor<AccessToken> argument = ArgumentCaptor.forClass(AccessToken.class);
        verify(repo).insert(eq(123L), argument.capture());
        assertThat(argument.getValue().getToken()).isEqualTo("token");
        assertThat(argument.getValue().getAccessTokenSecret()).doesNotContain("plain text secret");
    }

    @Test
    void decryptionReversesEncryption() {
        // encrypt
        AccessToken token = new AccessToken("token", "plain text secret");
        service.save(111L, token);

        ArgumentCaptor<AccessToken> argument = ArgumentCaptor.forClass(AccessToken.class);
        verify(repo).insert(eq(111L), argument.capture());
        AccessToken encryptedToken = argument.getValue();

        // decrypt
        when(repo.getById(111L)).thenReturn(Optional.of(encryptedToken));
        AccessToken decryptedToken = service.getByTwitterId(111L).get();

        // verify we got same token as we had at the beginning
        assertThat(decryptedToken.getToken()).isEqualTo(token.getToken());
        assertThat(decryptedToken.getAccessTokenSecret()).isEqualTo(token.getAccessTokenSecret());
    }
}