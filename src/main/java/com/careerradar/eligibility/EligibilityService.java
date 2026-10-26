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
import com.careerradar.profile.ProfileEmploymentPreferenceRepository;
import com.careerradar.profile.ProfileLocation;
import com.careerradar.profile.ProfileLocationRepository;
import com.careerradar.profile.ProfileWorkplacePreference;
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

    private final java.util.Map<Long, CachedPreferences> preferenceCache = new java.util.concurrent.ConcurrentHashMap<>();

    private record CachedPreferences(
            List<ProfileLocation> locations,
            List<ProfileWorkplacePreference> workplacePreferences,
            List<ProfileEmploymentPreference> employmentPreferences,
            long cachedAt
    ) {}

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

        long now = System.currentTimeMillis();
        CachedPreferences cached = preferenceCache.get(profileId);
        List<ProfileLocation> locations;
        List<ProfileWorkplacePreference> workplacePreferences;
        List<ProfileEmploymentPreference> employmentPreferences;

        if (cached != null && (now - cached.cachedAt() < 60_000)) {
            locations = cached.locations();
            workplacePreferences = cached.workplacePreferences();
            employmentPreferences = cached.employmentPreferences();
        } else {
            locations =
                    profileLocationRepository
                            .findByProfileIdAndActiveTrueOrderByPriorityAsc(
                                    profileId
                            );

            workplacePreferences =
                    workplacePreferenceRepository
                            .findByProfileIdAndActiveTrueOrderByPriorityAsc(
                                    profileId
                            );

            employmentPreferences =
                    employmentPreferenceRepository
                            .findByProfileIdAndActiveTrueOrderByPriorityAsc(
                                    profileId
                            );
            preferenceCache.put(profileId, new CachedPreferences(locations, workplacePreferences, employmentPreferences, now));
        }

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

        boolean geographyKnown =
                isGeographyKnown(job);

        boolean workplaceKnown =
                isWorkplaceKnown(job);

        boolean employmentKnown =
                isEmploymentKnown(job);

        List<String> reasons =
                new ArrayList<>();

        EligibilityStatus status =
                determineStatus(
                        locations,
                        workplacePreferences,
                        employmentPreferences,
                        geographyMatch,
                        workplaceMatch,
                        employmentMatch,
                        geographyKnown,
                        workplaceKnown,
                        employmentKnown,
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
            List<ProfileLocation> locations,
            List<ProfileWorkplacePreference>
                    workplacePreferences,
            List<ProfileEmploymentPreference>
                    employmentPreferences,
            boolean geographyMatch,
            boolean workplaceMatch,
            boolean employmentMatch,
            boolean geographyKnown,
            boolean workplaceKnown,
            boolean employmentKnown,
            List<String> reasons) {

        /*
         * An empty preference list means that dimension
         * is unrestricted.
         */
        if (locations.isEmpty()) {
            geographyMatch = true;
        }

        if (workplacePreferences.isEmpty()) {
            workplaceMatch = true;
        }

        if (employmentPreferences.isEmpty()) {
            employmentMatch = true;
        }

        /*
         * Only explicit, known conflicts make a job
         * NOT_ELIGIBLE.
         *
         * Unknown values are intentionally not treated
         * as mismatches.
         */
        boolean geographyConflict =
                geographyKnown && !geographyMatch;

        boolean workplaceConflict =
                workplaceKnown && !workplaceMatch;

        boolean employmentConflict =
                employmentKnown && !employmentMatch;

        if (geographyConflict) {
            reasons.add(
                    "Job geography does not match the profile preferences"
            );
        } else if (geographyKnown) {
            reasons.add(
                    "Job geography matches the profile preferences"
            );
        } else {
            reasons.add(
                    "Job geography is unknown"
            );
        }

        if (workplaceConflict) {
            reasons.add(
                    "Job workplace type does not match the profile preferences"
            );
        } else if (workplaceKnown) {
            reasons.add(
                    "Job workplace type matches the profile preferences"
            );
        } else {
            reasons.add(
                    "Job workplace type is unknown"
            );
        }

        if (employmentConflict) {
            reasons.add(
                    "Job employment type does not match the profile preferences"
            );
        } else if (employmentKnown) {
            reasons.add(
                    "Job employment type matches the profile preferences"
            );
        } else {
            reasons.add(
                    "Job employment type is unknown"
            );
        }

        /*
         * Explicit known conflicts always take precedence.
         */
        if (geographyConflict
                || workplaceConflict
                || employmentConflict) {

            return EligibilityStatus.NOT_ELIGIBLE;
        }

        /*
         * If any restricted dimension is unknown,
         * keep the opportunity reviewable.
         */
        boolean unknownDimension =
                (!geographyKnown && !locations.isEmpty())
                        || (!workplaceKnown
                        && !workplacePreferences.isEmpty())
                        || (!employmentKnown
                        && !employmentPreferences.isEmpty());

        if (unknownDimension) {
            return EligibilityStatus.MAYBE;
        }

        /*
         * No geographic restriction means the opportunity
         * is globally acceptable from the profile's point
         * of view.
         */
        if (locations.isEmpty()) {
            return EligibilityStatus.GLOBAL;
        }

        return EligibilityStatus.REGION_OK;
    }

    private boolean isGeographyKnown(Job job) {

        return job.getGeographyType() != null;
    }

    private boolean isWorkplaceKnown(Job job) {

        return job.getWorkplaceType() != null
                && job.getWorkplaceType()
                != WorkplaceType.UNKNOWN;
    }

    private boolean isEmploymentKnown(Job job) {

        return job.getEmploymentType() != null;
    }

    private boolean matchesGeography(
            Job job,
            List<ProfileLocation> locations) {

        if (locations.isEmpty()) {
            return true;
        }

        /*
         * Unknown geography is not a mismatch.
         * It will be represented by MAYBE later.
         */
        if (!isGeographyKnown(job)) {
            return true;
        }

        for (ProfileLocation location :
                locations) {

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

        /*
         * Unknown workplace is not a mismatch.
         */
        if (jobWorkplace == null
                || jobWorkplace == WorkplaceType.UNKNOWN) {

            return true;
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

        /*
         * Unknown employment type is not a mismatch.
         */
        if (jobEmployment == null) {
            return true;
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