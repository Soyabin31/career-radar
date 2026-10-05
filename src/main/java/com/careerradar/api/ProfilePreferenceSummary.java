package com.careerradar.api;

import java.util.List;

public record ProfilePreferenceSummary(
        Long profileId,
        String profileName,
        List<LocationPreference> locations,
        List<String> workplaceTypes,
        List<String> employmentTypes
) {

    public record LocationPreference(
            String geographyType,
            String foreignRegion,
            String country,
            String stateOrRegion,
            String city,
            Integer priority
    ) {
    }
}