package com.careerradar.ingestion;

import com.careerradar.company.Company;
import com.careerradar.company.CompanyRepository;
import com.careerradar.company.CompanyType;
import com.careerradar.job.Job;
import com.careerradar.job.JobRepository;
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

    public JobIngestionService(
            JobSourceRepository jobSourceRepository,
            JobSourceRegistry jobSourceRegistry,
            JobRepository jobRepository,
            CompanyRepository companyRepository) {

        this.jobSourceRepository = jobSourceRepository;
        this.jobSourceRegistry = jobSourceRegistry;
        this.jobRepository = jobRepository;
        this.companyRepository = companyRepository;
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

        Job job = Job.create(
                company != null
                        ? company.getId()
                        : null,

                source.getId(),

                rawJob.sourceJobId(),

                rawJob.title(),

                rawJob.description(),

                rawJob.url(),

                rawJob.country(),

                rawJob.city(),

                rawJob.postedAt()
        );

        jobRepository.save(job);

        return true;
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
}