package com.no-namesocial.homework;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.no-namesocial.homework.util.NullStringJsonDeserializer;
import io.dropwizard.jackson.Jackson;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static io.dropwizard.testing.FixtureHelpers.fixture;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

class ServiceTweetDTOTest {
    private static final ObjectMapper mapper = Jackson.newObjectMapper();

    @BeforeAll
    static void setupMapper() {
        mapper.registerModule(
                new SimpleModule().addDeserializer(String.class, new NullStringJsonDeserializer()));
        mapper.registerModule(new JodaModule());
    }

    @Test
    void deserializeJsonWithUnmappedProperties() throws Exception {
        // tweet received as a response contains many other properties then those we are interested in and are deserialized in ServiceTweetDTO
        List<ServiceTweetDTO> tweets = mapper.readValue(fixture("twitter-timeline-response.json"), new TypeReference<List<ServiceTweetDTO>>(){});

        assertThat(tweets).hasSize(1);
        assertSoftly(softly -> {
            ServiceTweetDTO tweet = tweets.get(0);

            softly.assertThat(tweet.getCreatedAt()).isEqualTo("Tue Jul 23 04:20:00 +0000 2019");
            softly.assertThat(tweet.getDate()).isEqualTo(1563855600L);
            softly.assertThat(tweet.getText()).isEqualTo("To make room for more expression, we will now count all emojis as equal—including those with gender‍‍‍ and skin t… https://t.co/MkGjXf9aXm");

            softly.assertThat(tweet.getUser().getScreenName()).isEqualTo("TwitterAPI");
            softly.assertThat(tweet.getUser().getProfileImage())
                    .isEqualTo("https://pbs.twimg.com/profile_images/942858479592554497/BbazLO9L_normal.jpg");
        });
    }

    @Test
    void serializeTweetToJson() throws IOException {
        List<ServiceTweetDTO> tweets = mapper.readValue(fixture("twitter-timeline-response.json"), new TypeReference<List<ServiceTweetDTO>>(){});

        String json = mapper.writeValueAsString(tweets);

        assertSoftly(softly -> {
            softly.assertThat(json).contains("\"date\"");
            softly.assertThat(json).contains(Long.toString(tweets.get(0).getDate()));
            softly.assertThat(json).doesNotContain("created_at");

            softly.assertThat(json).contains("\"text\"");
            softly.assertThat(json).contains(tweets.get(0).getText());

            softly.assertThat(json).contains("\"screen_name\"");
            softly.assertThat(json).contains(tweets.get(0).getUser().getScreenName());

            softly.assertThat(json).contains("\"profile_image\"");
            softly.assertThat(json).doesNotContain("profile_picture_url");
            softly.assertThat(json).doesNotContain("profile_picture_url_https");
        });
    }
}