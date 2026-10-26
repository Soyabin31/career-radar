package com.careerradar.opportunity;

import com.careerradar.eligibility.EligibilityResult;
import com.careerradar.matching.JobMatchResult;

import java.time.OffsetDateTime;

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
        OffsetDateTime postedAt,
        OffsetDateTime discoveredAt,
        JobMatchResult match,
        EligibilityResult eligibility
) {

    public JobOpportunityResult(
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
            EligibilityResult eligibility) {
        this(jobId, title, companyName, description, canonicalUrl, location, country, city,
                geographyType, foreignRegion, workplaceType, employmentType, roleFamily, null, null, match, eligibility);
    }
}