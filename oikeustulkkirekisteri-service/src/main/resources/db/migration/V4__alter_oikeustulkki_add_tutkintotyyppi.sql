ALTER TABLE oikeustulkki ADD COLUMN tutkinto_tyyppi VARCHAR(64) NOT NULL DEFAULT NULL;
ALTER TABLE oikeustulkki ADD tutkinto_tyyppi_check CHECK (tutkinto_tyyppi IN (
      'OIKEUSTULKIN_ERIKOISAMMATTITUTKINTO',
      'MUU_KORKEAKOULUTUTKINTO'
  ));
