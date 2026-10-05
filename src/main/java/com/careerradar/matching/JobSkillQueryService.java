package com.careerradar.matching;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class JobSkillQueryService {

    private final JobSkillRepository jobSkillRepository;

    public JobSkillQueryService(
            JobSkillRepository jobSkillRepository) {

        this.jobSkillRepository =
                jobSkillRepository;
    }

    @Transactional(readOnly = true)
    public List<JobSkill> getSkillsForJob(
            Long jobId) {

        return jobSkillRepository
                .findByJobIdOrderByConfidenceDesc(jobId);
    }
}