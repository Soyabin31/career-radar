/*
 * Seed high-signal tech companies hiring backend & distributed systems engineers.
 *
 * Sources:
 *   - Elastic (Greenhouse: elastic)
 *   - Spotify (Lever: spotify)
 *   - Cloudflare (Greenhouse: cloudflare)
 *   - Palantir (Lever: palantir)
 */

INSERT INTO companies (name, website_url, careers_url, company_type, headquarters_country)
VALUES
    ('Elastic', 'https://www.elastic.co', 'https://jobs.elastic.co/jobs', 'PRODUCT', 'USA'),
    ('Spotify', 'https://www.spotify.com', 'https://jobs.lever.co/spotify', 'PRODUCT', 'Sweden'),
    ('Cloudflare', 'https://www.cloudflare.com', 'https://boards.greenhouse.io/cloudflare', 'PRODUCT', 'USA'),
    ('Palantir', 'https://www.palantir.com', 'https://jobs.lever.co/palantir', 'PRODUCT', 'USA')
ON CONFLICT (name) DO NOTHING;

INSERT INTO job_sources (company_id, source_type, source_identifier, source_url, active)
SELECT c.id, 'GREENHOUSE', 'elastic', 'https://boards-api.greenhouse.io/v1/boards/elastic/jobs', TRUE
FROM companies c WHERE c.name = 'Elastic'
  AND NOT EXISTS (SELECT 1 FROM job_sources js WHERE js.source_identifier = 'elastic' AND js.source_type = 'GREENHOUSE');

INSERT INTO job_sources (company_id, source_type, source_identifier, source_url, active)
SELECT c.id, 'LEVER', 'spotify', 'https://api.lever.co/v0/postings/spotify?mode=json', TRUE
FROM companies c WHERE c.name = 'Spotify'
  AND NOT EXISTS (SELECT 1 FROM job_sources js WHERE js.source_identifier = 'spotify' AND js.source_type = 'LEVER');

INSERT INTO job_sources (company_id, source_type, source_identifier, source_url, active)
SELECT c.id, 'GREENHOUSE', 'cloudflare', 'https://boards-api.greenhouse.io/v1/boards/cloudflare/jobs', TRUE
FROM companies c WHERE c.name = 'Cloudflare'
  AND NOT EXISTS (SELECT 1 FROM job_sources js WHERE js.source_identifier = 'cloudflare' AND js.source_type = 'GREENHOUSE');

INSERT INTO job_sources (company_id, source_type, source_identifier, source_url, active)
SELECT c.id, 'LEVER', 'palantir', 'https://api.lever.co/v0/postings/palantir?mode=json', TRUE
FROM companies c WHERE c.name = 'Palantir'
  AND NOT EXISTS (SELECT 1 FROM job_sources js WHERE js.source_identifier = 'palantir' AND js.source_type = 'LEVER');
