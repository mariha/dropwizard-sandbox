package pl.wanderers.sandbox.dropwizard;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import io.dropwizard.configuration.ConfigurationSourceProvider;
import io.dropwizard.configuration.ConfigurationValidationException;
import io.dropwizard.configuration.YamlConfigurationFactory;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.jersey.validation.Validators;
import io.dropwizard.testing.ResourceHelpers;
import org.junit.jupiter.api.Test;

import javax.validation.Validator;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class SandboxConfigurationTest {
    private final ObjectMapper objectMapper = Jackson.newObjectMapper();
    private final Validator validator = Validators.newValidator();
    private final YamlConfigurationFactory<SandboxConfiguration> factory =
            new YamlConfigurationFactory<>(SandboxConfiguration.class, validator, objectMapper, "dw");

    @Test
    void twitterConsumerKeyIsMandatory() throws Exception {
        // given
        ConfigurationSourceProvider configProvider = ConfigProviderBuilder.minimalCorrectConfig()
                .removeProperty("twitterConsumerKey")
                .build();

        // when
        final Throwable thrown = catchThrowable(() -> factory.build(configProvider, ""));

        // then
        assertThat(thrown).isInstanceOf(ConfigurationValidationException.class)
                .hasMessageContaining("twitterConsumerKey may not be null");
    }

    @Test
    void twitterConsumerSecretIsMandatory() throws Exception {
        // given
        ConfigurationSourceProvider configProvider = ConfigProviderBuilder.minimalCorrectConfig()
                .removeProperty("twitterConsumerSecret")
                .build();

        // when
        final Throwable thrown = catchThrowable(() -> factory.build(configProvider, ""));

        // then
        assertThat(thrown).isInstanceOf(ConfigurationValidationException.class)
                .hasMessageContaining("twitterConsumerSecret may not be null");
    }

    @Test
    void twitterEndpointsContainRootUri() throws Exception {
        // given
        final File yml = new File(ResourceHelpers.resourceFilePath("config.yml"));

        // when
        final SandboxConfiguration config = factory.build(yml);

        // then
        assertThat(config.getTwitterEndpoints().getTimelineEndpoint()).isEqualTo("https://api.twitter.com/1.1/statuses/home_timeline.json");
        assertThat(config.getTwitterEndpoints().getUpdateEndpoint()).isEqualTo("https://api.twitter.com/1.1/statuses/update.json");
    }

    @Test
    void twitterRootUri_isMandatory() throws Exception {
        // given
        ConfigurationSourceProvider configProvider = ConfigProviderBuilder.minimalCorrectConfig()
                .removeProperty("rootUri")
                .build();

        // when
        final Throwable thrown = catchThrowable(() -> factory.build(configProvider, ""));

        // then
        assertThat(thrown).isInstanceOf(ConfigurationValidationException.class)
                .hasMessageContaining("twitterEndpoints.rootUri may not be empty");
    }

    @Test
    void twitterTimelineEndpoint_isMandatory() throws Exception {
        // given
        ConfigurationSourceProvider configProvider = ConfigProviderBuilder.minimalCorrectConfig()
                .removeProperty("timelineEndpoint")
                .build();

        // when
        final Throwable thrown = catchThrowable(() -> factory.build(configProvider, ""));

        // then
        assertThat(thrown).isInstanceOf(ConfigurationValidationException.class)
                .hasMessageContaining("twitterEndpoints.timelineEndpoint may not be empty");
    }

    @Test
    void twitterUpdateEndpoint_isMandatory() throws Exception {
        // given
        ConfigurationSourceProvider configProvider = ConfigProviderBuilder.minimalCorrectConfig()
                .removeProperty("updateEndpoint")
                .build();

        // when
        final Throwable thrown = catchThrowable(() -> factory.build(configProvider, ""));

        // then
        assertThat(thrown).isInstanceOf(ConfigurationValidationException.class)
                .hasMessageContaining("twitterEndpoints.updateEndpoint may not be empty");
    }

    @Test
    void dbEncryptionKey_isMandatory() throws Exception {
        // given
        ConfigurationSourceProvider configProvider = ConfigProviderBuilder.minimalCorrectConfig()
                .removeProperty("dbEncryptionKey")
                .build();

        // when
        final Throwable thrown = catchThrowable(() -> factory.build(configProvider, ""));

        // then
        assertThat(thrown).isInstanceOf(ConfigurationValidationException.class)
                .hasMessageContaining("dbEncryptionKey may not be null");
    }

    @Test
    void dbEncryptionKey_has128bits_notLess() throws Exception {
        // given
        ConfigurationSourceProvider configProvider = ConfigProviderBuilder.minimalCorrectConfig()
                .resetPropertyValue("dbEncryptionKey", "here_are15chars")
                .build();

        // when
        final Throwable thrown = catchThrowable(() -> factory.build(configProvider, ""));

        // then
        assertThat(thrown).isInstanceOf(ConfigurationValidationException.class)
                .hasMessageContaining("dbEncryptionKey has to have 128 UTF-8 encoded bits (= 16 chars * 8 bits, if only ASCII chars are used)");
    }

    @Test
    void dbEncryptionKey_has128bits_notMore() throws Exception {
        // given
        ConfigurationSourceProvider configProvider = ConfigProviderBuilder.minimalCorrectConfig()
                .resetPropertyValue("dbEncryptionKey", "here_are_17_chars")
                .build();

        // when
        final Throwable thrown = catchThrowable(() -> factory.build(configProvider, ""));

        // then
        assertThat(thrown).isInstanceOf(ConfigurationValidationException.class)
                .hasMessageContaining("dbEncryptionKey has to have 128 UTF-8 encoded bits (= 16 chars * 8 bits, if only ASCII chars are used)");
    }


    static class ConfigProviderBuilder {

        private static final String minimalCorrectConfig = "fixtures/config/minimal-correct-config.yml";
        private Stream<String> lines;

        private ConfigProviderBuilder(Stream<String> lines) {
            this.lines = lines;
        }

        public static ConfigProviderBuilder minimalCorrectConfig() throws URISyntaxException, IOException {
            return new ConfigProviderBuilder(Files.lines(Paths.get(Resources.getResource(minimalCorrectConfig).toURI())));
        }

        public ConfigurationSourceProvider build() {
            String configContents = lines.collect(Collectors.joining("\n"));
            return (ignore) -> new ByteArrayInputStream(configContents.getBytes());
        }

        public ConfigProviderBuilder removeProperty(String property) {
            lines = lines.filter(line -> !line.contains(property));
            return this;
        }

        public ConfigProviderBuilder resetPropertyValue(String property, String value) {
            lines = lines.map(line -> line.contains(property) ? line.substring(0, line.indexOf(':') + 1) + " " + value : line);
            return this;
        }

        public ConfigProviderBuilder debug() {
            lines = lines.peek(System.err::println);
            return this;
        }
    }
}