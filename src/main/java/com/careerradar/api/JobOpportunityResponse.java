package com.careerradar.api;

import com.careerradar.eligibility.EligibilityResult;
import com.careerradar.matching.JobMatchResult;
import com.careerradar.opportunity.JobOpportunityResult;

import java.time.OffsetDateTime;

public record JobOpportunityResponse(
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
        MatchResponse match,
        EligibilityResponse eligibility
) {

    public static JobOpportunityResponse from(
            JobOpportunityResult result) {

        return new JobOpportunityResponse(
                result.jobId(),
                result.title(),
                result.companyName(),
                result.description(),
                result.canonicalUrl(),
                result.location(),
                result.country(),
                result.city(),
                result.geographyType(),
                result.foreignRegion(),
                result.workplaceType(),
                result.employmentType(),
                result.roleFamily(),
                result.postedAt(),
                result.discoveredAt(),
                MatchResponse.from(result.match()),
                EligibilityResponse.from(result.eligibility())
        );
    }

    public record MatchResponse(
            Long jobId,
            String jobTitle,
            String jobRoleFamily,
            java.math.BigDecimal score,
            boolean roleMatch,
            java.util.List<String> matchedSkills,
            java.util.List<String> missingSkills,
            java.util.List<String> supportingSkills
    ) {

        public static MatchResponse from(JobMatchResult result) {
            return new MatchResponse(
                    result.jobId(),
                    result.jobTitle(),
                    result.jobRoleFamily(),
                    result.score(),
                    result.roleMatch(),
                    result.matchedSkills(),
                    result.missingSkills(),
                    result.supportingSkills()
            );
        }
    }
}