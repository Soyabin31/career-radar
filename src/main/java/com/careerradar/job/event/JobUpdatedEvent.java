package com.careerradar.job.event;

public record JobUpdatedEvent(
        Long jobId,
        Long sourceId,
        String sourceJobId,
        String title,
        String companyName
) {
}
