package com.careerradar.eligibility;

import com.careerradar.job.domain.ForeignRegion;
import com.careerradar.job.domain.GeographyType;
import com.careerradar.job.domain.WorkplaceType;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class LocationNormalizer {

    public NormalizedLocation normalize(
            String location,
            String country,
            String city) {

        String combined = String.join(
                " ",
                safe(location),
                safe(country),
                safe(city)
        ).toLowerCase(Locale.ROOT);

        WorkplaceType workplaceType =
                detectWorkplaceType(combined);

        GeographyType geographyType =
                detectGeographyType(combined);

        ForeignRegion foreignRegion =
                detectForeignRegion(combined);

        return new NormalizedLocation(
                geographyType,
                foreignRegion,
                workplaceType,
                normalizeCountry(country),
                normalizeCity(city)
        );
    }

    private WorkplaceType detectWorkplaceType(String value) {

        if (containsAny(
                value,
                "remote",
                "work from home",
                "distributed"
        )) {
            return WorkplaceType.REMOTE;
        }

        if (containsAny(
                value,
                "hybrid"
        )) {
            return WorkplaceType.HYBRID;
        }

        if (containsAny(
                value,
                "onsite",
                "on-site"
        )) {
            return WorkplaceType.ONSITE;
        }

        return WorkplaceType.UNKNOWN;
    }

    private GeographyType detectGeographyType(String value) {

        if (containsAny(
                value,
                "india",
                "bengaluru",
                "bangalore",
                "pune",
                "kolkata",
                "bhubaneswar",
                "odisha"
        )) {
            return GeographyType.INDIA;
        }

        return GeographyType.FOREIGN;
    }

    private ForeignRegion detectForeignRegion(String value) {

        if (containsAny(
                value,
                "uk",
                "uk&i",
                "united kingdom",
                "england",
                "scotland",
                "wales",
                "northern ireland"
        )) {
            return ForeignRegion.UK;
        }

        if (containsAny(
                value,
                "europe",
                "emea",
                "germany",
                "france",
                "spain",
                "italy",
                "netherlands",
                "ireland",
                "portugal",
                "poland"
        )) {
            return ForeignRegion.EUROPE;
        }

        if (containsAny(
                value,
                "usa",
                "united states",
                "america",
                "amer"
        )) {
            return ForeignRegion.USA;
        }

        if (containsAny(
                value,
                "apac",
                "asia",
                "australia",
                "singapore",
                "japan",
                "india"
        )) {
            return ForeignRegion.APAC;
        }

        return ForeignRegion.OTHER;
    }

    private String normalizeCountry(String country) {
        if (country == null || country.isBlank()) {
            return null;
        }

        return country.trim();
    }

    private String normalizeCity(String city) {
        if (city == null || city.isBlank()) {
            return null;
        }

        return city.trim();
    }

    private boolean containsAny(
            String value,
            String... candidates) {

        for (String candidate : candidates) {
            if (value.contains(candidate)) {
                return true;
            }
        }

        return false;
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    public record NormalizedLocation(
            GeographyType geographyType,
            ForeignRegion foreignRegion,
            WorkplaceType workplaceType,
            String country,
            String city
    ) {
    }
}