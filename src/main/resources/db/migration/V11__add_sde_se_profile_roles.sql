-- V11: Add SDE and SE profile roles for candidate profile (SDE II with 8+ years experience)

INSERT INTO profile_roles (profile_id, role_name, role_family, priority, active)
SELECT p.id, r.role_name, r.role_family, r.priority, true
FROM profiles p
CROSS JOIN (
    VALUES
        ('SDE II', 'SOFTWARE_ENGINEER', 2),
        ('Software Development Engineer II', 'SOFTWARE_ENGINEER', 2),
        ('Senior SDE', 'SOFTWARE_ENGINEER', 3),
        ('SDE III', 'SOFTWARE_ENGINEER', 3),
        ('Backend SDE', 'BACKEND_ENGINEER', 4),
        ('Backend SDE II', 'BACKEND_ENGINEER', 4),
        ('SE II', 'SOFTWARE_ENGINEER', 5),
        ('Senior SE', 'SOFTWARE_ENGINEER', 5),
        ('Software Development Engineer', 'SOFTWARE_ENGINEER', 9),
        ('SDE', 'SOFTWARE_ENGINEER', 9),
        ('SE', 'SOFTWARE_ENGINEER', 9)
) AS r(role_name, role_family, priority)
WHERE p.name = 'Primary Career Profile'
ON CONFLICT (profile_id, role_name) DO NOTHING;
