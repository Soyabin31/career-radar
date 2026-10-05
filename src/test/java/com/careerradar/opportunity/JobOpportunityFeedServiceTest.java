package com.careerradar.opportunity;

import com.careerradar.eligibility.EligibilityResult;
import com.careerradar.job.Job;
import com.careerradar.job.JobRepository;
import com.careerradar.job.domain.EligibilityStatus;
import com.careerradar.job.domain.GeographyType;
import com.careerradar.job.domain.RoleFamily;
import com.careerradar.job.domain.WorkplaceType;
import com.careerradar.matching.JobMatchResult;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JobOpportunityFeedServiceTest {

    private final JobRepository jobRepository =
            mock(JobRepository.class);

    private final JobOpportunityService
            jobOpportunityService =
            mock(JobOpportunityService.class);

    private final JobOpportunityFeedService service =
            new JobOpportunityFeedService(
                    jobRepository,
                    jobOpportunityService
            );

    @Test
    void shouldReturnOnlyMatchingMinimumScore() {

        Job job1 = mock(Job.class);
        Job job2 = mock(Job.class);

        when(job1.getId()).thenReturn(1L);
        when(job1.getActive()).thenReturn(true);

        when(job2.getId()).thenReturn(2L);
        when(job2.getActive()).thenReturn(true);

        when(jobRepository.findAll())
                .thenReturn(List.of(job1, job2));

        when(jobOpportunityService.getOpportunity(1L))
                .thenReturn(createResult(
                        1L,
                        "Senior Java Backend Engineer",
                        "JAVA_BACKEND",
                        "90.00",
                        EligibilityStatus.REGION_OK,
                        "INDIA",
                        "REMOTE"
                ));

        when(jobOpportunityService.getOpportunity(2L))
                .thenReturn(createResult(
                        2L,
                        "Python Data Engineer",
                        "PYTHON_DATA",
                        "45.00",
                        EligibilityStatus.REGION_OK,
                        "INDIA",
                        "REMOTE"
                ));

        OpportunityFilter filter =
                new OpportunityFilter(
                        70.0,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null
                );

        List<JobOpportunityResult> results =
                service.findOpportunities(filter);

        assertEquals(1, results.size());
        assertEquals(1L, results.get(0).jobId());
        assertEquals(
                "Senior Java Backend Engineer",
                results.get(0).title()
        );
    }

    @Test
    void shouldFilterByRoleFamilyAndWorkplace() {

        Job job1 = mock(Job.class);
        Job job2 = mock(Job.class);

        when(job1.getId()).thenReturn(1L);
        when(job1.getActive()).thenReturn(true);

        when(job2.getId()).thenReturn(2L);
        when(job2.getActive()).thenReturn(true);

        when(jobRepository.findAll())
                .thenReturn(List.of(job1, job2));

        when(jobOpportunityService.getOpportunity(1L))
                .thenReturn(createResult(
                        1L,
                        "Senior Java Backend Engineer",
                        "JAVA_BACKEND",
                        "90.00",
                        EligibilityStatus.REGION_OK,
                        "INDIA",
                        "REMOTE"
                ));

        when(jobOpportunityService.getOpportunity(2L))
                .thenReturn(createResult(
                        2L,
                        "Senior Java Backend Engineer",
                        "JAVA_BACKEND",
                        "95.00",
                        EligibilityStatus.REGION_OK,
                        "INDIA",
                        "ONSITE"
                ));

        OpportunityFilter filter =
                new OpportunityFilter(
                        null,
                        null,
                        null,
                        WorkplaceType.REMOTE,
                        null,
                        RoleFamily.JAVA_BACKEND,
                        null
                );

        List<JobOpportunityResult> results =
                service.findOpportunities(filter);

        assertEquals(1, results.size());
        assertEquals(1L, results.get(0).jobId());
    }

    @Test
    void shouldSortByMatchScoreDescending() {

        Job job1 = mock(Job.class);
        Job job2 = mock(Job.class);

        when(job1.getId()).thenReturn(1L);
        when(job1.getActive()).thenReturn(true);

        when(job2.getId()).thenReturn(2L);
        when(job2.getActive()).thenReturn(true);

        when(jobRepository.findAll())
                .thenReturn(List.of(job1, job2));

        when(jobOpportunityService.getOpportunity(1L))
                .thenReturn(createResult(
                        1L,
                        "Java Engineer",
                        "JAVA_BACKEND",
                        "75.00",
                        EligibilityStatus.REGION_OK,
                        "INDIA",
                        "REMOTE"
                ));

        when(jobOpportunityService.getOpportunity(2L))
                .thenReturn(createResult(
                        2L,
                        "Senior Java Engineer",
                        "JAVA_BACKEND",
                        "95.00",
                        EligibilityStatus.REGION_OK,
                        "INDIA",
                        "REMOTE"
                ));

        List<JobOpportunityResult> results =
                service.findOpportunities(
                        OpportunityFilter.empty()
                );

        assertEquals(2, results.size());
        assertEquals(2L, results.get(0).jobId());
        assertEquals(1L, results.get(1).jobId());
    }

    private JobOpportunityResult createResult(
            Long jobId,
            String title,
            String roleFamily,
            String score,
            EligibilityStatus eligibilityStatus,
            String geography,
            String workplace) {

        JobMatchResult match =
                new JobMatchResult(
                        jobId,
                        title,
                        roleFamily,
                        new BigDecimal(score),
                        "JAVA_BACKEND".equals(roleFamily),
                        List.of("Java"),
                        List.of(),
                        List.of()
                );

        EligibilityResult eligibility =
                new EligibilityResult(
                        jobId,
                        eligibilityStatus,
                        true,
                        true,
                        true,
                        List.of()
                );

        return new JobOpportunityResult(
                jobId,
                title,
                "Test Company",
                "Test description",
                "https://example.com/jobs/" + jobId,
                "Bengaluru, India",
                "India",
                "Bengaluru",
                geography,
                null,
                workplace,
                "FULL_TIME",
                roleFamily,
                match,
                eligibility
        );
    }
}