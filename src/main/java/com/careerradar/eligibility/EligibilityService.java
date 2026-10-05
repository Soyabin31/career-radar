package com.careerradar.eligibility;

import com.careerradar.job.Job;
import com.careerradar.job.JobRepository;
import com.careerradar.job.domain.EligibilityStatus;
import com.careerradar.job.domain.EmploymentType;
import com.careerradar.job.domain.ForeignRegion;
import com.careerradar.job.domain.GeographyType;
import com.careerradar.job.domain.WorkplaceType;
import com.careerradar.profile.Profile;
import com.careerradar.profile.ProfileConfigurationService;
import com.careerradar.profile.ProfileEmploymentPreference;
import com.careerradar.profile.ProfileLocation;
import com.careerradar.profile.ProfileWorkplacePreference;
import com.careerradar.profile.ProfileLocationRepository;
import com.careerradar.profile.ProfileEmploymentPreferenceRepository;
import com.careerradar.profile.ProfileWorkplacePreferenceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class EligibilityService {

    private final JobRepository jobRepository;
    private final ProfileConfigurationService
            profileConfigurationService;
    private final ProfileLocationRepository
            profileLocationRepository;
    private final ProfileWorkplacePreferenceRepository
            workplacePreferenceRepository;
    private final ProfileEmploymentPreferenceRepository
            employmentPreferenceRepository;

    public EligibilityService(
            JobRepository jobRepository,
            ProfileConfigurationService
                    profileConfigurationService,
            ProfileLocationRepository
                    profileLocationRepository,
            ProfileWorkplacePreferenceRepository
                    workplacePreferenceRepository,
            ProfileEmploymentPreferenceRepository
                    employmentPreferenceRepository) {

        this.jobRepository = jobRepository;
        this.profileConfigurationService =
                profileConfigurationService;
        this.profileLocationRepository =
                profileLocationRepository;
        this.workplacePreferenceRepository =
                workplacePreferenceRepository;
        this.employmentPreferenceRepository =
                employmentPreferenceRepository;
    }

    @Transactional(readOnly = true)
    public EligibilityResult evaluateJob(Long jobId) {

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

        Long profileId =
                profile.getId();

        List<ProfileLocation> locations =
                profileLocationRepository
                        .findByProfileIdAndActiveTrueOrderByPriorityAsc(
                                profileId
                        );

        List<ProfileWorkplacePreference>
                workplacePreferences =
                workplacePreferenceRepository
                        .findByProfileIdAndActiveTrueOrderByPriorityAsc(
                                profileId
                        );

        List<ProfileEmploymentPreference>
                employmentPreferences =
                employmentPreferenceRepository
                        .findByProfileIdAndActiveTrueOrderByPriorityAsc(
                                profileId
                        );

        boolean geographyMatch =
                matchesGeography(
                        job,
                        locations
                );

        boolean workplaceMatch =
                matchesWorkplace(
                        job.getWorkplaceType(),
                        workplacePreferences
                );

        boolean employmentMatch =
                matchesEmployment(
                        job.getEmploymentType(),
                        employmentPreferences
                );

        List<String> reasons =
                new ArrayList<>();

        EligibilityStatus status =
                determineStatus(
                        job,
                        locations,
                        workplacePreferences,
                        employmentPreferences,
                        geographyMatch,
                        workplaceMatch,
                        employmentMatch,
                        reasons
                );

        return new EligibilityResult(
                job.getId(),
                status,
                geographyMatch,
                workplaceMatch,
                employmentMatch,
                reasons
        );
    }

    private EligibilityStatus determineStatus(
            Job job,
            List<ProfileLocation> locations,
            List<ProfileWorkplacePreference>
                    workplacePreferences,
            List<ProfileEmploymentPreference>
                    employmentPreferences,
            boolean geographyMatch,
            boolean workplaceMatch,
            boolean employmentMatch,
            List<String> reasons) {

        boolean geographyKnown =
                job.getGeographyType() != null;

        boolean workplaceKnown =
                job.getWorkplaceType() != null
                        && job.getWorkplaceType()
                        != WorkplaceType.UNKNOWN;

        boolean employmentKnown =
                job.getEmploymentType() != null;

        /*
         * No geographic preferences means we do not
         * restrict the job by location.
         */
        if (locations.isEmpty()) {
            geographyMatch = true;
        }

        /*
         * No workplace preferences means we do not
         * restrict the job by workplace type.
         */
        if (workplacePreferences.isEmpty()) {
            workplaceMatch = true;
        }

        /*
         * No employment preferences means we do not
         * restrict the job by employment type.
         */
        if (employmentPreferences.isEmpty()) {
            employmentMatch = true;
        }

        if (geographyMatch) {
            reasons.add(
                    "Job geography matches the profile preferences"
            );
        } else if (geographyKnown) {
            reasons.add(
                    "Job geography does not match the profile preferences"
            );
        }

        if (workplaceMatch) {
            reasons.add(
                    "Job workplace type matches the profile preferences"
            );
        } else if (workplaceKnown) {
            reasons.add(
                    "Job workplace type does not match the profile preferences"
            );
        }

        if (employmentMatch) {
            reasons.add(
                    "Job employment type matches the profile preferences"
            );
        } else if (employmentKnown) {
            reasons.add(
                    "Job employment type does not match the profile preferences"
            );
        }

        /*
         * Explicit conflicts take precedence over ambiguity.
         */
        if (!geographyMatch
                || !workplaceMatch
                || !employmentMatch) {

            return EligibilityStatus.NOT_ELIGIBLE;
        }

        /*
         * If all known dimensions match but one or more
         * dimensions are unknown, keep the job reviewable.
         */
        if (!geographyKnown
                || !workplaceKnown
                || !employmentKnown) {

            return EligibilityStatus.MAYBE;
        }

        /*
         * No geographic restriction and all known
         * dimensions are acceptable.
         */
        if (locations.isEmpty()
                && workplaceMatch
                && employmentMatch) {

            return EligibilityStatus.GLOBAL;
        }

        return EligibilityStatus.REGION_OK;
    }

    private boolean matchesGeography(
            Job job,
            List<ProfileLocation> locations) {

        if (locations.isEmpty()) {
            return true;
        }

        if (job.getGeographyType() == null) {
            return false;
        }

        for (ProfileLocation location : locations) {

            if (matchesProfileLocation(
                    job,
                    location)) {

                return true;
            }
        }

        return false;
    }

    private boolean matchesProfileLocation(
            Job job,
            ProfileLocation profileLocation) {

        GeographyType profileGeography =
                profileLocation.getGeographyType();

        if (profileGeography == null) {
            return false;
        }

        if (profileGeography
                != job.getGeographyType()) {

            return false;
        }

        if (profileGeography
                == GeographyType.INDIA) {

            return matchesIndiaLocation(
                    job,
                    profileLocation
            );
        }

        return matchesForeignLocation(
                job,
                profileLocation
        );
    }

    private boolean matchesIndiaLocation(
            Job job,
            ProfileLocation profileLocation) {

        String profileCountry =
                normalize(
                        profileLocation.getCountry()
                );

        String jobCountry =
                normalize(
                        job.getCountry()
                );

        if (!profileCountry.isBlank()
                && !jobCountry.isBlank()
                && profileCountry.equals(jobCountry)) {

            String profileCity =
                    normalize(
                            profileLocation.getCity()
                    );

            String jobCity =
                    normalize(
                            job.getCity()
                    );

            /*
             * Country-level preference matches every
             * job in that country.
             */
            if (profileCity.isBlank()) {
                return true;
            }

            return !jobCity.isBlank()
                    && profileCity.equals(jobCity);
        }

        /*
         * If country was not populated on one side,
         * compare city as a fallback.
         */
        String profileCity =
                normalize(
                        profileLocation.getCity()
                );

        String jobCity =
                normalize(
                        job.getCity()
                );

        return !profileCity.isBlank()
                && !jobCity.isBlank()
                && profileCity.equals(jobCity);
    }

    private boolean matchesForeignLocation(
            Job job,
            ProfileLocation profileLocation) {

        ForeignRegion profileRegion =
                profileLocation.getForeignRegion();

        if (profileRegion == null) {
            return false;
        }

        /*
         * A foreign-region preference is intentionally
         * region based rather than country based.
         */
        if (job.getForeignRegion() == null) {
            return false;
        }

        if (profileRegion
                != job.getForeignRegion()) {

            return false;
        }

        String profileCountry =
                normalize(
                        profileLocation.getCountry()
                );

        if (profileCountry.isBlank()) {
            return true;
        }

        String jobCountry =
                normalize(
                        job.getCountry()
                );

        return profileCountry.equals(jobCountry);
    }

    private boolean matchesWorkplace(
            WorkplaceType jobWorkplace,
            List<ProfileWorkplacePreference>
                    preferences) {

        if (preferences.isEmpty()) {
            return true;
        }

        if (jobWorkplace == null
                || jobWorkplace == WorkplaceType.UNKNOWN) {

            return false;
        }

        for (ProfileWorkplacePreference preference :
                preferences) {

            if (!Boolean.TRUE.equals(
                    preference.getActive())) {
                continue;
            }

            if (preference.getWorkplaceType()
                    == jobWorkplace) {

                return true;
            }
        }

        return false;
    }

    private boolean matchesEmployment(
            EmploymentType jobEmployment,
            List<ProfileEmploymentPreference>
                    preferences) {

        if (preferences.isEmpty()) {
            return true;
        }

        if (jobEmployment == null) {
            return false;
        }

        for (ProfileEmploymentPreference preference :
                preferences) {

            if (!Boolean.TRUE.equals(
                    preference.getActive())) {
                continue;
            }

            if (preference.getEmploymentType()
                    == jobEmployment) {

                return true;
            }
        }

        return false;
    }

    private String normalize(String value) {

        if (value == null) {
            return "";
        }

        return value
                .trim()
                .toLowerCase();
    }
}