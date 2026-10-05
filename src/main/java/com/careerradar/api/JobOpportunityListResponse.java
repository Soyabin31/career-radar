package com.careerradar.api;

import com.careerradar.opportunity.JobOpportunityResult;

import java.math.BigDecimal;
import java.util.List;

public record JobOpportunityListResponse(
        int count,
        List<Item> opportunities
) {

    public static JobOpportunityListResponse from(
            List<JobOpportunityResult> results) {

        List<Item> items = results.stream()
                .map(Item::from)
                .toList();

        return new JobOpportunityListResponse(
                items.size(),
                items
        );
    }

    public record Item(
            Long jobId,
            String title,
            String companyName,
            String location,
            String geographyType,
            String foreignRegion,
            String workplaceType,
            String employmentType,
            String roleFamily,
            BigDecimal matchScore,
            boolean roleMatch,
            String eligibilityStatus,
            List<String> matchedSkills,
            List<String> missingSkills,
            String canonicalUrl
    ) {

        public static Item from(JobOpportunityResult result) {

            return new Item(
                    result.jobId(),
                    result.title(),
                    result.companyName(),
                    result.location(),
                    result.geographyType(),
                    result.foreignRegion(),
                    result.workplaceType(),
                    result.employmentType(),
                    result.roleFamily(),
                    result.match().score(),
                    result.match().roleMatch(),
                    result.eligibility().status().name(),
                    result.match().matchedSkills(),
                    result.match().missingSkills(),
                    result.canonicalUrl()
            );
        }
    }
}