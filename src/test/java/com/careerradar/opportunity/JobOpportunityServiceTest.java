package com.careerradar.opportunity;

import com.careerradar.company.Company;
import com.careerradar.company.CompanyRepository;
import com.careerradar.eligibility.EligibilityResult;
import com.careerradar.eligibility.EligibilityService;
import com.careerradar.job.Job;
import com.careerradar.job.JobRepository;
import com.careerradar.job.domain.EligibilityStatus;
import com.careerradar.job.domain.RoleFamily;
import com.careerradar.matching.JobMatchResult;
import com.careerradar.matching.JobMatchingService;
import com.careerradar.matching.JobRoleClassification;
import com.careerradar.matching.JobRoleClassificationRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JobOpportunityServiceTest {

    private final JobRepository jobRepository =
            mock(JobRepository.class);

    private final CompanyRepository companyRepository =
            mock(CompanyRepository.class);

    private final JobRoleClassificationRepository
            jobRoleClassificationRepository =
            mock(JobRoleClassificationRepository.class);

    private final JobMatchingService jobMatchingService =
            mock(JobMatchingService.class);

    private final EligibilityService eligibilityService =
            mock(EligibilityService.class);

    private final JobOpportunityService service =
            new JobOpportunityService(
                    jobRepository,
                    companyRepository,
                    jobRoleClassificationRepository,
                    jobMatchingService,
                    eligibilityService
            );

    @Test
    void shouldBuildCompleteJobOpportunity() {

        Long jobId = 101L;

        Job job = mock(Job.class);
        Company company = mock(Company.class);
        JobRoleClassification classification =
                mock(JobRoleClassification.class);

        when(jobRepository.findById(jobId))
                .thenReturn(Optional.of(job));

        when(job.getId()).thenReturn(jobId);
        when(job.getCompanyId()).thenReturn(10L);
        when(job.getTitle())
                .thenReturn("Senior Java Backend Engineer");
        when(job.getDescription())
                .thenReturn("Build Java microservices.");
        when(job.getCanonicalUrl())
                .thenReturn("https://example.com/jobs/101");
        when(job.getLocation())
                .thenReturn("Bengaluru, India");
        when(job.getCountry())
                .thenReturn("India");
        when(job.getCity())
                .thenReturn("Bengaluru");

        when(companyRepository.findById(10L))
                .thenReturn(Optional.of(company));

        when(company.getName())
                .thenReturn("Example Technologies");

        when(classification.getRoleFamily())
                .thenReturn(RoleFamily.JAVA_BACKEND);

        when(jobRoleClassificationRepository
                .findByJobIdOrderByConfidenceDesc(jobId))
                .thenReturn(List.of(classification));

        JobMatchResult match = new JobMatchResult(
                jobId,
                "Senior Java Backend Engineer",
                "JAVA_BACKEND",
                new BigDecimal("100.00"),
                true,
                List.of("Java", "Spring Boot"),
                List.of(),
                List.of()
        );

        EligibilityResult eligibility =
                new EligibilityResult(
                        jobId,
                        EligibilityStatus.REGION_OK,
                        true,
                        true,
                        true,
                        List.of()
                );

        when(jobMatchingService.matchJob(jobId))
                .thenReturn(match);

        when(eligibilityService.evaluateJob(jobId))
                .thenReturn(eligibility);

        JobOpportunityResult result =
                service.getOpportunity(jobId);

        assertEquals(jobId, result.jobId());
        assertEquals(
                "Senior Java Backend Engineer",
                result.title()
        );
        assertEquals(
                "Example Technologies",
                result.companyName()
        );
        assertEquals(
                "https://example.com/jobs/101",
                result.canonicalUrl()
        );
        assertEquals(
                "JAVA_BACKEND",
                result.roleFamily()
        );

        assertNotNull(result.match());
        assertEquals(
                0,
                result.match().score()
                        .compareTo(new BigDecimal("100.00"))
        );
        assertTrue(result.match().roleMatch());

        assertNotNull(result.eligibility());
        assertEquals(
                EligibilityStatus.REGION_OK,
                result.eligibility().status()
        );

        verify(jobMatchingService).matchJob(jobId);
        verify(eligibilityService).evaluateJob(jobId);
    }

    @Test
    void shouldFailWhenJobDoesNotExist() {

        Long jobId = 999L;

        when(jobRepository.findById(jobId))
                .thenReturn(Optional.empty());

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> service.getOpportunity(jobId)
                );

        assertEquals(
                "Job not found: 999",
                exception.getMessage()
        );

        verifyNoInteractions(
                companyRepository,
                jobMatchingService,
                eligibilityService
        );
    }
}