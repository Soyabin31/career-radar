package com.careerradar.api;

import com.careerradar.eligibility.EligibilityResult;

import java.util.List;

public record EligibilityResponse(
        Long jobId,
        String status,
        boolean geographyMatch,
        boolean workplaceMatch,
        boolean employmentMatch,
        List<String> reasons
) {

    public static EligibilityResponse from(
            EligibilityResult result) {

        return new EligibilityResponse(
                result.jobId(),
                result.status().name(),
                result.geographyMatch(),
                result.workplaceMatch(),
                result.employmentMatch(),
                result.reasons()
        );
    }
}