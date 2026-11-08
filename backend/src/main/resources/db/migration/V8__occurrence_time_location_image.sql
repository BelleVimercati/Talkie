ALTER TABLE occurrences ADD COLUMN occurred_at TIMESTAMP;
ALTER TABLE occurrences ADD COLUMN latitude DOUBLE PRECISION;
ALTER TABLE occurrences ADD COLUMN longitude DOUBLE PRECISION;
ALTER TABLE occurrences ADD COLUMN image_path VARCHAR(255);

UPDATE occurrences SET occurred_at = created_at WHERE occurred_at IS NULL;
