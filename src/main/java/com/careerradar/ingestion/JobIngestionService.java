package com.careerradar.ingestion;

import com.careerradar.company.Company;
import com.careerradar.company.CompanyRepository;
import com.careerradar.company.CompanyType;
import com.careerradar.eligibility.LocationNormalizer;
import com.careerradar.job.Job;
import com.careerradar.job.JobRepository;
import com.careerradar.matching.JobSkillExtractionService;
import com.careerradar.matching.RoleClassificationService;
import com.careerradar.source.JobSource;
import com.careerradar.source.JobSourceEntity;
import com.careerradar.source.JobSourceRegistry;
import com.careerradar.source.JobSourceRepository;
import com.careerradar.source.RawJob;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class JobIngestionService {

    private final JobSourceRepository jobSourceRepository;
    private final JobSourceRegistry jobSourceRegistry;
    private final JobRepository jobRepository;
    private final CompanyRepository companyRepository;
    private final LocationNormalizer locationNormalizer;
    private final RoleClassificationService roleClassificationService;
    private final JobSkillExtractionService jobSkillExtractionService;

    public JobIngestionService(
            JobSourceRepository jobSourceRepository,
            JobSourceRegistry jobSourceRegistry,
            JobRepository jobRepository,
            CompanyRepository companyRepository,
            LocationNormalizer locationNormalizer,
            RoleClassificationService roleClassificationService,
            JobSkillExtractionService jobSkillExtractionService) {

        this.jobSourceRepository = jobSourceRepository;
        this.jobSourceRegistry = jobSourceRegistry;
        this.jobRepository = jobRepository;
        this.companyRepository = companyRepository;
        this.locationNormalizer = locationNormalizer;
        this.roleClassificationService = roleClassificationService;
        this.jobSkillExtractionService = jobSkillExtractionService;
    }

    @Transactional
    public int ingestAllActiveSources() {

        int inserted = 0;

        List<JobSourceEntity> sources =
                jobSourceRepository.findByActiveTrue();

        for (JobSourceEntity configuration : sources) {

            JobSource source =
                    jobSourceRegistry.get(
                            configuration.getSourceType()
                    );

            List<RawJob> rawJobs =
                    source.fetch(configuration);

            for (RawJob rawJob : rawJobs) {

                boolean wasInserted =
                        ingest(configuration, rawJob);

                if (wasInserted) {
                    inserted++;
                }
            }
        }

        return inserted;
    }

    private boolean ingest(
            JobSourceEntity source,
            RawJob rawJob) {

        if (rawJob == null) {
            return false;
        }

        if (rawJob.url() == null ||
                rawJob.url().isBlank()) {
            return false;
        }

        if (rawJob.title() == null ||
                rawJob.title().isBlank()) {
            return false;
        }

        /*
         * Primary deduplication:
         *
         * source_id + source_job_id
         */
        if (rawJob.sourceJobId() != null &&
                !rawJob.sourceJobId().isBlank()) {

            var existing =
                    jobRepository.findBySourceIdAndSourceJobId(
                            source.getId(),
                            rawJob.sourceJobId()
                    );

            if (existing.isPresent()) {

                Job job = existing.get();

                job.markSeen();

                return false;
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
                source.getId(),
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

        Job savedJob =
                jobRepository.save(job);

        /*
         * Newly inserted jobs enter the matching pipeline.
         */
        enrichJob(savedJob);

        return true;
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
     * Backfills location, role classification and extracted skills
     * for jobs that were already stored before the enrichment pipeline
     * was introduced.
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