package com.careerradar.matching;

import java.math.BigDecimal;

public record RoleClassificationResponse(
        Long jobId,
        String roleFamily,
        BigDecimal confidence,
        String source
) {

    public static RoleClassificationResponse from(
            JobRoleClassification classification) {

        return new RoleClassificationResponse(
                classification.getJob().getId(),
                classification.getRoleFamily().name(),
                classification.getConfidence(),
                classification.getSource()
        );
    }
}