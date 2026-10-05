package com.careerradar.matching;

import com.careerradar.job.Job;
import com.careerradar.job.JobRepository;
import com.careerradar.job.domain.RoleFamily;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class RoleClassificationServiceTest {

    @Test
    void shouldClassifyAndPersistJavaBackendJob() {

        JobRepository jobRepository =
                mock(JobRepository.class);

        JobRoleClassificationRepository classificationRepository =
                mock(JobRoleClassificationRepository.class);

        RoleClassifier roleClassifier =
                mock(RoleClassifier.class);

        Job job = mock(Job.class);

        when(job.getId()).thenReturn(1L);
        when(job.getTitle())
                .thenReturn("Senior Java Backend Engineer");
        when(job.getDescription())
                .thenReturn("Spring Boot backend services.");

        when(jobRepository.findAll())
                .thenReturn(List.of(job));

        when(roleClassifier.classify(
                job.getTitle(),
                job.getDescription()
        )).thenReturn(
                new RoleClassificationResult(
                        RoleFamily.JAVA_BACKEND,
                        0.95
                )
        );

        when(classificationRepository
                .findByJobIdAndRoleFamily(
                        1L,
                        RoleFamily.JAVA_BACKEND
                ))
                .thenReturn(java.util.Optional.empty());

        RoleClassificationService service =
                new RoleClassificationService(
                        jobRepository,
                        classificationRepository,
                        roleClassifier
                );

        int classified =
                service.classifyAllJobs();

        assertEquals(1, classified);

        verify(classificationRepository)
                .save(any(JobRoleClassification.class));
    }

    @Test
    void shouldNotPersistUnknownRole() {

        JobRepository jobRepository =
                mock(JobRepository.class);

        JobRoleClassificationRepository classificationRepository =
                mock(JobRoleClassificationRepository.class);

        RoleClassifier roleClassifier =
                mock(RoleClassifier.class);

        Job job = mock(Job.class);

        when(job.getId()).thenReturn(2L);
        when(job.getTitle())
                .thenReturn("Senior Professional");
        when(job.getDescription())
                .thenReturn("Business stakeholder role.");

        when(jobRepository.findAll())
                .thenReturn(List.of(job));

        when(roleClassifier.classify(
                job.getTitle(),
                job.getDescription()
        )).thenReturn(
                new RoleClassificationResult(
                        RoleFamily.UNKNOWN,
                        0.0
                )
        );

        RoleClassificationService service =
                new RoleClassificationService(
                        jobRepository,
                        classificationRepository,
                        roleClassifier
                );

        int classified =
                service.classifyAllJobs();

        assertEquals(0, classified);

        verify(classificationRepository, never())
                .save(any(JobRoleClassification.class));
    }
}