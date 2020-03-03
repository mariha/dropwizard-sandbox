package pl.wanderers.sandbox.dropwizard;

import io.dropwizard.jdbi3.JdbiFactory;
import io.dropwizard.testing.DropwizardTestSupport;
import org.jdbi.v3.core.Jdbi;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AccessTokenServiceTest {

    private static final DropwizardTestSupport<HomeworkConfiguration> testSupport =
            new DropwizardTestSupport<>(HomeworkApplication.class, "config.yml");

    private static AccessTokenService accessTokenService;

    @BeforeAll
    static void startTestSupport() {
        testSupport.before();

        final Jdbi jdbi = new JdbiFactory().build(
                testSupport.getEnvironment(),
                testSupport.getConfiguration().getDataSourceFactory(),
                "sqlite");
        accessTokenService = new AccessTokenService(jdbi);
    }

    @AfterAll
    static void stopTestSupport() {
        testSupport.after();
    }

    @Test
    void getExistingToken() {
        assertThat(accessTokenService.getByTwitterId(123L).isPresent()).isTrue();
    }

    @Test
    void getNonExistingToken() {
        assertThat(accessTokenService.getByTwitterId(12121).isPresent()).isFalse();
    }
}