package com.careerradar.matching;

import com.careerradar.job.Job;
import com.careerradar.job.JobRepository;
import com.careerradar.job.domain.RoleFamily;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoleClassificationServiceTest {

    @Mock
    private JobRepository jobRepository;

    @Mock
    private JobRoleClassificationRepository
            jobRoleClassificationRepository;

    @Mock
    private RoleClassifier roleClassifier;

    @Mock
    private Job job;

    @Mock
    private JobRoleClassification existingClassification;

    private RoleClassificationService service;

    @BeforeEach
    void setUp() {

        service =
                new RoleClassificationService(
                        jobRepository,
                        jobRoleClassificationRepository,
                        roleClassifier
                );
    }

    @Test
    void shouldNotPersistUnknownRole() {

        when(job.getId())
                .thenReturn(1L);

        when(job.getTitle())
                .thenReturn("Operations Lead");

        when(job.getDescription())
                .thenReturn("Java and Kubernetes.");

        RoleClassificationResult result =
                new RoleClassificationResult(
                        RoleFamily.UNKNOWN,
                        0.0
                );

        when(roleClassifier.classify(
                "Operations Lead",
                "Java and Kubernetes."
        )).thenReturn(result);

        when(jobRoleClassificationRepository
                .findByJobIdOrderByConfidenceDesc(1L))
                .thenReturn(List.of(existingClassification));

        RoleClassificationResult actual =
                service.classifyAndPersist(job);

        assertThat(actual.roleFamily())
                .isEqualTo(RoleFamily.UNKNOWN);

        verify(jobRoleClassificationRepository)
                .deleteAll(List.of(existingClassification));

        verify(jobRoleClassificationRepository, never())
                .save(any(JobRoleClassification.class));
    }

    @Test
    void shouldPersistMeaningfulRoleClassification() {

        when(job.getId())
                .thenReturn(1L);

        when(job.getTitle())
                .thenReturn("Senior Java Backend Engineer");

        when(job.getDescription())
                .thenReturn("Java and Spring Boot.");

        RoleClassificationResult result =
                new RoleClassificationResult(
                        RoleFamily.JAVA_BACKEND,
                        0.95
                );

        when(roleClassifier.classify(
                "Senior Java Backend Engineer",
                "Java and Spring Boot."
        )).thenReturn(result);

        when(jobRoleClassificationRepository
                .findByJobIdOrderByConfidenceDesc(1L))
                .thenReturn(List.of());

        when(jobRoleClassificationRepository
                .findByJobIdAndRoleFamily(
                        1L,
                        RoleFamily.JAVA_BACKEND
                ))
                .thenReturn(Optional.empty());

        RoleClassificationResult actual =
                service.classifyAndPersist(job);

        assertThat(actual.roleFamily())
                .isEqualTo(RoleFamily.JAVA_BACKEND);

        verify(jobRoleClassificationRepository)
                .save(any(JobRoleClassification.class));
    }
}