package pl.wanderers.sandbox.dropwizard;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.dropwizard.client.JerseyClientConfiguration;
import io.dropwizard.db.DataSourceFactory;

import javax.annotation.Nonnegative;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.nio.charset.StandardCharsets;

public class SandboxConfiguration extends Configuration {

    @NotNull
    private String twitterConsumerKey;

    @NotNull
    private String twitterConsumerSecret;

    @NotNull
    @Size(min = 16, max = 16,
            message = "has to have 128 UTF-8 encoded bits (= 16 chars * 8 bits, if only ASCII chars are used)")
    private byte[] dbEncryptionKey;

    @Nonnegative
    private long functionalUserId;

    @NotNull
    private @Valid TwitterEndpoints twitterEndpoints = new TwitterEndpoints();

    @NotNull
    private @Valid DataSourceFactory database = new DataSourceFactory();

    @NotNull
    private @Valid JerseyClientConfiguration jerseyClient = new JerseyClientConfiguration();

    public String getTwitterConsumerKey() {
        return twitterConsumerKey;
    }

    @JsonProperty
    private void setTwitterConsumerKey(String twitterConsumerKey) {
        this.twitterConsumerKey = twitterConsumerKey;
    }

    public String getTwitterConsumerSecret() {
        return twitterConsumerSecret;
    }

    @JsonProperty
    public void setTwitterConsumerSecret(String twitterConsumerSecret) {
        this.twitterConsumerSecret = twitterConsumerSecret;
    }

    public DataSourceFactory getDataSourceFactory() {
        return database;
    }

    @JsonProperty("database")
    private void setDataSourceFactory(DataSourceFactory factory) {
        this.database = factory;
    }

    public JerseyClientConfiguration getJerseyClientConfiguration() {
        return jerseyClient;
    }

    @JsonProperty("jerseyClient")
    private void setJerseyClientConfiguration(JerseyClientConfiguration jerseyClient) {
        this.jerseyClient = jerseyClient;
    }

    public TwitterEndpoints getTwitterEndpoints() {
        return twitterEndpoints;
    }

    @JsonProperty
    private void setTwitterEndpoints(TwitterEndpoints twitterEndpoints) {
        this.twitterEndpoints = twitterEndpoints;
    }

    public long getFunctionalUserId() {
        return functionalUserId;
    }

    @JsonProperty("functionalUserId")
    private void setFunctionalUserId(long functionalUserId) {
        this.functionalUserId = functionalUserId;
    }

    public byte[] getDbEncryptionKey() {
        return dbEncryptionKey;
    }

    @JsonProperty("dbEncryptionKey")
    public void setDbEncryptionKey(String dbEncryptionKey) {
        this.dbEncryptionKey = dbEncryptionKey.getBytes(StandardCharsets.UTF_8);
    }
}
