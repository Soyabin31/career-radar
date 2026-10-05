package com.careerradar.matching;

import com.careerradar.job.Job;
import com.careerradar.job.JobRepository;
import com.careerradar.job.domain.RoleFamily;
import com.careerradar.profile.Profile;
import com.careerradar.profile.ProfileConfigurationService;
import com.careerradar.profile.ProfileRole;
import com.careerradar.profile.ProfileSkill;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class JobMatchingService {

    private final JobRepository jobRepository;
    private final JobRoleClassificationRepository
            roleClassificationRepository;
    private final JobSkillRepository jobSkillRepository;
    private final ProfileConfigurationService
            profileConfigurationService;

    public JobMatchingService(
            JobRepository jobRepository,
            JobRoleClassificationRepository
                    roleClassificationRepository,
            JobSkillRepository jobSkillRepository,
            ProfileConfigurationService
                    profileConfigurationService) {

        this.jobRepository = jobRepository;
        this.roleClassificationRepository =
                roleClassificationRepository;
        this.jobSkillRepository =
                jobSkillRepository;
        this.profileConfigurationService =
                profileConfigurationService;
    }

    @Transactional(readOnly = true)
    public JobMatchResult matchJob(Long jobId) {

        Job job =
                jobRepository.findById(jobId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Job not found: " + jobId
                                )
                        );

        Profile profile =
                profileConfigurationService
                        .getActiveProfile();

        List<ProfileRole> profileRoles =
                profileConfigurationService
                        .getRoles(profile.getId());

        List<ProfileSkill> profileSkills =
                profileConfigurationService
                        .getSkills(profile.getId());

        List<JobRoleClassification> classifications =
                roleClassificationRepository
                        .findByJobIdOrderByConfidenceDesc(jobId);

        List<JobSkill> jobSkills =
                jobSkillRepository
                        .findByJobIdOrderByConfidenceDesc(jobId);

        RoleFamily jobRoleFamily =
                classifications.isEmpty()
                        ? RoleFamily.UNKNOWN
                        : classifications.get(0)
                        .getRoleFamily();

        boolean roleMatch =
                isRoleMatch(
                        jobRoleFamily,
                        profileRoles
                );

        Set<String> profileSkillNames =
                new HashSet<>();

        Set<String> secondarySkillNames =
                new HashSet<>();

        for (ProfileSkill profileSkill :
                profileSkills) {

            if (!Boolean.TRUE.equals(
                    profileSkill.getActive())) {
                continue;
            }

            String normalizedName =
                    profileSkill.getSkill()
                            .getNormalizedName();

            profileSkillNames.add(
                    normalizedName
            );

            if ("SECONDARY".equals(
                    profileSkill.getSkillType())) {

                secondarySkillNames.add(
                        normalizedName
                );
            }
        }

        List<String> matchedSkills =
                new ArrayList<>();

        List<String> missingSkills =
                new ArrayList<>();

        List<String> supportingSkills =
                new ArrayList<>();

        double totalWeight = 0.0;
        double matchedWeight = 0.0;

        for (JobSkill jobSkill :
                jobSkills) {

            if (!Boolean.TRUE.equals(
                    jobSkill.getRequired())) {
                continue;
            }

            String skillName =
                    jobSkill.getSkill()
                            .getNormalizedName();

            double confidence =
                    jobSkill.getConfidence()
                            .doubleValue();

            double skillWeight =
                    determineSkillWeight(
                            skillName,
                            profileSkills
                    );

            totalWeight +=
                    skillWeight * confidence;

            if (profileSkillNames.contains(
                    skillName)) {

                matchedWeight +=
                        skillWeight * confidence;

                matchedSkills.add(
                        jobSkill.getSkill().getName()
                );

            } else {

                missingSkills.add(
                        jobSkill.getSkill().getName()
                );
            }
        }

        for (JobSkill jobSkill :
                jobSkills) {

            if (Boolean.TRUE.equals(
                    jobSkill.getRequired())) {
                continue;
            }

            String skillName =
                    jobSkill.getSkill()
                            .getNormalizedName();

            if (secondarySkillNames.contains(
                    skillName)) {

                supportingSkills.add(
                        jobSkill.getSkill().getName()
                );
            }
        }

        double technologyFit =
                totalWeight == 0.0
                        ? 0.0
                        : matchedWeight / totalWeight;

        double roleFit =
                roleMatch ? 1.0 : 0.0;

        /*
         * Role fit deliberately has the larger weight.
         *
         * This prevents a Python/Data/DevOps job with
         * overlapping infrastructure skills from becoming
         * a strong Java match.
         */
        double finalScore =
                (roleFit * 0.60)
                        + (technologyFit * 0.40);

        BigDecimal score =
                BigDecimal.valueOf(
                                finalScore * 100.0
                        )
                        .setScale(
                                2,
                                RoundingMode.HALF_UP
                        );

        return new JobMatchResult(
                job.getId(),
                job.getTitle(),
                jobRoleFamily.name(),
                score,
                roleMatch,
                matchedSkills,
                missingSkills,
                supportingSkills
        );
    }

    private boolean isRoleMatch(
            RoleFamily jobRoleFamily,
            List<ProfileRole> profileRoles) {

        for (ProfileRole profileRole :
                profileRoles) {

            if (!Boolean.TRUE.equals(
                    profileRole.getActive())) {
                continue;
            }

            /*
             * Both values are RoleFamily enums.
             *
             * Do not compare RoleFamily with String.
             */
            if (profileRole.getRoleFamily()
                    == jobRoleFamily) {

                return true;
            }
        }

        return false;
    }

    private double determineSkillWeight(
            String skillName,
            List<ProfileSkill> profileSkills) {

        for (ProfileSkill profileSkill :
                profileSkills) {

            if (!Boolean.TRUE.equals(
                    profileSkill.getActive())) {
                continue;
            }

            if (profileSkill.getSkill()
                    .getNormalizedName()
                    .equals(skillName)) {

                return profileSkill.getWeight()
                        .doubleValue();
            }
        }

        /*
         * Unknown job skills still contribute to the
         * denominator, but with a neutral weight.
         */
        return 1.0;
    }
}