package com.careerradar.source.ashby;

import com.careerradar.source.JobSource;
import com.careerradar.source.JobSourceEntity;
import com.careerradar.source.RawJob;
import com.careerradar.source.SourceType;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;

@Component
public class AshbyJobSource implements JobSource {

    private static final String BASE_URL = "https://api.ashbyhq.com/posting-api/job-board";

    private final RestClient restClient;

    public AshbyJobSource(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder
                .defaultHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.0.0 Safari/537.36 CareerRadar/2.0")
                .build();
    }

    @Override
    public SourceType type() {
        return SourceType.ASHBY;
    }

    @Override
    public List<RawJob> fetch(JobSourceEntity configuration) {
        String companySlug = configuration.getSourceIdentifier();

        if (companySlug == null || companySlug.isBlank()) {
            throw new IllegalArgumentException(
                    "Ashby sourceIdentifier must contain the company slug"
            );
        }

        AshbyJobBoardResponse response = restClient.get()
                .uri(BASE_URL + "/" + companySlug)
                .retrieve()
                .body(AshbyJobBoardResponse.class);

        if (response == null || response.jobs() == null) {
            return Collections.emptyList();
        }

        String companyName = companySlug;

        return response.jobs().stream()
                .map(job -> toRawJob(job, companyName))
                .toList();
    }

    private RawJob toRawJob(AshbyJobBoardResponse.AshbyJob job, String companyName) {
        String description = job.descriptionPlain() != null && !job.descriptionPlain().isBlank()
                ? job.descriptionPlain()
                : job.descriptionHtml();

        return new RawJob(
                job.id(),
                job.title(),
                description,
                companyName,
                job.location(),
                extractCountry(job.location()),
                extractCity(job.location()),
                job.jobUrl() != null ? job.jobUrl() : job.applyUrl(),
                parseDateTime(job.publishedAt()),
                null
        );
    }

    private OffsetDateTime parseDateTime(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        try {
            return OffsetDateTime.parse(value);
        } catch (Exception e) {
            return null;
        }
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
        return parts.length > 0 ? parts[0].trim() : location.trim();
    }
}
