package com.no-namesocial.homework;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Entity into which twitter message is deserialized.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Tweet {
    // todo validation?

    @JsonProperty("created_at")
    private String createdAt;

    @JsonProperty("text")
    private String text;

    @JsonProperty(value = "user", required = true)
    private User user;

    public Tweet() {
        // for jackson deserializer
    }

    public Tweet(String createdAt, String text, User user) {
        this.createdAt = createdAt;
        this.text = text;
        this.user = user;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getText() {
        return text;
    }

    public User getUser() {
        return user;
    }

    // todo hashCode, equals, toString
}
