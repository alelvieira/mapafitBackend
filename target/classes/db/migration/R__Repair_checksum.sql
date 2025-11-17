-- This script repairs the Flyway schema history
-- It updates the checksum of migration version 3 to match the current file
UPDATE flyway_schema_history 
SET checksum = 1842703293 
WHERE version = '3' AND checksum = 797924755;
