package com.careerradar.source.greenhouse;

import com.careerradar.source.JobSource;
import com.careerradar.source.JobSourceEntity;
import com.careerradar.source.RawJob;
import com.careerradar.source.SourceType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;

@Component
public class GreenhouseJobSource implements JobSource {

    private static final String BASE_URL =
            "https://boards-api.greenhouse.io/v1/boards";

    private final RestClient restClient;

    public GreenhouseJobSource(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder.build();
    }

    @Override
    public SourceType type() {
        return SourceType.GREENHOUSE;
    }

    @Override
    public List<RawJob> fetch(JobSourceEntity configuration) {

        String boardToken = configuration.getSourceIdentifier();

        if (boardToken == null || boardToken.isBlank()) {
            throw new IllegalArgumentException(
                    "Greenhouse sourceIdentifier must contain the board token"
            );
        }

        GreenhouseBoardResponse board =
                restClient.get()
                        .uri(BASE_URL + "/" + boardToken)
                        .retrieve()
                        .body(GreenhouseBoardResponse.class);

        String companyName =
                board != null && board.name() != null
                        ? board.name()
                        : boardToken;

        GreenhouseJobResponse response =
                restClient.get()
                        .uri(BASE_URL + "/" + boardToken + "/jobs?content=true")
                        .retrieve()
                        .body(GreenhouseJobResponse.class);

        if (response == null || response.jobs() == null) {
            return Collections.emptyList();
        }

        return response.jobs()
                .stream()
                .map(job -> toRawJob(job, companyName))
                .toList();
    }

    private RawJob toRawJob(
            GreenhouseJobResponse.GreenhouseJob job,
            String companyName) {

        String location =
                job.location() != null
                        ? job.location().name()
                        : null;

        return new RawJob(
                String.valueOf(job.id()),
                job.title(),
                job.content(),
                companyName,
                location,
                extractCountry(location),
                extractCity(location),
                job.absoluteUrl(),
                null,
                parseDateTime(job.updatedAt())
        );
    }

    private OffsetDateTime parseDateTime(String value) {

        if (value == null || value.isBlank()) {
            return null;
        }

        return OffsetDateTime.parse(value);
    }

    private String extractCountry(String location) {

        if (location == null || location.isBlank()) {
            return null;
        }

        String[] parts = location.split(",");

        if (parts.length == 0) {
            return null;
        }

        String lastPart = parts[parts.length - 1].trim();

        return lastPart.isBlank() ? null : lastPart;
    }

    private String extractCity(String location) {

        if (location == null || location.isBlank()) {
            return null;
        }

        String[] parts = location.split(",");

        return parts.length > 0
                ? parts[0].trim()
                : location.trim();
    }
}