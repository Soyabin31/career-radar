package com.careerradar.matching;

import com.careerradar.job.domain.RoleFamily;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface JobRoleClassificationRepository
        extends JpaRepository<JobRoleClassification, Long> {

    List<JobRoleClassification> findByJobIdOrderByConfidenceDesc(
            Long jobId
    );

    Optional<JobRoleClassification> findByJobIdAndRoleFamily(
            Long jobId,
            RoleFamily roleFamily
    );
}