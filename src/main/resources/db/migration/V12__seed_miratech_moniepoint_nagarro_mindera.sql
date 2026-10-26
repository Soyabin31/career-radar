/*
 * V12: Seed user-requested companies from live ATS job boards:
 *   - Miratech (SmartRecruiters: Miratech1) - Senior Java Developer & distributed systems
 *   - Moniepoint (Greenhouse: moniepoint) - Java, Spring Boot, FinTech
 *   - Nagarro (SmartRecruiters: Nagarro1) - Java fullstack, staff engineer & backend
 *   - Mindera (Workable: minderacraft) - Java, cloud systems, remote software engineering
 */

INSERT INTO companies (name, website_url, careers_url, company_type, headquarters_country)
VALUES
    ('Miratech', 'https://miratechonline.com', 'https://jobs.smartrecruiters.com/Miratech1', 'SERVICE', 'USA'),
    ('Moniepoint', 'https://moniepoint.com', 'https://job-boards.greenhouse.io/moniepoint', 'PRODUCT', 'UK'),
    ('Nagarro', 'https://www.nagarro.com', 'https://jobs.smartrecruiters.com/Nagarro1', 'SERVICE', 'Germany'),
    ('Mindera', 'https://mindera.com', 'https://apply.workable.com/minderacraft', 'SERVICE', 'UK')
ON CONFLICT (name) DO NOTHING;

INSERT INTO job_sources (company_id, source_type, source_identifier, source_url, active)
SELECT c.id, 'SMARTRECRUITERS', 'Miratech1', 'https://api.smartrecruiters.com/v1/companies/Miratech1/postings', TRUE
FROM companies c WHERE c.name = 'Miratech'
  AND NOT EXISTS (SELECT 1 FROM job_sources js WHERE js.source_identifier = 'Miratech1' AND js.source_type = 'SMARTRECRUITERS');

INSERT INTO job_sources (company_id, source_type, source_identifier, source_url, active)
SELECT c.id, 'GREENHOUSE', 'moniepoint', 'https://boards-api.greenhouse.io/v1/boards/moniepoint/jobs', TRUE
FROM companies c WHERE c.name = 'Moniepoint'
  AND NOT EXISTS (SELECT 1 FROM job_sources js WHERE js.source_identifier = 'moniepoint' AND js.source_type = 'GREENHOUSE');

INSERT INTO job_sources (company_id, source_type, source_identifier, source_url, active)
SELECT c.id, 'SMARTRECRUITERS', 'Nagarro1', 'https://api.smartrecruiters.com/v1/companies/Nagarro1/postings', TRUE
FROM companies c WHERE c.name = 'Nagarro'
  AND NOT EXISTS (SELECT 1 FROM job_sources js WHERE js.source_identifier = 'Nagarro1' AND js.source_type = 'SMARTRECRUITERS');

INSERT INTO job_sources (company_id, source_type, source_identifier, source_url, active)
SELECT c.id, 'WORKABLE', 'minderacraft', 'https://apply.workable.com/api/v1/widget/accounts/minderacraft', TRUE
FROM companies c WHERE c.name = 'Mindera'
  AND NOT EXISTS (SELECT 1 FROM job_sources js WHERE js.source_identifier = 'minderacraft' AND js.source_type = 'WORKABLE');
