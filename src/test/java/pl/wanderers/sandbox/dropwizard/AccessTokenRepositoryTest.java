package pl.wanderers.sandbox.dropwizard;

import io.dropwizard.jdbi3.JdbiFactory;
import io.dropwizard.testing.DropwizardTestSupport;
import org.jdbi.v3.core.Jdbi;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AccessTokenRepositoryTest {

    private static final DropwizardTestSupport<SandboxConfiguration> testSupport =
            new DropwizardTestSupport<>(SandboxApplication.class, "config.yml");

    private static AccessTokenRepository accessTokenRepository;

    @BeforeAll
    static void startTestSupport() {
        testSupport.before();

        final Jdbi jdbi = new JdbiFactory().build(
                testSupport.getEnvironment(),
                testSupport.getConfiguration().getDataSourceFactory(),
                "sqlite");
        accessTokenRepository = new AccessTokenRepository(jdbi);
    }

    @AfterAll
    static void stopTestSupport() {
        testSupport.after();
    }

    @Test
    void getExistingToken() {
        assertThat(accessTokenRepository.getById(123L).isPresent()).isTrue();
    }

    @Test
    void getNonExistingToken() {
        assertThat(accessTokenRepository.getById(12121).isPresent()).isFalse();
    }
}