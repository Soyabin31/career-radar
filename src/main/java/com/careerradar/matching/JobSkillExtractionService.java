package com.careerradar.matching;

import com.careerradar.job.Job;
import com.careerradar.job.JobRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class JobSkillExtractionService {

    private static final String EXTRACTOR_SOURCE =
            "RULE_BASED";

    private final JobRepository jobRepository;
    private final SkillRepository skillRepository;
    private final JobSkillRepository jobSkillRepository;
    private final SkillExtractor skillExtractor;

    public JobSkillExtractionService(
            JobRepository jobRepository,
            SkillRepository skillRepository,
            JobSkillRepository jobSkillRepository,
            SkillExtractor skillExtractor) {

        this.jobRepository = jobRepository;
        this.skillRepository = skillRepository;
        this.jobSkillRepository = jobSkillRepository;
        this.skillExtractor = skillExtractor;
    }

    @Transactional
    public int extractAllJobs() {

        List<Job> jobs =
                jobRepository.findAll();

        List<Skill> skills =
                skillRepository.findByActiveTrue();

        int extracted = 0;

        for (Job job : jobs) {
            extracted +=
                    extractAndPersist(
                            job,
                            skills
                    );
        }

        return extracted;
    }

    @Transactional
    public int extractJob(Long jobId) {

        Job job =
                jobRepository.findById(jobId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Job not found: " + jobId
                                )
                        );

        List<Skill> skills =
                skillRepository.findByActiveTrue();

        return extractAndPersist(
                job,
                skills
        );
    }

    @Transactional
    public int extractAndPersist(Job job) {

        List<Skill> skills =
                skillRepository.findByActiveTrue();

        return extractAndPersist(
                job,
                skills
        );
    }

    private int extractAndPersist(
            Job job,
            List<Skill> knownSkills) {

        List<SkillExtractor.ExtractedSkill> extractedSkills =
                skillExtractor.extract(
                        job.getTitle(),
                        job.getDescription(),
                        knownSkills
                );

        for (SkillExtractor.ExtractedSkill item :
                extractedSkills) {

            JobSkill jobSkill =
                    jobSkillRepository
                            .findByJobIdAndSkillId(
                                    job.getId(),
                                    item.skill().getId()
                            )
                            .orElse(null);

            if (jobSkill != null) {

                jobSkill.update(
                        item.required(),
                        item.confidence(),
                        EXTRACTOR_SOURCE
                );

            } else {

                jobSkillRepository.save(
                        JobSkill.create(
                                job,
                                item.skill(),
                                item.required(),
                                item.confidence(),
                                EXTRACTOR_SOURCE
                        )
                );
            }
        }

        return extractedSkills.size();
    }
}