package com.careerradar.source.workable;

import com.careerradar.source.JobSource;
import com.careerradar.source.JobSourceEntity;
import com.careerradar.source.RawJob;
import com.careerradar.source.SourceType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;

@Component
public class WorkableJobSource implements JobSource {

    private static final String BASE_URL = "https://apply.workable.com/api/v1/widget/accounts";

    private final RestClient restClient;

    public WorkableJobSource(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder
                .defaultHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.0.0 Safari/537.36 CareerRadar/2.0")
                .build();
    }

    @Override
    public SourceType type() {
        return SourceType.WORKABLE;
    }

    @Override
    public List<RawJob> fetch(JobSourceEntity configuration) {
        String companySlug = configuration.getSourceIdentifier();

        if (companySlug == null || companySlug.isBlank()) {
            throw new IllegalArgumentException(
                    "Workable sourceIdentifier must contain the company account slug"
            );
        }

        WorkableJobBoardResponse response = restClient.get()
                .uri(BASE_URL + "/" + companySlug + "?details=true")
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) CareerRadar/2.0")
                .retrieve()
                .body(WorkableJobBoardResponse.class);

        if (response == null || response.jobs() == null) {
            return Collections.emptyList();
        }

        String companyName = response.name() != null && !response.name().isBlank()
                ? response.name()
                : companySlug;

        return response.jobs().stream()
                .map(job -> toRawJob(job, companyName))
                .toList();
    }

    private RawJob toRawJob(WorkableJobBoardResponse.WorkableJob job, String companyName) {
        String location = null;
        String city = null;
        String country = null;

        if (job.location() != null) {
            location = job.location().locationStr();
            city = job.location().city();
            country = job.location().country();
        }

        OffsetDateTime postedAt = null;
        if (job.publishedOn() != null && !job.publishedOn().isBlank()) {
            try {
                if (job.publishedOn().contains("T")) {
                    postedAt = OffsetDateTime.parse(job.publishedOn());
                } else {
                    postedAt = LocalDate.parse(job.publishedOn()).atStartOfDay().atOffset(ZoneOffset.UTC);
                }
            } catch (Exception ignored) {
            }
        }

        String applyUrl = job.url() != null ? job.url() : job.shortlink();

        return new RawJob(
                job.id() != null ? job.id() : job.shortlink(),
                job.title(),
                job.description(),
                companyName,
                location,
                country,
                city,
                applyUrl,
                postedAt,
                null
        );
    }
}
