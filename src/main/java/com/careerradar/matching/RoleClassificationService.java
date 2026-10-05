package com.careerradar.matching;

import com.careerradar.job.Job;
import com.careerradar.job.JobRepository;
import com.careerradar.job.domain.RoleFamily;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class RoleClassificationService {

    private static final String CLASSIFIER_SOURCE = "RULE_BASED";

    private final JobRepository jobRepository;
    private final JobRoleClassificationRepository classificationRepository;
    private final RoleClassifier roleClassifier;

    public RoleClassificationService(
            JobRepository jobRepository,
            JobRoleClassificationRepository classificationRepository,
            RoleClassifier roleClassifier) {

        this.jobRepository = jobRepository;
        this.classificationRepository = classificationRepository;
        this.roleClassifier = roleClassifier;
    }

    @Transactional
    public int classifyAllJobs() {

        List<Job> jobs = jobRepository.findAll();

        int classified = 0;

        for (Job job : jobs) {

            RoleClassificationResult result =
                    roleClassifier.classify(
                            job.getTitle(),
                            job.getDescription()
                    );

            if (result.roleFamily() == RoleFamily.UNKNOWN) {
                continue;
            }

            upsertClassification(job, result);

            classified++;
        }

        return classified;
    }

    @Transactional
    public RoleClassificationResult classifyJob(Long jobId) {

        Job job =
                jobRepository.findById(jobId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Job not found: " + jobId
                                ));

        RoleClassificationResult result =
                roleClassifier.classify(
                        job.getTitle(),
                        job.getDescription()
                );

        if (result.roleFamily() != RoleFamily.UNKNOWN) {
            upsertClassification(job, result);
        }

        return result;
    }

    @Transactional(readOnly = true)
    public List<JobRoleClassification> getClassifications(
            Long jobId) {

        return classificationRepository
                .findByJobIdOrderByConfidenceDesc(jobId);
    }

    private void upsertClassification(
            Job job,
            RoleClassificationResult result) {

        classificationRepository
                .findByJobIdAndRoleFamily(
                        job.getId(),
                        result.roleFamily()
                )
                .ifPresentOrElse(
                        existing ->
                                existing.update(
                                        BigDecimal.valueOf(result.confidence()),
                                        CLASSIFIER_SOURCE
                                ),
                        () ->
                                classificationRepository.save(
                                        JobRoleClassification.create(
                                                job,
                                                result.roleFamily(),
                                                result.confidence(),
                                                CLASSIFIER_SOURCE
                                        )
                                )
                );
    }

    @Transactional
    public RoleClassificationResult classifyAndPersist(Job job) {

        RoleClassificationResult result =
                roleClassifier.classify(
                        job.getTitle(),
                        job.getDescription()
                );

        if (result.roleFamily() != RoleFamily.UNKNOWN) {
            upsertClassification(job, result);
        }

        return result;
    }
}