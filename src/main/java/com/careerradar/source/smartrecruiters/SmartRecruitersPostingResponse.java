package com.careerradar.source.smartrecruiters;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SmartRecruitersPostingResponse(
        int totalFound,
        List<Posting> content
) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Posting(
            String id,
            String name,
            String releasedDate,
            Location location,
            TypeOfEmployment typeOfEmployment,
            Company company,
            String ref
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Location(
            String city,
            String region,
            String country,
            Boolean remote,
            Boolean hybrid,
            String fullLocation
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record TypeOfEmployment(
            String id,
            String label
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Company(
            String identifier,
            String name
    ) {
    }
}
