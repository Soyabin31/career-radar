package com.careerradar.source.ashby;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AshbyJobBoardResponse(
        String apiVersion,
        List<AshbyJob> jobs
) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record AshbyJob(
            String id,
            String title,
            String department,
            String location,
            @JsonProperty("isRemote")
            Boolean isRemote,
            String jobUrl,
            String applyUrl,
            String publishedAt,
            String employmentType,
            String descriptionHtml,
            String descriptionPlain
    ) {
    }
}
