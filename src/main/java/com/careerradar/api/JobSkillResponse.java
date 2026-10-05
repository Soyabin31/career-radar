package com.careerradar.api;

import com.careerradar.matching.JobSkill;

import java.math.BigDecimal;

public record JobSkillResponse(
        Long jobId,
        Long skillId,
        String skillName,
        String normalizedSkillName,
        Boolean required,
        BigDecimal confidence,
        String source
) {

    public static JobSkillResponse from(
            JobSkill jobSkill) {

        return new JobSkillResponse(
                jobSkill.getJob().getId(),
                jobSkill.getSkill().getId(),
                jobSkill.getSkill().getName(),
                jobSkill.getSkill().getNormalizedName(),
                jobSkill.getRequired(),
                jobSkill.getConfidence(),
                jobSkill.getSource()
        );
    }
}