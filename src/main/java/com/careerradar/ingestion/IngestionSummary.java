package com.careerradar.ingestion;

public record IngestionSummary(
        int totalDiscovered,
        int totalUpdated,
        int totalClosed,
        int totalUnchanged
) {
    public static IngestionSummary empty() {
        return new IngestionSummary(0, 0, 0, 0);
    }
}
