
ALTER TABLE sijainti ALTER COLUMN koodi DROP NOT NULL;
ALTER TABLE sijainti DROP CONSTRAINT sijainti_tyyppi_check;
ALTER TABLE sijainti ADD CONSTRAINT sijainti_tyyppi_check CHECK (tyyppi IN ('MAAKUNTA', 'KOKO_SUOMI')
  AND ( (tyyppi = 'KOKO_SUOMI' AND koodi IS NULL)
      OR (tyyppi != 'KOKO_SUOMI' AND koodi IS NOT NULL)
   )
);
