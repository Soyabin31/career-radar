package com.careerradar.eligibility;

import com.careerradar.job.domain.EligibilityStatus;

import java.util.List;

public record EligibilityResult(
        Long jobId,
        EligibilityStatus status,
        boolean geographyMatch,
        boolean workplaceMatch,
        boolean employmentMatch,
        List<String> reasons
) {
}