CREATE TABLE profile_roles (
                               id BIGSERIAL PRIMARY KEY,

                               profile_id BIGINT NOT NULL REFERENCES profiles(id) ON DELETE CASCADE,

                               role_name VARCHAR(200) NOT NULL,

                               role_family VARCHAR(50) NOT NULL,

                               priority INTEGER NOT NULL DEFAULT 1,

                               active BOOLEAN NOT NULL DEFAULT TRUE,

                               created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

                               CONSTRAINT uk_profile_roles
                                   UNIQUE (profile_id, role_name)
);


CREATE INDEX idx_profile_roles_profile_id
    ON profile_roles(profile_id);


CREATE INDEX idx_profile_roles_family
    ON profile_roles(role_family);

CREATE TABLE skills (
                        id BIGSERIAL PRIMARY KEY,

                        name VARCHAR(150) NOT NULL,

                        normalized_name VARCHAR(150) NOT NULL,

                        category VARCHAR(100),

                        active BOOLEAN NOT NULL DEFAULT TRUE,

                        created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

                        CONSTRAINT uk_skills_normalized_name
                            UNIQUE (normalized_name)
);


CREATE TABLE profile_skills (
                                id BIGSERIAL PRIMARY KEY,

                                profile_id BIGINT NOT NULL REFERENCES profiles(id) ON DELETE CASCADE,

                                skill_id BIGINT NOT NULL REFERENCES skills(id) ON DELETE CASCADE,

                                skill_type VARCHAR(30) NOT NULL,

                                weight NUMERIC(6, 3) NOT NULL DEFAULT 1.000,

                                active BOOLEAN NOT NULL DEFAULT TRUE,

                                created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                CONSTRAINT uk_profile_skills
                                    UNIQUE (profile_id, skill_id)
);


CREATE INDEX idx_profile_skills_profile_id
    ON profile_skills(profile_id);


CREATE INDEX idx_profile_skills_skill_id
    ON profile_skills(skill_id);

CREATE TABLE profile_locations (
                                   id BIGSERIAL PRIMARY KEY,

                                   profile_id BIGINT NOT NULL REFERENCES profiles(id) ON DELETE CASCADE,

                                   geography_type VARCHAR(30) NOT NULL,

                                   foreign_region VARCHAR(30),

                                   country VARCHAR(100),

                                   state_or_region VARCHAR(100),

                                   city VARCHAR(150),

                                   priority INTEGER NOT NULL DEFAULT 1,

                                   active BOOLEAN NOT NULL DEFAULT TRUE,

                                   created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);


CREATE INDEX idx_profile_locations_profile_id
    ON profile_locations(profile_id);


CREATE INDEX idx_profile_locations_geography
    ON profile_locations(geography_type);

CREATE TABLE profile_workplace_preferences (
                                               id BIGSERIAL PRIMARY KEY,

                                               profile_id BIGINT NOT NULL REFERENCES profiles(id) ON DELETE CASCADE,

                                               workplace_type VARCHAR(30) NOT NULL,

                                               priority INTEGER NOT NULL DEFAULT 1,

                                               active BOOLEAN NOT NULL DEFAULT TRUE,

                                               CONSTRAINT uk_profile_workplace
                                                   UNIQUE (profile_id, workplace_type)
);

CREATE TABLE profile_employment_preferences (
                                                id BIGSERIAL PRIMARY KEY,

                                                profile_id BIGINT NOT NULL REFERENCES profiles(id) ON DELETE CASCADE,

                                                employment_type VARCHAR(30) NOT NULL,

                                                priority INTEGER NOT NULL DEFAULT 1,

                                                active BOOLEAN NOT NULL DEFAULT TRUE,

                                                CONSTRAINT uk_profile_employment
                                                    UNIQUE (profile_id, employment_type)
);

CREATE TABLE profile_company_preferences (
                                             id BIGSERIAL PRIMARY KEY,

                                             profile_id BIGINT NOT NULL REFERENCES profiles(id) ON DELETE CASCADE,

                                             company_type VARCHAR(50) NOT NULL,

                                             priority INTEGER NOT NULL DEFAULT 1,

                                             active BOOLEAN NOT NULL DEFAULT TRUE,

                                             CONSTRAINT uk_profile_company_type
                                                 UNIQUE (profile_id, company_type)
);

CREATE TABLE job_skills (
                            id BIGSERIAL PRIMARY KEY,

                            job_id BIGINT NOT NULL REFERENCES jobs(id) ON DELETE CASCADE,

                            skill_id BIGINT NOT NULL REFERENCES skills(id) ON DELETE CASCADE,

                            required BOOLEAN NOT NULL DEFAULT TRUE,

                            confidence NUMERIC(5, 4) NOT NULL DEFAULT 1.0000,

                            source VARCHAR(50),

                            created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

                            CONSTRAINT uk_job_skills
                                UNIQUE (job_id, skill_id)
);


CREATE INDEX idx_job_skills_job_id
    ON job_skills(job_id);


CREATE INDEX idx_job_skills_skill_id
    ON job_skills(skill_id);

CREATE TABLE job_role_classifications (
                                          id BIGSERIAL PRIMARY KEY,

                                          job_id BIGINT NOT NULL REFERENCES jobs(id) ON DELETE CASCADE,

                                          role_family VARCHAR(50) NOT NULL,

                                          confidence NUMERIC(5, 4) NOT NULL,

                                          source VARCHAR(50) NOT NULL,

                                          created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                          CONSTRAINT uk_job_role_classification
                                              UNIQUE (job_id, role_family)
);


CREATE INDEX idx_job_role_classifications_job_id
    ON job_role_classifications(job_id);


CREATE INDEX idx_job_role_classifications_family
    ON job_role_classifications(role_family);

