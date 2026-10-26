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

        List<Job> jobs =
                jobRepository.findAll();

        int classified = 0;

        for (Job job : jobs) {

            RoleClassificationResult result =
                    classifyAndPersist(job);

            if (result.roleFamily() != RoleFamily.UNKNOWN) {
                classified++;
            }
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
                                )
                        );

        return classifyAndPersist(job);
    }

    @Transactional(readOnly = true)
    public List<JobRoleClassification> getClassifications(
            Long jobId) {

        return classificationRepository
                .findByJobIdOrderByConfidenceDesc(jobId);
    }

    @Transactional
    public RoleClassificationResult classifyAndPersist(Job job) {

        RoleClassificationResult result =
                roleClassifier.classify(
                        job.getTitle(),
                        job.getDescription()
                );

        reconcileClassification(
                job,
                result
        );

        return result;
    }

    private void reconcileClassification(
            Job job,
            RoleClassificationResult result) {

        List<JobRoleClassification> existing =
                classificationRepository
                        .findByJobIdOrderByConfidenceDesc(
                                job.getId()
                        );

        /*
         * UNKNOWN means that the job has no positive role
         * classification.
         *
         * Remove stale classifications so that an old
         * JAVA_BACKEND classification cannot survive after
         * the classifier now determines UNKNOWN.
         */
        if (result.roleFamily() == RoleFamily.UNKNOWN) {

            if (!existing.isEmpty()) {
                classificationRepository.deleteAll(existing);
            }

            return;
        }

        /*
         * The classifier currently produces one winning role
         * family. Remove any previous competing classifications.
         */
        List<JobRoleClassification> stale =
                existing.stream()
                        .filter(existingClassification ->
                                existingClassification
                                        .getRoleFamily()
                                        != result.roleFamily()
                        )
                        .toList();

        if (!stale.isEmpty()) {
            classificationRepository.deleteAll(stale);
        }

        /*
         * Update the existing classification when the role family
         * is unchanged; otherwise create a new classification.
         */
        classificationRepository
                .findByJobIdAndRoleFamily(
                        job.getId(),
                        result.roleFamily()
                )
                .ifPresentOrElse(
                        existingClassification ->
                                existingClassification.update(
                                        BigDecimal.valueOf(
                                                result.confidence()
                                        ),
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
}