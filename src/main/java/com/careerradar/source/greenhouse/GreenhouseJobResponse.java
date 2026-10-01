package com.careerradar.source.greenhouse;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record GreenhouseJobResponse(
        List<GreenhouseJob> jobs,
        Meta meta
) {

    public record Meta(
            Integer total
    ) {
    }

    public record GreenhouseJob(
            Long id,

            @JsonProperty("internal_job_id")
            Long internalJobId,

            String title,

            @JsonProperty("updated_at")
            String updatedAt,

            Location location,

            @JsonProperty("absolute_url")
            String absoluteUrl,

            String content
    ) {
    }

    public record Location(
            String name
    ) {
    }
}