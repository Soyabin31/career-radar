package com.careerradar.ingestion;

import com.careerradar.company.CompanyRepository;
import com.careerradar.eligibility.EmploymentTypeNormalizer;
import com.careerradar.eligibility.LocationNormalizer;
import com.careerradar.job.Job;
import com.careerradar.job.JobRepository;
import com.careerradar.job.domain.EmploymentType;
import com.careerradar.job.domain.ForeignRegion;
import com.careerradar.job.domain.GeographyType;
import com.careerradar.job.domain.WorkplaceType;
import com.careerradar.job.event.JobClosedEvent;
import com.careerradar.job.event.JobDiscoveredEvent;
import com.careerradar.job.event.JobUpdatedEvent;
import com.careerradar.matching.JobSkillExtractionService;
import com.careerradar.matching.RoleClassificationService;
import com.careerradar.source.JobSource;
import com.careerradar.source.JobSourceEntity;
import com.careerradar.source.JobSourceRegistry;
import com.careerradar.source.JobSourceRepository;
import com.careerradar.source.RawJob;
import com.careerradar.source.SourceType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class JobIngestionServiceTest {

    private JobSourceRepository jobSourceRepository;
    private JobSourceRegistry jobSourceRegistry;
    private JobRepository jobRepository;
    private CompanyRepository companyRepository;
    private LocationNormalizer locationNormalizer;
    private RoleClassificationService roleClassificationService;
    private JobSkillExtractionService jobSkillExtractionService;
    private EmploymentTypeNormalizer employmentTypeNormalizer;
    private ApplicationEventPublisher eventPublisher;
    private JobIngestionService service;

    @BeforeEach
    void setUp() {
        jobSourceRepository = mock(JobSourceRepository.class);
        jobSourceRegistry = mock(JobSourceRegistry.class);
        jobRepository = mock(JobRepository.class);
        companyRepository = mock(CompanyRepository.class);
        locationNormalizer = mock(LocationNormalizer.class);
        roleClassificationService = mock(RoleClassificationService.class);
        jobSkillExtractionService = mock(JobSkillExtractionService.class);
        employmentTypeNormalizer = mock(EmploymentTypeNormalizer.class);
        eventPublisher = mock(ApplicationEventPublisher.class);

        service = new JobIngestionService(
                jobSourceRepository,
                jobSourceRegistry,
                jobRepository,
                companyRepository,
                locationNormalizer,
                roleClassificationService,
                jobSkillExtractionService,
                employmentTypeNormalizer,
                eventPublisher
        );
    }

    @Test
    void shouldCreateService() {
        assertNotNull(service);
    }

    @Test
    void shouldIngestAndDetectNewUpdatedAndClosedJobs() {
        JobSourceEntity sourceEntity = JobSourceEntity.create(
                1L,
                SourceType.GREENHOUSE,
                "acme",
                "https://boards.greenhouse.io/acme"
        );

        JobSource mockSource = mock(JobSource.class);
        when(jobSourceRepository.findByActiveTrue()).thenReturn(List.of(sourceEntity));
        when(jobSourceRegistry.get(SourceType.GREENHOUSE)).thenReturn(mockSource);

        RawJob newJob = new RawJob(
                "job-1",
                "Staff Backend Engineer",
                "Lead distributed systems",
                "Acme Corp",
                "Remote",
                "USA",
                "San Francisco",
                "https://acme.com/jobs/1",
                OffsetDateTime.now(),
                null
        );

        RawJob updatedRawJob = new RawJob(
                "job-2",
                "Senior Frontend Engineer",
                "Updated description with React & TypeScript",
                "Acme Corp",
                "Remote",
                "USA",
                "San Francisco",
                "https://acme.com/jobs/2",
                OffsetDateTime.now(),
                null
        );

        when(mockSource.fetch(sourceEntity)).thenReturn(List.of(newJob, updatedRawJob));

        when(locationNormalizer.normalize(any(), any(), any()))
                .thenReturn(new LocationNormalizer.NormalizedLocation(
                        GeographyType.FOREIGN,
                        ForeignRegion.USA,
                        WorkplaceType.REMOTE,
                        "USA",
                        "San Francisco"
                ));

        when(employmentTypeNormalizer.normalize(any(), any()))
                .thenReturn(EmploymentType.FULL_TIME);

        // Job 1 is new
        when(jobRepository.findBySourceIdAndSourceJobId(sourceEntity.getId(), "job-1"))
                .thenReturn(Optional.empty());

        // Job 2 already exists
        Job existingJob2 = Job.create(
                1L,
                sourceEntity.getId(),
                "job-2",
                "Senior Frontend Engineer",
                "Old description",
                "https://acme.com/jobs/2",
                "Remote",
                "USA",
                "San Francisco",
                OffsetDateTime.now()
        );
        when(jobRepository.findBySourceIdAndSourceJobId(sourceEntity.getId(), "job-2"))
                .thenReturn(Optional.of(existingJob2));

        // Job 3 is currently active in DB, but omitted from incoming feed -> closed
        Job closedJob3 = Job.create(
                1L,
                sourceEntity.getId(),
                "job-3",
                "Junior QA Engineer",
                "QA testing",
                "https://acme.com/jobs/3",
                "Remote",
                "USA",
                "San Francisco",
                OffsetDateTime.now()
        );
        when(jobRepository.findBySourceIdAndActiveTrue(sourceEntity.getId()))
                .thenReturn(List.of(existingJob2, closedJob3));

        when(jobRepository.save(any(Job.class))).thenAnswer(invocation -> invocation.getArgument(0));

        IngestionSummary summary = service.ingestActiveSources();

        assertEquals(1, summary.totalDiscovered());
        assertEquals(1, summary.totalUpdated());
        assertEquals(1, summary.totalClosed());
        assertEquals(0, summary.totalUnchanged());

        verify(eventPublisher, times(1)).publishEvent(any(JobDiscoveredEvent.class));
        verify(eventPublisher, times(1)).publishEvent(any(JobUpdatedEvent.class));
        verify(eventPublisher, times(1)).publishEvent(any(JobClosedEvent.class));
    }
}