package com.no-namesocial.homework;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class HomeworkConfiguration extends Configuration {

    @Valid
    @NotNull
    private String twitterConsumerKey;

    @Valid
    @NotNull
    private String twitterConsumerSecret;

    @JsonProperty("twitterConsumerKey")
    public String getTwitterConsumerKey() {
        return twitterConsumerKey;
    }

    @JsonProperty("twitterConsumerKey")
    public void setTwitterConsumerKey(String twitterConsumerKey) {
        this.twitterConsumerKey = twitterConsumerKey;
    }

    @JsonProperty("twitterConsumerSecret")
    public String getTwitterConsumerSecret() {
        return twitterConsumerSecret;
    }

    @JsonProperty("twitterConsumerSecret")
    public void setTwitterConsumerSecret(String twitterConsumerSecret) {
        this.twitterConsumerSecret = twitterConsumerSecret;
    }

    @Valid
    @NotNull
    private DataSourceFactory database = new DataSourceFactory();

    @JsonProperty("database")
    public void setDataSourceFactory(DataSourceFactory factory) {
        this.database = factory;
    }

    @JsonProperty("database")
    public DataSourceFactory getDataSourceFactory() {
        return database;
    }
}
