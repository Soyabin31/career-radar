package com.careerradar.job.event;

import java.time.OffsetDateTime;

public record JobDiscoveredEvent(
        Long jobId,
        Long sourceId,
        String sourceJobId,
        String title,
        String companyName,
        String location,
        String url,
        OffsetDateTime postedAt
) {
}
