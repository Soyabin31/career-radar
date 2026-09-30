CREATE TABLE profiles (
                          id BIGSERIAL PRIMARY KEY,
                          name VARCHAR(100) NOT NULL,
                          active BOOLEAN NOT NULL DEFAULT TRUE,
                          created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);


CREATE TABLE companies (
                           id BIGSERIAL PRIMARY KEY,
                           name VARCHAR(200) NOT NULL,
                           website_url TEXT,
                           careers_url TEXT,
                           company_type VARCHAR(50) NOT NULL DEFAULT 'PRODUCT',
                           headquarters_country VARCHAR(100),
                           active BOOLEAN NOT NULL DEFAULT TRUE,
                           created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

                           CONSTRAINT uk_companies_name UNIQUE (name)
);


CREATE TABLE job_sources (
                             id BIGSERIAL PRIMARY KEY,
                             company_id BIGINT REFERENCES companies(id),
                             source_type VARCHAR(50) NOT NULL,
                             source_identifier VARCHAR(255),
                             source_url TEXT NOT NULL,
                             active BOOLEAN NOT NULL DEFAULT TRUE,
                             created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
                             updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);


CREATE INDEX idx_job_sources_company_id
    ON job_sources(company_id);


CREATE INDEX idx_job_sources_type
    ON job_sources(source_type);