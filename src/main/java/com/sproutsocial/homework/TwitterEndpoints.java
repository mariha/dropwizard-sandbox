package com.no-namesocial.homework;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

public class TwitterEndpoints {

    @NotEmpty
    private String rootUri;

    @NotEmpty
    private String timelineEndpoint;

    @NotEmpty
    private String updateEndpoint;

    public TwitterEndpoints() {
        // for Jackson deserializer
    }

    public TwitterEndpoints(String rootUri, String timelineEndpoint, String updateEndpoint) {
        this.rootUri = rootUri;
        this.timelineEndpoint = timelineEndpoint;
        this.updateEndpoint = updateEndpoint;
    }

    @JsonProperty
    private void setRootUri(String rootUri) {
        this.rootUri = rootUri;
    }

    public String getTimelineEndpoint() {
        return rootUri + timelineEndpoint;
    }

    @JsonProperty
    private void setTimelineEndpoint(String timelineEndpoint) {
        this.timelineEndpoint = timelineEndpoint;
    }

    public String getUpdateEndpoint() {
        return rootUri + updateEndpoint;
    }

    @JsonProperty
    private void setUpdateEndpoint(String updateEndpoint) {
        this.updateEndpoint = updateEndpoint;
    }
}
