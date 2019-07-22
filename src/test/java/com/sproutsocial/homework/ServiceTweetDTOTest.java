package com.no-namesocial.homework;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.jackson.Jackson;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.dropwizard.testing.FixtureHelpers.fixture;
import static org.assertj.core.api.Assertions.assertThat;

class ServiceTweetDTOTest {
    private static final ObjectMapper MAPPER = Jackson.newObjectMapper();

    @Test
    public void deserializeJsonWithUnmappedProperties() throws Exception {
        // tweet received as a response contains many other properties then those we are interested in and are deserialized in ServiceTweetDTO
        List<ServiceTweetDTO> tweets = MAPPER.readValue(fixture("twitter-timeline-response.json"), new TypeReference<List<ServiceTweetDTO>>(){});

        assertThat(tweets).hasSize(1);
        assertThat(tweets.get(0).getUser().getName()).isEqualTo("Twitter API");
    }
}