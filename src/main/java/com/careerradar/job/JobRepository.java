package com.careerradar.job;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JobRepository extends JpaRepository<Job, Long> {

    Optional<Job> findBySourceIdAndSourceJobId(
            Long sourceId,
            String sourceJobId
    );
}