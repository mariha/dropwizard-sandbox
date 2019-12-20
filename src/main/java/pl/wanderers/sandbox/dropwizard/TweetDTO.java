package pl.wanderers.sandbox.dropwizard;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;
import org.joda.time.DateTimeZone;
import org.joda.time.Instant;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Objects;

/**
 * Entity into which twitter message is deserialized.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TweetDTO {
    @NotNull
    private Date createdAt;

    @NotEmpty
    private String text;

    private @Valid
    UserDTO user;

    public TweetDTO() {
        // for jackson deserializer
    }

    // deserialization json -> java (from twitter answer)

    @JsonProperty("created_at")
    @JsonFormat(pattern = "EEE MMM dd HH:mm:ss Z yyyy")
    // example: Wed Oct 10 20:19:24 +0000 2018
    private void setCreatedAt(Date createdAt) {
        // joda DateTime after building fat jar was throwing:
        // com.fasterxml.jackson.databind.exc.MismatchedInputException: Cannot construct instance of `org.joda.time.DateTime` (although at least one Creator exists): no String-argument constructor/factory method to deserialize from String value
        this.createdAt = createdAt;
    }

    @JsonProperty("text")
    private void setText(String text) {
        this.text = text;
    }

    @JsonProperty(value = "user", required = true)
    private void setUser(UserDTO user) {
        this.user = user;
    }

    // serialization java -> json (for user response)

    @JsonProperty("date")
    public long getDate() {
        return createdAt.toInstant().getEpochSecond();
    }

    @JsonIgnore
    public String getCreatedAt() {
        return Instant.ofEpochMilli(createdAt.getTime()).toDateTime(DateTimeZone.UTC)
                .toString("EEE MMM dd HH:mm:ss Z yyyy");
        // although simpler, shifts timezones
        //return new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy").format(createdAt);
    }

    @JsonProperty("text")
    public String getText() {
        return text;
    }

    @JsonProperty("user")
    public UserDTO getUser() {
        return user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TweetDTO that = (TweetDTO) o;
        return createdAt.equals(that.createdAt) &&
                text.equals(that.text) &&
                user.equals(that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(createdAt, text, user);
    }

    @Override
    public String toString() {
        return "TweetDTO{" +
                "createdAt=" + createdAt +
                ", text='" + text + '\'' +
                ", user=" + user +
                '}';
    }
}
