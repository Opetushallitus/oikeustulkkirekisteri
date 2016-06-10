ALTER TABLE oikeustulkki ADD COLUMN julkaisulupa BOOLEAN DEFAULT FALSE;
UPDATE oikeustulkki SET julkaisulupa = FALSE;
ALTER TABLE oikeustulkki ALTER COLUMN julkaisulupa SET NOT NULL;