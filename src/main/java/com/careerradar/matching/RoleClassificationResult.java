package com.careerradar.matching;

import com.careerradar.job.domain.RoleFamily;

public record RoleClassificationResult(
        RoleFamily roleFamily,
        double confidence
) {
}