CREATE TABLE jobs (
                      id BIGSERIAL PRIMARY KEY,

                      company_id BIGINT REFERENCES companies(id),
                      source_id BIGINT NOT NULL REFERENCES job_sources(id),

                      source_job_id VARCHAR(255),

                      title VARCHAR(500) NOT NULL,

                      description TEXT,

                      canonical_url TEXT NOT NULL,

                      geography_type VARCHAR(30),

                      foreign_region VARCHAR(30),

                      workplace_type VARCHAR(30),

                      employment_type VARCHAR(30),

                      country VARCHAR(100),

                      state_or_region VARCHAR(100),

                      city VARCHAR(150),

                      salary_min NUMERIC(15, 2),

                      salary_max NUMERIC(15, 2),

                      salary_currency VARCHAR(10),

                      posted_at TIMESTAMPTZ,

                      discovered_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

                      last_seen_at TIMESTAMPTZ,

                      content_hash VARCHAR(128),

                      active BOOLEAN NOT NULL DEFAULT TRUE,

                      created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

                      updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_jobs_company_id
    ON jobs(company_id);


CREATE INDEX idx_jobs_source_id
    ON jobs(source_id);


CREATE INDEX idx_jobs_geography_type
    ON jobs(geography_type);


CREATE INDEX idx_jobs_workplace_type
    ON jobs(workplace_type);


CREATE INDEX idx_jobs_employment_type
    ON jobs(employment_type);


CREATE INDEX idx_jobs_posted_at
    ON jobs(posted_at);


CREATE INDEX idx_jobs_active
    ON jobs(active);

CREATE UNIQUE INDEX uk_jobs_source_job_id
    ON jobs(source_id, source_job_id)
    WHERE source_job_id IS NOT NULL;