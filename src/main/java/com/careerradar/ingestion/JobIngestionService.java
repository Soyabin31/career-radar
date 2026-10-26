package com.careerradar.ingestion;

import com.careerradar.company.Company;
import com.careerradar.company.CompanyRepository;
import com.careerradar.company.CompanyType;
import com.careerradar.eligibility.EmploymentTypeNormalizer;
import com.careerradar.eligibility.LocationNormalizer;
import com.careerradar.job.Job;
import com.careerradar.job.JobRepository;
import com.careerradar.job.domain.EmploymentType;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class JobIngestionService {

    private static final Logger log = LoggerFactory.getLogger(JobIngestionService.class);

    private final JobSourceRepository jobSourceRepository;
    private final JobSourceRegistry jobSourceRegistry;
    private final JobRepository jobRepository;
    private final CompanyRepository companyRepository;
    private final LocationNormalizer locationNormalizer;
    private final RoleClassificationService roleClassificationService;
    private final JobSkillExtractionService jobSkillExtractionService;
    private final EmploymentTypeNormalizer employmentTypeNormalizer;
    private final ApplicationEventPublisher eventPublisher;

    public JobIngestionService(
            JobSourceRepository jobSourceRepository,
            JobSourceRegistry jobSourceRegistry,
            JobRepository jobRepository,
            CompanyRepository companyRepository,
            LocationNormalizer locationNormalizer,
            RoleClassificationService roleClassificationService,
            JobSkillExtractionService jobSkillExtractionService,
            EmploymentTypeNormalizer employmentTypeNormalizer,
            ApplicationEventPublisher eventPublisher) {

        this.jobSourceRepository = jobSourceRepository;
        this.jobSourceRegistry = jobSourceRegistry;
        this.jobRepository = jobRepository;
        this.companyRepository = companyRepository;
        this.locationNormalizer = locationNormalizer;
        this.roleClassificationService = roleClassificationService;
        this.jobSkillExtractionService = jobSkillExtractionService;
        this.employmentTypeNormalizer = employmentTypeNormalizer;
        this.eventPublisher = eventPublisher;
    }

    public IngestionSummary ingestActiveSources() {
        List<JobSourceEntity> sources =
                jobSourceRepository.findByActiveTrue();

        if (sources.isEmpty()) {
            return new IngestionSummary(0, 0, 0, 0);
        }

        return sources.parallelStream()
                .map(this::ingestSource)
                .reduce(new IngestionSummary(0, 0, 0, 0),
                        (a, b) -> new IngestionSummary(
                                a.totalDiscovered() + b.totalDiscovered(),
                                a.totalUpdated() + b.totalUpdated(),
                                a.totalClosed() + b.totalClosed(),
                                a.totalUnchanged() + b.totalUnchanged()
                        ));
    }

    @Transactional
    public IngestionSummary ingestSource(JobSourceEntity configuration) {
        int discovered = 0;
        int updated = 0;
        int closed = 0;
        int unchanged = 0;

        try {
            JobSource source =
                    jobSourceRegistry.get(
                            configuration.getSourceType()
                    );

                List<RawJob> rawJobs =
                        source.fetch(configuration);

                if (rawJobs == null) {
                    return new IngestionSummary(0, 0, 0, 0);
                }

                Set<String> seenSourceJobIds = new HashSet<>();

                for (RawJob rawJob : rawJobs) {

                    if (rawJob == null ||
                            rawJob.url() == null ||
                            rawJob.url().isBlank() ||
                            rawJob.title() == null ||
                            rawJob.title().isBlank()) {
                        continue;
                    }

                    if (rawJob.sourceJobId() != null &&
                            !rawJob.sourceJobId().isBlank()) {

                        seenSourceJobIds.add(rawJob.sourceJobId());

                        var existing =
                                jobRepository.findBySourceIdAndSourceJobId(
                                        configuration.getId(),
                                        rawJob.sourceJobId()
                                );

                        if (existing.isPresent()) {

                            Job job = existing.get();
                            job.markSeen();

                            boolean contentChanged =
                                    job.updateContentIfChanged(
                                            rawJob.title(),
                                            rawJob.description(),
                                            rawJob.location()
                                    );

                            if (contentChanged) {
                                LocationNormalizer.NormalizedLocation normalizedLocation =
                                        locationNormalizer.normalize(
                                                rawJob.location(),
                                                rawJob.country(),
                                                rawJob.city()
                                        );

                                job.applyLocation(
                                        normalizedLocation.geographyType(),
                                        normalizedLocation.foreignRegion(),
                                        normalizedLocation.workplaceType(),
                                        normalizedLocation.country(),
                                        normalizedLocation.city()
                                );

                                EmploymentType employmentType =
                                        employmentTypeNormalizer.normalize(
                                                rawJob.title(),
                                                rawJob.description()
                                        );

                                job.applyEmploymentType(employmentType);

                                Job savedJob = jobRepository.save(job);
                                enrichJob(savedJob);

                                eventPublisher.publishEvent(
                                        new JobUpdatedEvent(
                                                savedJob.getId(),
                                                configuration.getId(),
                                                savedJob.getSourceJobId(),
                                                savedJob.getTitle(),
                                                rawJob.companyName()
                                        )
                                );
                                updated++;
                            } else {
                                unchanged++;
                            }
                            continue;
                        }
                    }

                    Company company =
                            resolveCompany(
                                    rawJob.companyName()
                            );

                    LocationNormalizer.NormalizedLocation normalizedLocation =
                            locationNormalizer.normalize(
                                    rawJob.location(),
                                    rawJob.country(),
                                    rawJob.city()
                            );

                    Job job = Job.create(
                            company != null ? company.getId() : null,
                            configuration.getId(),
                            rawJob.sourceJobId(),
                            rawJob.title(),
                            rawJob.description(),
                            rawJob.url(),
                            rawJob.location(),
                            normalizedLocation.country(),
                            normalizedLocation.city(),
                            rawJob.postedAt()
                    );

                    job.applyLocation(
                            normalizedLocation.geographyType(),
                            normalizedLocation.foreignRegion(),
                            normalizedLocation.workplaceType(),
                            normalizedLocation.country(),
                            normalizedLocation.city()
                    );

                    EmploymentType employmentType =
                            employmentTypeNormalizer.normalize(
                                    rawJob.title(),
                                    rawJob.description()
                            );

                    job.applyEmploymentType(
                            employmentType
                    );

                    Job savedJob =
                            jobRepository.save(job);

                    enrichJob(savedJob);

                    eventPublisher.publishEvent(
                            new JobDiscoveredEvent(
                                    savedJob.getId(),
                                    configuration.getId(),
                                    savedJob.getSourceJobId(),
                                    savedJob.getTitle(),
                                    rawJob.companyName(),
                                    rawJob.location(),
                                    savedJob.getCanonicalUrl(),
                                    savedJob.getPostedAt()
                            )
                    );

                    discovered++;
                }

                /*
                 * Detect closed jobs:
                 * If the ATS returned listings, any currently active jobs
                 * for this source that were omitted from the feed are marked closed.
                 */
                if (!rawJobs.isEmpty() && !seenSourceJobIds.isEmpty()) {
                    List<Job> activeJobs =
                            jobRepository.findBySourceIdAndActiveTrue(
                                    configuration.getId()
                            );

                    for (Job activeJob : activeJobs) {
                        if (activeJob.getSourceJobId() != null &&
                                !seenSourceJobIds.contains(activeJob.getSourceJobId())) {

                            activeJob.markClosed();
                            jobRepository.save(activeJob);

                            eventPublisher.publishEvent(
                                    new JobClosedEvent(
                                            activeJob.getId(),
                                            configuration.getId(),
                                            activeJob.getSourceJobId(),
                                            activeJob.getTitle()
                                    )
                            );

                            closed++;
                        }
                    }
                }
            } catch (Exception ex) {
                log.error("Failed to ingest source: {} ({}) - {}",
                        configuration.getSourceIdentifier(),
                        configuration.getSourceType(),
                        ex.getMessage(),
                        ex);
            }

        return new IngestionSummary(discovered, updated, closed, unchanged);
    }

    @Transactional
    public int ingestAllActiveSources() {
        return ingestActiveSources().totalDiscovered();
    }

    /**
     * Enriches an existing job with the same matching information
     * that is applied to newly ingested jobs.
     *
     * This is intentionally kept in one method so that the new-job
     * and existing-job paths do not develop different enrichment logic.
     */
    private void enrichJob(Job job) {

        roleClassificationService.classifyAndPersist(
                job
        );

        jobSkillExtractionService.extractAndPersist(
                job
        );
    }

    private Company resolveCompany(
            String companyName) {

        if (companyName == null ||
                companyName.isBlank()) {

            return null;
        }

        String normalizedName =
                companyName.trim();

        return companyRepository
                .findByNameIgnoreCase(normalizedName)
                .orElseGet(() ->
                        companyRepository.save(
                                Company.create(
                                        normalizedName,
                                        null,
                                        null,
                                        CompanyType.OTHER,
                                        null
                                )
                        )
                );
    }

    /**
     * Backfills location, employment type, role classification
     * and extracted skills for jobs that were already stored.
     */
    @Transactional
    public int enrichExistingJobs() {

        List<Job> jobs =
                jobRepository.findAll();

        int updated = 0;

        for (Job job : jobs) {

            LocationNormalizer.NormalizedLocation normalized =
                    locationNormalizer.normalize(
                            job.getLocation(),
                            job.getCountry(),
                            job.getCity()
                    );

            job.applyLocation(
                    normalized.geographyType(),
                    normalized.foreignRegion(),
                    normalized.workplaceType(),
                    normalized.country(),
                    normalized.city()
            );

            EmploymentType employmentType =
                    employmentTypeNormalizer.normalize(
                            job.getTitle(),
                            job.getDescription()
                    );

            job.applyEmploymentType(
                    employmentType
            );

            enrichJob(job);

            updated++;
        }

        return updated;
    }

    /**
     * Kept for compatibility with the earlier location-only
     * backfill operation.
     *
     * New code should use enrichExistingJobs().
     */
    @Transactional
    public int normalizeExistingJobs() {

        List<Job> jobs =
                jobRepository.findAll();

        int updated = 0;

        for (Job job : jobs) {

            LocationNormalizer.NormalizedLocation normalized =
                    locationNormalizer.normalize(
                            job.getLocation(),
                            job.getCountry(),
                            job.getCity()
                    );

            job.applyLocation(
                    normalized.geographyType(),
                    normalized.foreignRegion(),
                    normalized.workplaceType(),
                    normalized.country(),
                    normalized.city()
            );

            updated++;
        }

        return updated;
    }
}