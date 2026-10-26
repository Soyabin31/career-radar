package com.careerradar.source.workable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record WorkableJobBoardResponse(
        String name,
        String description,
        List<WorkableJob> jobs
) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record WorkableJob(
            String id,
            String title,
            String department,
            @JsonProperty("workplace_type")
            String workplaceType,
            String url,
            String shortlink,
            @JsonProperty("published_on")
            String publishedOn,
            String description,
            Location location
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Location(
            @JsonProperty("location_str")
            String locationStr,
            String country,
            String city,
            String region
    ) {
    }
}
