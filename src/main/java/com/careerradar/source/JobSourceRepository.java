package com.careerradar.source;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobSourceRepository
        extends JpaRepository<JobSourceEntity, Long> {

    List<JobSourceEntity> findByActiveTrue();

    List<JobSourceEntity> findBySourceTypeAndActiveTrue(
            SourceType sourceType
    );
}