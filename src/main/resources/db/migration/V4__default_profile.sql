INSERT INTO profiles (name, active)
VALUES ('Primary Career Profile', TRUE);

INSERT INTO skills (name, normalized_name, category)
VALUES
    ('Java', 'java', 'PROGRAMMING_LANGUAGE'),
    ('Spring Boot', 'spring_boot', 'FRAMEWORK'),
    ('Spring Security', 'spring_security', 'FRAMEWORK'),
    ('REST', 'rest', 'API'),
    ('Microservices', 'microservices', 'ARCHITECTURE'),
    ('Distributed Systems', 'distributed_systems', 'ARCHITECTURE'),
    ('PostgreSQL', 'postgresql', 'DATABASE'),
    ('MySQL', 'mysql', 'DATABASE'),
    ('Kafka', 'kafka', 'MESSAGING'),
    ('Google Pub/Sub', 'google_pubsub', 'MESSAGING'),
    ('Kubernetes', 'kubernetes', 'CLOUD'),
    ('Docker', 'docker', 'CLOUD'),
    ('AWS', 'aws', 'CLOUD'),
    ('GCP', 'gcp', 'CLOUD'),
    ('Terraform', 'terraform', 'DEVOPS'),
    ('OAuth2', 'oauth2', 'SECURITY'),
    ('JWT', 'jwt', 'SECURITY'),
    ('Python', 'python', 'PROGRAMMING_LANGUAGE'),
    ('Pandas', 'pandas', 'PYTHON'),
    ('FastAPI', 'fastapi', 'PYTHON')
    ON CONFLICT (normalized_name) DO NOTHING;

INSERT INTO profile_roles
(profile_id, role_name, role_family, priority)
SELECT
    p.id,
    r.role_name,
    r.role_family,
    r.priority
FROM profiles p
         CROSS JOIN (
    VALUES
        ('Senior Backend Engineer', 'JAVA_BACKEND', 1),
        ('Senior Java Developer', 'JAVA_BACKEND', 2),
        ('Java Backend Engineer', 'JAVA_BACKEND', 3),
        ('Backend Engineer', 'BACKEND_ENGINEER', 4),
        ('Senior Software Engineer', 'SOFTWARE_ENGINEER', 5),
        ('Lead Backend Engineer', 'JAVA_BACKEND', 6),
        ('Tech Lead', 'JAVA_PLATFORM', 7),
        ('Platform Engineer', 'JAVA_PLATFORM', 8),
        ('Software Engineer', 'SOFTWARE_ENGINEER', 9)
) AS r(role_name, role_family, priority)
WHERE p.name = 'Primary Career Profile';

INSERT INTO profile_skills
(profile_id, skill_id, skill_type, weight)
SELECT
    p.id,
    s.id,
    CASE
        WHEN s.normalized_name IN (
                                   'java',
                                   'spring_boot',
                                   'spring_security',
                                   'rest',
                                   'microservices',
                                   'distributed_systems',
                                   'postgresql',
                                   'mysql',
                                   'kafka',
                                   'google_pubsub',
                                   'kubernetes',
                                   'docker',
                                   'aws',
                                   'gcp',
                                   'terraform',
                                   'oauth2',
                                   'jwt'
            )
            THEN 'PRIMARY'
        ELSE 'SECONDARY'
        END,
    CASE
        WHEN s.normalized_name = 'java' THEN 1.50
        WHEN s.normalized_name = 'spring_boot' THEN 1.40
        WHEN s.normalized_name = 'spring_security' THEN 1.30
        WHEN s.normalized_name IN ('microservices', 'distributed_systems') THEN 1.30
        WHEN s.normalized_name IN ('rest', 'postgresql', 'kafka') THEN 1.20
        WHEN s.normalized_name IN (
                                   'kubernetes',
                                   'docker',
                                   'aws',
                                   'gcp',
                                   'google_pubsub'
            ) THEN 1.10
        ELSE 0.50
        END
FROM profiles p
         JOIN skills s ON s.active = TRUE
WHERE p.name = 'Primary Career Profile';