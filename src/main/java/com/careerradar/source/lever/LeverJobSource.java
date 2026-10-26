package com.careerradar.source.lever;

import com.careerradar.source.JobSource;
import com.careerradar.source.JobSourceEntity;
import com.careerradar.source.RawJob;
import com.careerradar.source.SourceType;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;

@Component
public class LeverJobSource implements JobSource {

    private static final String BASE_URL = "https://api.lever.co/v0/postings";

    private final RestClient restClient;

    public LeverJobSource(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder
                .defaultHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.0.0 Safari/537.36 CareerRadar/2.0")
                .build();
    }

    @Override
    public SourceType type() {
        return SourceType.LEVER;
    }

    @Override
    public List<RawJob> fetch(JobSourceEntity configuration) {
        String site = configuration.getSourceIdentifier();

        if (site == null || site.isBlank()) {
            throw new IllegalArgumentException(
                    "Lever sourceIdentifier must contain the company site slug"
            );
        }

        List<LeverPostingResponse> postings = restClient.get()
                .uri(BASE_URL + "/" + site + "?mode=json")
                .retrieve()
                .body(new ParameterizedTypeReference<List<LeverPostingResponse>>() {});

        if (postings == null || postings.isEmpty()) {
            return Collections.emptyList();
        }

        String companyName = site;

        return postings.stream()
                .map(posting -> toRawJob(posting, companyName))
                .toList();
    }

    private RawJob toRawJob(LeverPostingResponse posting, String companyName) {
        String location = null;
        if (posting.categories() != null && posting.categories().location() != null) {
            location = posting.categories().location();
        }

        String description = null;
        if (posting.descriptionBodyPlain() != null && !posting.descriptionBodyPlain().isBlank()) {
            description = posting.descriptionBodyPlain();
        } else if (posting.descriptionPlain() != null && !posting.descriptionPlain().isBlank()) {
            description = posting.descriptionPlain();
        } else if (posting.descriptionBody() != null && !posting.descriptionBody().isBlank()) {
            description = posting.descriptionBody();
        } else {
            description = posting.description();
        }

        OffsetDateTime postedAt = null;
        if (posting.createdAt() != null) {
            postedAt = OffsetDateTime.ofInstant(
                    Instant.ofEpochMilli(posting.createdAt()),
                    ZoneOffset.UTC
            );
        }

        String country = posting.country() != null && !posting.country().isBlank()
                ? posting.country()
                : extractCountry(location);

        return new RawJob(
                posting.id(),
                posting.text(),
                description,
                companyName,
                location,
                country,
                extractCity(location),
                posting.hostedUrl(),
                postedAt,
                null
        );
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
