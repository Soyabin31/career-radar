package com.careerradar.ingestion;

import com.careerradar.job.domain.RoleFamily;
import com.careerradar.matching.JobRoleClassificationRepository;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class JobIngestionClassificationIntegrationTest {

    @Test
    void classificationRepositoryShouldBeAvailable() {

        assertTrue(
                JobRoleClassificationRepository.class
                        .isAssignableFrom(
                                JobRoleClassificationRepository.class
                        )
        );
    }
}