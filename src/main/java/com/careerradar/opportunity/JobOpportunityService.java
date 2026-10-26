package com.careerradar.opportunity;

import com.careerradar.company.Company;
import com.careerradar.company.CompanyRepository;
import com.careerradar.eligibility.EligibilityResult;
import com.careerradar.eligibility.EligibilityService;
import com.careerradar.job.Job;
import com.careerradar.job.JobRepository;
import com.careerradar.matching.JobMatchResult;
import com.careerradar.matching.JobMatchingService;
import com.careerradar.matching.JobRoleClassification;
import com.careerradar.matching.JobRoleClassificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class JobOpportunityService {

    private final JobRepository jobRepository;
    private final CompanyRepository companyRepository;
    private final JobRoleClassificationRepository jobRoleClassificationRepository;
    private final JobMatchingService jobMatchingService;
    private final EligibilityService eligibilityService;

    public JobOpportunityService(
            JobRepository jobRepository,
            CompanyRepository companyRepository,
            JobRoleClassificationRepository jobRoleClassificationRepository,
            JobMatchingService jobMatchingService,
            EligibilityService eligibilityService) {

        this.jobRepository = jobRepository;
        this.companyRepository = companyRepository;
        this.jobRoleClassificationRepository = jobRoleClassificationRepository;
        this.jobMatchingService = jobMatchingService;
        this.eligibilityService = eligibilityService;
    }

    private final java.util.Map<Long, String> companyNameCache = new java.util.concurrent.ConcurrentHashMap<>();

    @Transactional(readOnly = true)
    public JobOpportunityResult getOpportunity(Long jobId) {

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Job not found: " + jobId
                        ));

        String companyName = null;
        if (job.getCompanyId() != null) {
            companyName = companyNameCache.computeIfAbsent(job.getCompanyId(), id ->
                    companyRepository.findById(id).map(Company::getName).orElse(null)
            );
        }

        String roleFamily = jobRoleClassificationRepository
                .findByJobIdOrderByConfidenceDesc(jobId)
                .stream()
                .findFirst()
                .map(JobRoleClassification::getRoleFamily)
                .map(Enum::name)
                .orElse("UNKNOWN");

        JobMatchResult match =
                jobMatchingService.matchJob(jobId);

        EligibilityResult eligibility =
                eligibilityService.evaluateJob(jobId);

        return new JobOpportunityResult(
                job.getId(),
                job.getTitle(),
                companyName,
                job.getDescription(),
                job.getCanonicalUrl(),
                job.getLocation(),
                job.getCountry(),
                job.getCity(),
                job.getGeographyType() == null
                        ? null
                        : job.getGeographyType().name(),
                job.getForeignRegion() == null
                        ? null
                        : job.getForeignRegion().name(),
                job.getWorkplaceType() == null
                        ? null
                        : job.getWorkplaceType().name(),
                job.getEmploymentType() == null
                        ? null
                        : job.getEmploymentType().name(),
                roleFamily,
                job.getPostedAt(),
                job.getDiscoveredAt(),
                match,
                eligibility
        );
    }
}