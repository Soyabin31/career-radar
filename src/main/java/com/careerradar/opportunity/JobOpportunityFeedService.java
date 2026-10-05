package com.careerradar.opportunity;

import com.careerradar.job.Job;
import com.careerradar.job.JobRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;

@Service
public class JobOpportunityFeedService {

    private final JobRepository jobRepository;
    private final JobOpportunityService jobOpportunityService;

    public JobOpportunityFeedService(
            JobRepository jobRepository,
            JobOpportunityService jobOpportunityService) {

        this.jobRepository = jobRepository;
        this.jobOpportunityService = jobOpportunityService;
    }

    @Transactional(readOnly = true)
    public List<JobOpportunityResult> findOpportunities(
            OpportunityFilter filter) {

        List<Job> jobs = jobRepository.findAll()
                .stream()
                .filter(Job::getActive)
                .toList();

        OffsetDateTime postedAfter = calculatePostedAfter(filter);

        return jobs.stream()
                .map(job -> jobOpportunityService
                        .getOpportunity(job.getId()))
                .filter(result -> matches(result, filter, postedAfter))
                .sorted(
                        Comparator
                                .comparing(
                                        (JobOpportunityResult result) ->
                                                result.match().score(),
                                        Comparator.reverseOrder()
                                )
                                .thenComparing(
                                        result -> result.jobId(),
                                        Comparator.reverseOrder()
                                )
                )
                .toList();
    }

    private boolean matches(
            JobOpportunityResult result,
            OpportunityFilter filter,
            OffsetDateTime postedAfter) {

        if (filter.minScore() != null
                && result.match().score().doubleValue()
                < filter.minScore()) {
            return false;
        }

        if (filter.eligibilityStatus() != null
                && result.eligibility().status()
                != filter.eligibilityStatus()) {
            return false;
        }

        if (filter.geographyType() != null
                && !filter.geographyType().name()
                .equals(result.geographyType())) {
            return false;
        }

        if (filter.workplaceType() != null
                && !filter.workplaceType().name()
                .equals(result.workplaceType())) {
            return false;
        }

        if (filter.employmentType() != null
                && !filter.employmentType().name()
                .equals(result.employmentType())) {
            return false;
        }

        if (filter.roleFamily() != null
                && !filter.roleFamily().name()
                .equals(result.roleFamily())) {
            return false;
        }

        if (postedAfter != null) {
            Job job = jobRepository.findById(result.jobId())
                    .orElse(null);

            if (job == null
                    || job.getPostedAt() == null
                    || job.getPostedAt().isBefore(postedAfter)) {
                return false;
            }
        }

        return true;
    }

    private OffsetDateTime calculatePostedAfter(
            OpportunityFilter filter) {

        if (!filter.hasPostedWithinFilter()) {
            return null;
        }

        return OffsetDateTime.now()
                .minusDays(filter.postedWithinDays());
    }
}