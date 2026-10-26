package com.careerradar.job.event;

public record JobClosedEvent(
        Long jobId,
        Long sourceId,
        String sourceJobId,
        String title
) {
}
