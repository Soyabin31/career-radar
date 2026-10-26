/*
 * Seed high-volume tech companies & global remote opportunities.
 *
 * Sources:
 *   - Toptal (Lever: toptal) - Global Remote / USD contractor pay
 *   - Snowflake (Ashby: snowflake) - Pune & India Remote
 *   - Twilio (Greenhouse: twilio) - Bangalore engineering hub
 *   - GitLab (Greenhouse: gitlab) - 100% Global Remote
 *   - Databricks (Greenhouse: databricks) - Bangalore engineering hub
 *   - Thoughtworks (Greenhouse: thoughtworks) - Bangalore / Pune / Hyderabad Java powerhouse
 *   - Stripe (Greenhouse: stripe) - Bangalore engineering hub
 *   - Airbnb (Greenhouse: airbnb) - Tech & distributed engineering
 *   - Notion (Ashby: notion) - Productivity & platform engineering
 *   - Ramp (Ashby: ramp) - Fintech & backend engineering
 */

INSERT INTO companies (name, website_url, careers_url, company_type, headquarters_country)
VALUES
    ('Toptal', 'https://www.toptal.com', 'https://jobs.lever.co/toptal', 'PRODUCT', 'USA'),
    ('Snowflake', 'https://www.snowflake.com', 'https://jobs.ashbyhq.com/snowflake', 'PRODUCT', 'USA'),
    ('Twilio', 'https://www.twilio.com', 'https://boards.greenhouse.io/twilio', 'PRODUCT', 'USA'),
    ('GitLab', 'https://about.gitlab.com', 'https://boards.greenhouse.io/gitlab', 'PRODUCT', 'USA'),
    ('Databricks', 'https://www.databricks.com', 'https://boards.greenhouse.io/databricks', 'PRODUCT', 'USA'),
    ('Thoughtworks', 'https://www.thoughtworks.com', 'https://boards.greenhouse.io/thoughtworks', 'SERVICE', 'USA'),
    ('Stripe', 'https://stripe.com', 'https://boards.greenhouse.io/stripe', 'PRODUCT', 'USA'),
    ('Airbnb', 'https://www.airbnb.com', 'https://boards.greenhouse.io/airbnb', 'PRODUCT', 'USA'),
    ('Notion', 'https://www.notion.so', 'https://jobs.ashbyhq.com/notion', 'PRODUCT', 'USA'),
    ('Ramp', 'https://ramp.com', 'https://jobs.ashbyhq.com/ramp', 'PRODUCT', 'USA')
ON CONFLICT (name) DO NOTHING;

INSERT INTO job_sources (company_id, source_type, source_identifier, source_url, active)
SELECT c.id, 'LEVER', 'toptal', 'https://api.lever.co/v0/postings/toptal?mode=json', TRUE
FROM companies c WHERE c.name = 'Toptal'
  AND NOT EXISTS (SELECT 1 FROM job_sources js WHERE js.source_identifier = 'toptal' AND js.source_type = 'LEVER');

INSERT INTO job_sources (company_id, source_type, source_identifier, source_url, active)
SELECT c.id, 'ASHBY', 'snowflake', 'https://api.ashbyhq.com/posting-api/job-board/snowflake', TRUE
FROM companies c WHERE c.name = 'Snowflake'
  AND NOT EXISTS (SELECT 1 FROM job_sources js WHERE js.source_identifier = 'snowflake' AND js.source_type = 'ASHBY');

INSERT INTO job_sources (company_id, source_type, source_identifier, source_url, active)
SELECT c.id, 'GREENHOUSE', 'twilio', 'https://boards-api.greenhouse.io/v1/boards/twilio/jobs', TRUE
FROM companies c WHERE c.name = 'Twilio'
  AND NOT EXISTS (SELECT 1 FROM job_sources js WHERE js.source_identifier = 'twilio' AND js.source_type = 'GREENHOUSE');

INSERT INTO job_sources (company_id, source_type, source_identifier, source_url, active)
SELECT c.id, 'GREENHOUSE', 'gitlab', 'https://boards-api.greenhouse.io/v1/boards/gitlab/jobs', TRUE
FROM companies c WHERE c.name = 'GitLab'
  AND NOT EXISTS (SELECT 1 FROM job_sources js WHERE js.source_identifier = 'gitlab' AND js.source_type = 'GREENHOUSE');

INSERT INTO job_sources (company_id, source_type, source_identifier, source_url, active)
SELECT c.id, 'GREENHOUSE', 'databricks', 'https://boards-api.greenhouse.io/v1/boards/databricks/jobs', TRUE
FROM companies c WHERE c.name = 'Databricks'
  AND NOT EXISTS (SELECT 1 FROM job_sources js WHERE js.source_identifier = 'databricks' AND js.source_type = 'GREENHOUSE');

INSERT INTO job_sources (company_id, source_type, source_identifier, source_url, active)
SELECT c.id, 'GREENHOUSE', 'thoughtworks', 'https://boards-api.greenhouse.io/v1/boards/thoughtworks/jobs', TRUE
FROM companies c WHERE c.name = 'Thoughtworks'
  AND NOT EXISTS (SELECT 1 FROM job_sources js WHERE js.source_identifier = 'thoughtworks' AND js.source_type = 'GREENHOUSE');

INSERT INTO job_sources (company_id, source_type, source_identifier, source_url, active)
SELECT c.id, 'GREENHOUSE', 'stripe', 'https://boards-api.greenhouse.io/v1/boards/stripe/jobs', TRUE
FROM companies c WHERE c.name = 'Stripe'
  AND NOT EXISTS (SELECT 1 FROM job_sources js WHERE js.source_identifier = 'stripe' AND js.source_type = 'GREENHOUSE');

INSERT INTO job_sources (company_id, source_type, source_identifier, source_url, active)
SELECT c.id, 'GREENHOUSE', 'airbnb', 'https://boards-api.greenhouse.io/v1/boards/airbnb/jobs', TRUE
FROM companies c WHERE c.name = 'Airbnb'
  AND NOT EXISTS (SELECT 1 FROM job_sources js WHERE js.source_identifier = 'airbnb' AND js.source_type = 'GREENHOUSE');

INSERT INTO job_sources (company_id, source_type, source_identifier, source_url, active)
SELECT c.id, 'ASHBY', 'notion', 'https://api.ashbyhq.com/posting-api/job-board/notion', TRUE
FROM companies c WHERE c.name = 'Notion'
  AND NOT EXISTS (SELECT 1 FROM job_sources js WHERE js.source_identifier = 'notion' AND js.source_type = 'ASHBY');

INSERT INTO job_sources (company_id, source_type, source_identifier, source_url, active)
SELECT c.id, 'ASHBY', 'ramp', 'https://api.ashbyhq.com/posting-api/job-board/ramp', TRUE
FROM companies c WHERE c.name = 'Ramp'
  AND NOT EXISTS (SELECT 1 FROM job_sources js WHERE js.source_identifier = 'ramp' AND js.source_type = 'ASHBY');
