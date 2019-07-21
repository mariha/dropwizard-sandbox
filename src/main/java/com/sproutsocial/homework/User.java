package com.no-namesocial.homework;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Entity into which twitter user is deserialized.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {
    @JsonProperty("name")
    private String name;

    public User() {
        // for jackson deserializer
    }

    public User(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}