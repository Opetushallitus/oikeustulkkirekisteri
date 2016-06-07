ALTER TABLE tulkki DROP COLUMN julklaisulupa;
ALTER TABLE oikeustulkki ADD COLUMN julklaisulupa_email BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE oikeustulkki ADD COLUMN julklaisulupa_puhelinnumero BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE oikeustulkki ADD COLUMN julklaisulupa_muu_yhteystieto BOOLEAN NOT NULL DEFAULT FALSE;
