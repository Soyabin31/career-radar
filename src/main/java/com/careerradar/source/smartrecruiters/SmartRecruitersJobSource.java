package com.careerradar.source.smartrecruiters;

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
public class SmartRecruitersJobSource implements JobSource {

    private static final String BASE_URL = "https://api.smartrecruiters.com/v1/companies";

    private final RestClient restClient;

    public SmartRecruitersJobSource(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder
                .defaultHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.0.0 Safari/537.36 CareerRadar/2.0")
                .build();
    }

    @Override
    public SourceType type() {
        return SourceType.SMARTRECRUITERS;
    }

    @Override
    public List<RawJob> fetch(JobSourceEntity configuration) {
        String companyIdentifier = configuration.getSourceIdentifier();

        if (companyIdentifier == null || companyIdentifier.isBlank()) {
            throw new IllegalArgumentException(
                    "SmartRecruiters sourceIdentifier must contain the company identifier"
            );
        }

        SmartRecruitersPostingResponse response = restClient.get()
                .uri(BASE_URL + "/" + companyIdentifier + "/postings?limit=100")
                .retrieve()
                .body(SmartRecruitersPostingResponse.class);

        if (response == null || response.content() == null) {
            return Collections.emptyList();
        }

        return response.content().stream()
                .map(posting -> toRawJob(posting, companyIdentifier))
                .toList();
    }

    private RawJob toRawJob(SmartRecruitersPostingResponse.Posting posting, String companySlug) {
        String companyName = posting.company() != null && posting.company().name() != null
                ? posting.company().name()
                : companySlug;

        String location = null;
        String city = null;
        String country = null;

        if (posting.location() != null) {
            location = posting.location().fullLocation() != null
                    ? posting.location().fullLocation()
                    : posting.location().city();
            city = posting.location().city();
            country = posting.location().country();
        }

        OffsetDateTime postedAt = null;
        if (posting.releasedDate() != null) {
            try {
                postedAt = OffsetDateTime.parse(posting.releasedDate());
            } catch (Exception ignored) {
            }
        }

        String jobUrl = "https://jobs.smartrecruiters.com/" + companySlug + "/" + posting.id();

        return new RawJob(
                posting.id(),
                posting.name(),
                posting.name(),
                companyName,
                location,
                country,
                city,
                jobUrl,
                postedAt,
                null
        );
    }
}
