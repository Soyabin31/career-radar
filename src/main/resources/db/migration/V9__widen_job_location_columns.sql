/*
 * Widen location columns to accommodate multi-region locations (e.g., Databricks, Stripe)
 */
ALTER TABLE jobs ALTER COLUMN country TYPE VARCHAR(255);
ALTER TABLE jobs ALTER COLUMN state_or_region TYPE VARCHAR(255);
ALTER TABLE jobs ALTER COLUMN city TYPE VARCHAR(255);
