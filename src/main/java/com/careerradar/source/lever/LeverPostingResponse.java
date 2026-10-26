package com.careerradar.source.lever;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record LeverPostingResponse(
        String id,
        String text,
        Long createdAt,
        String description,
        String descriptionPlain,
        String descriptionBody,
        String descriptionBodyPlain,
        String hostedUrl,
        String applyUrl,
        Categories categories,
        String workplaceType,
        String country
) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Categories(
            String commitment,
            String location,
            String team,
            String department,
            List<String> allLocations
    ) {
    }
}
