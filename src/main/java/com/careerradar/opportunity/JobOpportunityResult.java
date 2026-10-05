package com.careerradar.opportunity;

import com.careerradar.eligibility.EligibilityResult;
import com.careerradar.matching.JobMatchResult;

public record JobOpportunityResult(
        Long jobId,
        String title,
        String companyName,
        String description,
        String canonicalUrl,
        String location,
        String country,
        String city,
        String geographyType,
        String foreignRegion,
        String workplaceType,
        String employmentType,
        String roleFamily,
        JobMatchResult match,
        EligibilityResult eligibility
) {
}