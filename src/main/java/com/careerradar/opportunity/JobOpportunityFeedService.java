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

        List<Job> jobs =
                jobRepository.findAll()
                        .stream()
                        .filter(Job::getActive)
                        .toList();

        OffsetDateTime postedAfter =
                calculatePostedAfter(filter);

        java.util.Map<Long, Job> jobMap = jobs.stream()
                .filter(j -> j.getId() != null)
                .collect(java.util.stream.Collectors.toMap(Job::getId, j -> j, (a, b) -> a));

        java.util.stream.Stream<Job> stream = jobs.stream();

        if (postedAfter != null) {
            stream = stream.filter(job -> {
                OffsetDateTime effectiveDate =
                        job.getPostedAt() != null
                                ? job.getPostedAt()
                                : (job.getDiscoveredAt() != null ? job.getDiscoveredAt() : job.getUpdatedAt());
                return effectiveDate != null && !effectiveDate.isBefore(postedAfter);
            });
        }

        if (filter.geographyType() != null) {
            stream = stream.filter(job ->
                    job.getGeographyType() == null || filter.geographyType().equals(job.getGeographyType()));
        }

        if (filter.workplaceType() != null) {
            stream = stream.filter(job ->
                    job.getWorkplaceType() == null || filter.workplaceType().equals(job.getWorkplaceType()));
        }

        if (filter.employmentType() != null) {
            stream = stream.filter(job ->
                    job.getEmploymentType() == null || filter.employmentType().equals(job.getEmploymentType()));
        }

        List<Job> targetJobs = stream.toList();

        Comparator<JobOpportunityResult> comparator;
        if ("freshness".equalsIgnoreCase(filter.sortBy()) || "date".equalsIgnoreCase(filter.sortBy())) {
            comparator = Comparator
                    .comparing(
                            (JobOpportunityResult result) -> {
                                OffsetDateTime dt = result.postedAt() != null ? result.postedAt() : result.discoveredAt();
                                return dt != null ? dt : OffsetDateTime.MIN;
                            },
                            Comparator.reverseOrder()
                    )
                    .thenComparing(
                            (JobOpportunityResult result) -> result.match().score(),
                            Comparator.reverseOrder()
                    )
                    .thenComparing(
                            JobOpportunityResult::jobId,
                            Comparator.reverseOrder()
                    );
        } else {
            comparator = Comparator
                    .comparing(
                            (JobOpportunityResult result) -> result.match().score(),
                            Comparator.reverseOrder()
                    )
                    .thenComparing(
                            (JobOpportunityResult result) -> {
                                OffsetDateTime dt = result.postedAt() != null ? result.postedAt() : result.discoveredAt();
                                return dt != null ? dt : OffsetDateTime.MIN;
                            },
                            Comparator.reverseOrder()
                    )
                    .thenComparing(
                            JobOpportunityResult::jobId,
                            Comparator.reverseOrder()
                    );
        }

        return targetJobs.parallelStream()
                .map(job ->
                        jobOpportunityService
                                .getOpportunity(job.getId()))
                .filter(result ->
                        matches(
                                result,
                                filter,
                                postedAfter,
                                jobMap
                        ))
                .sorted(comparator)
                .toList();
    }

    private boolean matches(
            JobOpportunityResult result,
            OpportunityFilter filter,
            OffsetDateTime postedAfter) {
        return matches(result, filter, postedAfter, null);
    }

    private boolean matches(
            JobOpportunityResult result,
            OpportunityFilter filter,
            OffsetDateTime postedAfter,
            java.util.Map<Long, Job> jobMap) {

        if (filter.search() != null && !filter.search().isBlank()) {
            String q = filter.search().trim().toLowerCase(java.util.Locale.ROOT);
            boolean titleMatch = result.title() != null && result.title().toLowerCase(java.util.Locale.ROOT).contains(q);
            boolean companyMatch = result.companyName() != null && result.companyName().toLowerCase(java.util.Locale.ROOT).contains(q);
            boolean locationMatch = result.location() != null && result.location().toLowerCase(java.util.Locale.ROOT).contains(q);
            boolean descMatch = result.description() != null && result.description().toLowerCase(java.util.Locale.ROOT).contains(q);
            boolean skillMatch = result.match() != null && (
                    (result.match().matchedSkills() != null && result.match().matchedSkills().stream().anyMatch(s -> s.toLowerCase(java.util.Locale.ROOT).contains(q))) ||
                    (result.match().missingSkills() != null && result.match().missingSkills().stream().anyMatch(s -> s.toLowerCase(java.util.Locale.ROOT).contains(q)))
            );

            if (!titleMatch && !companyMatch && !locationMatch && !descMatch && !skillMatch) {
                return false;
            }
        }

        if (filter.minScore() != null
                && result.match()
                .score()
                .doubleValue()
                < filter.minScore()) {

            return false;
        }

        if (filter.eligibilityStatus() != null
                && result.eligibility()
                .status()
                != filter.eligibilityStatus()) {

            return false;
        }

        if (filter.geographyType() != null
                && !filter.geographyType()
                .name()
                .equals(result.geographyType())) {

            return false;
        }

        if (filter.workplaceType() != null
                && !filter.workplaceType()
                .name()
                .equals(result.workplaceType())) {

            return false;
        }

        if (filter.employmentType() != null
                && !filter.employmentType()
                .name()
                .equals(result.employmentType())) {

            return false;
        }

        if (filter.roleFamily() != null
                && !filter.roleFamily()
                .name()
                .equals(result.roleFamily())) {

            return false;
        }

        if (postedAfter != null) {

            Job job = (jobMap != null && jobMap.containsKey(result.jobId()))
                    ? jobMap.get(result.jobId())
                    : jobRepository.findById(
                                    result.jobId()
                            )
                            .orElse(null);

            if (job == null) {
                return false;
            }

            /*
             * Greenhouse does not provide a reliable original
             * publication timestamp through the job-board
             * response currently consumed by Career Radar.
             *
             * Therefore:
             *
             * 1. Use postedAt when available.
             * 2. Otherwise, use updatedAt as the freshness
             *    fallback.
             *
             * We deliberately do NOT copy updatedAt into
             * postedAt because those timestamps have different
             * meanings.
             */
            OffsetDateTime effectiveDate =
                    job.getPostedAt() != null
                            ? job.getPostedAt()
                            : (job.getDiscoveredAt() != null ? job.getDiscoveredAt() : job.getUpdatedAt());

            if (effectiveDate == null
                    || effectiveDate.isBefore(postedAfter)) {

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
                .minusDays(
                        filter.postedWithinDays()
                );
    }
}