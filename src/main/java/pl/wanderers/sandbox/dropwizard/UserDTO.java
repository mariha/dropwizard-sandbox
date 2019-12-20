package pl.wanderers.sandbox.dropwizard;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

import javax.annotation.Nullable;
import java.util.Objects;

/**
 * Entity into which twitter user is deserialized.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDTO {

    @NotEmpty
    private long id;

    @NotEmpty
    private String screenName;

    private @Nullable String profileImageUrl;

    private @Nullable String profileImageUrlHttps;

    public UserDTO() {
        // for jackson deserializer
    }

    @JsonProperty
    private void setId(long id) {
        this.id = id;
    }

    @JsonProperty("screen_name")
    public String getScreenName() {
        return screenName;
    }

    @JsonProperty("screen_name")
    private void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    @JsonProperty("profile_image")
    public String getProfileImage() {
        return profileImageUrl != null ? profileImageUrl : profileImageUrlHttps;
    }

    @JsonProperty("profile_image_url")
    private void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    @JsonProperty("profile_image_url_https")
    private void setProfileImageUrlHttps(String profileImageUrlHttps) {
        this.profileImageUrlHttps = profileImageUrlHttps;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserDTO user = (UserDTO) o;
        if (id == 0 || user.id == 0) return screenName.equals(user.screenName);
        return id == user.id && screenName.equals(user.screenName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(screenName);
    }

    @Override
    public String toString() {
        return "UserDTO{" +
                "id='" + id + '\'' +
                ", screenName='" + screenName + '\'' +
                ", profileImageUrl='" + profileImageUrl + '\'' +
                ", profileImageUrlHttps='" + profileImageUrlHttps + '\'' +
                '}';
    }
}