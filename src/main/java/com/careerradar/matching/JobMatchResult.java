package com.careerradar.matching;

import java.math.BigDecimal;
import java.util.List;

public record JobMatchResult(
        Long jobId,
        String jobTitle,
        String jobRoleFamily,
        BigDecimal score,
        boolean roleMatch,
        List<String> matchedSkills,
        List<String> missingSkills,
        List<String> supportingSkills
) {
}