package com.careerradar.matching;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface JobSkillRepository
        extends JpaRepository<JobSkill, Long> {

    List<JobSkill> findByJobIdOrderByConfidenceDesc(Long jobId);

    Optional<JobSkill> findByJobIdAndSkillId(
            Long jobId,
            Long skillId
    );
}