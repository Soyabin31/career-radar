package com.careerradar.eligibility;

import com.careerradar.job.domain.EmploymentType;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class EmploymentTypeNormalizer {

    public EmploymentType normalize(
            String title,
            String description) {

        String combined =
                (safe(title) + " " + safe(description))
                        .toLowerCase(Locale.ROOT);

        /*
         * Explicit contract/freelance/consulting language should
         * take precedence over generic "full time" language.
         */
        if (containsAny(
                combined,
                "freelance",
                "freelancer",
                "independent contractor"
        )) {
            return EmploymentType.FREELANCE;
        }

        if (containsAny(
                combined,
                "consulting",
                "consultant"
        )) {
            return EmploymentType.CONSULTING;
        }

        if (containsAny(
                combined,
                "contract",
                "contractor",
                "contract position",
                "contract role",
                "fixed term"
        )) {
            return EmploymentType.CONTRACT;
        }

        if (containsAny(
                combined,
                "part time",
                "part-time",
                "parttime"
        )) {
            return EmploymentType.PART_TIME;
        }

        if (containsAny(
                combined,
                "project based",
                "project-based",
                "project role"
        )) {
            return EmploymentType.PROJECT;
        }

        if (containsAny(
                combined,
                "full time",
                "full-time",
                "fulltime",
                "permanent",
                "regular employee"
        )) {
            return EmploymentType.FULL_TIME;
        }

        /*
         * null means unknown, not ineligible.
         */
        return null;
    }

    private boolean containsAny(
            String value,
            String... phrases) {

        for (String phrase : phrases) {

            if (value.contains(phrase)) {
                return true;
            }
        }

        return false;
    }

    private String safe(String value) {

        return value == null
                ? ""
                : value.trim();
    }
}