package com.careerradar.api;

import com.careerradar.matching.JobMatchResult;

import java.math.BigDecimal;
import java.util.List;

public record JobMatchResponse(
        Long jobId,
        String jobTitle,
        String jobRoleFamily,
        BigDecimal score,
        boolean roleMatch,
        List<String> matchedSkills,
        List<String> missingSkills,
        List<String> supportingSkills
) {

    public static JobMatchResponse from(
            JobMatchResult result) {

        return new JobMatchResponse(
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