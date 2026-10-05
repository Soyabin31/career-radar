/*
 * Default Career Radar profile preferences.
 *
 * This migration configures the existing
 * "Primary Career Profile".
 *
 * Preferences are intentionally data-driven.
 * EligibilityService reads these values rather
 * than hardcoding the user's preferences.
 */


/*
 * ---------------------------------------------------------
 * Location preferences
 * ---------------------------------------------------------
 *
 * India:
 *   Bengaluru
 *   Pune
 *   Kolkata
 *   Bhubaneswar
 *
 * Foreign:
 *   APAC
 *   Europe
 *   UK
 *   USA
 *   Other
 */

INSERT INTO profile_locations (
    profile_id,
    geography_type,
    foreign_region,
    country,
    state_or_region,
    city,
    priority,
    active
)
SELECT
    p.id,
    x.geography_type,
    x.foreign_region,
    x.country,
    x.state_or_region,
    x.city,
    x.priority,
    TRUE
FROM profiles p
         CROSS JOIN (
    VALUES
        (
            'INDIA',
            NULL,
            'India',
            NULL,
            'Bengaluru',
            1
        ),
        (
            'INDIA',
            NULL,
            'India',
            NULL,
            'Pune',
            2
        ),
        (
            'INDIA',
            NULL,
            'India',
            NULL,
            'Kolkata',
            3
        ),
        (
            'INDIA',
            NULL,
            'India',
            'Odisha',
            'Bhubaneswar',
            4
        ),
        (
            'FOREIGN',
            'APAC',
            NULL,
            NULL,
            NULL,
            5
        ),
        (
            'FOREIGN',
            'EUROPE',
            NULL,
            NULL,
            NULL,
            6
        ),
        (
            'FOREIGN',
            'UK',
            NULL,
            NULL,
            NULL,
            7
        ),
        (
            'FOREIGN',
            'USA',
            NULL,
            NULL,
            NULL,
            8
        ),
        (
            'FOREIGN',
            'OTHER',
            NULL,
            NULL,
            NULL,
            9
        )
) AS x(
       geography_type,
       foreign_region,
       country,
       state_or_region,
       city,
       priority
    )
WHERE p.name = 'Primary Career Profile';


/*
 * ---------------------------------------------------------
 * Workplace preferences
 * ---------------------------------------------------------
 *
 * All three are supported because workplace is an
 * independent eligibility dimension.
 *
 * This also allows foreign onsite opportunities.
 */

INSERT INTO profile_workplace_preferences (
    profile_id,
    workplace_type,
    priority,
    active
)
SELECT
    p.id,
    x.workplace_type,
    x.priority,
    TRUE
FROM profiles p
         CROSS JOIN (
    VALUES
        ('REMOTE', 1),
        ('HYBRID', 2),
        ('ONSITE', 3)
) AS x(
       workplace_type,
       priority
    )
WHERE p.name = 'Primary Career Profile';


/*
 * ---------------------------------------------------------
 * Employment preferences
 * ---------------------------------------------------------
 *
 * All supported employment types are represented.
 */

INSERT INTO profile_employment_preferences (
    profile_id,
    employment_type,
    priority,
    active
)
SELECT
    p.id,
    x.employment_type,
    x.priority,
    TRUE
FROM profiles p
         CROSS JOIN (
    VALUES
        ('FULL_TIME', 1),
        ('CONTRACT', 2),
        ('FREELANCE', 3),
        ('CONSULTING', 4),
        ('PROJECT', 5),
        ('PART_TIME', 6)
) AS x(
       employment_type,
       priority
    )
WHERE p.name = 'Primary Career Profile';