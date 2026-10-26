/*
 * Seed user-requested companies:
 *   - Quince (Greenhouse: quince)
 *   - Yugabyte (Greenhouse: yugabyte) - Distributed SQL & backend
 *   - OneTrust (Greenhouse: onetrust) - Java Backend & distributed systems
 *   - Kong (Ashby: kong) - API Gateway, microservices & backend
 */

INSERT INTO companies (name, website_url, careers_url, company_type, headquarters_country)
VALUES
    ('Quince', 'https://www.quince.com', 'https://boards.greenhouse.io/quince', 'PRODUCT', 'USA'),
    ('Yugabyte', 'https://www.yugabyte.com', 'https://boards.greenhouse.io/yugabyte', 'PRODUCT', 'USA'),
    ('OneTrust', 'https://www.onetrust.com', 'https://boards.greenhouse.io/onetrust', 'PRODUCT', 'USA'),
    ('Kong', 'https://konghq.com', 'https://jobs.ashbyhq.com/kong', 'PRODUCT', 'USA')
ON CONFLICT (name) DO NOTHING;

INSERT INTO job_sources (company_id, source_type, source_identifier, source_url, active)
SELECT c.id, 'GREENHOUSE', 'quince', 'https://boards-api.greenhouse.io/v1/boards/quince/jobs', TRUE
FROM companies c WHERE c.name = 'Quince'
  AND NOT EXISTS (SELECT 1 FROM job_sources js WHERE js.source_identifier = 'quince' AND js.source_type = 'GREENHOUSE');

INSERT INTO job_sources (company_id, source_type, source_identifier, source_url, active)
SELECT c.id, 'GREENHOUSE', 'yugabyte', 'https://boards-api.greenhouse.io/v1/boards/yugabyte/jobs', TRUE
FROM companies c WHERE c.name = 'Yugabyte'
  AND NOT EXISTS (SELECT 1 FROM job_sources js WHERE js.source_identifier = 'yugabyte' AND js.source_type = 'GREENHOUSE');

INSERT INTO job_sources (company_id, source_type, source_identifier, source_url, active)
SELECT c.id, 'GREENHOUSE', 'onetrust', 'https://boards-api.greenhouse.io/v1/boards/onetrust/jobs', TRUE
FROM companies c WHERE c.name = 'OneTrust'
  AND NOT EXISTS (SELECT 1 FROM job_sources js WHERE js.source_identifier = 'onetrust' AND js.source_type = 'GREENHOUSE');

INSERT INTO job_sources (company_id, source_type, source_identifier, source_url, active)
SELECT c.id, 'ASHBY', 'kong', 'https://api.ashbyhq.com/posting-api/job-board/kong', TRUE
FROM companies c WHERE c.name = 'Kong'
  AND NOT EXISTS (SELECT 1 FROM job_sources js WHERE js.source_identifier = 'kong' AND js.source_type = 'ASHBY');
