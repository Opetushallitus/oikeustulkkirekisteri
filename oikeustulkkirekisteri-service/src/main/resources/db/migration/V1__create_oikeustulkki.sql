
CREATE TABLE tulkki (
  id BIGSERIAL NOT NULL PRIMARY KEY,
  henkilo_oid TEXT NOT NULL,
  julklaisulupa BOOLEAN NOT NULL,
  muokattu TIMESTAMP,
  muokkaaja TEXT,
  luotu TIMESTAMP NOT NULL DEFAULT NOW(),
  luoja TEXT NOT NULL
);

CREATE TABLE oikeustulkki (
  id BIGSERIAL NOT NULL PRIMARY KEY,
  tulkki BIGINT REFERENCES tulkki(id) NOT NULL,
  alkaa DATE NOT NULL,
  paattyy DATE NOT NULL,
  lisatiedot TEXT,
  muokattu TIMESTAMP,
  muokkaaja TEXT,
  luotu TIMESTAMP NOT NULL DEFAULT NOW(),
  luoja TEXT NOT NULL
);

CREATE TABLE kielipari (
  id BIGSERIAL NOT NULL PRIMARY KEY,
  oikeustulkki BIGINT REFERENCES oikeustulkki(id) NOT NULL,
  kielesta VARCHAR(10) NOT NULL,
  kieleen  VARCHAR(10) NOT NULL,
  UNIQUE (oikeustulkki, kielesta, kieleen)
);

CREATE TABLE sijainti (
  id BIGSERIAL NOT NULL PRIMARY KEY,
  oikeustulkki BIGINT REFERENCES oikeustulkki(id) NOT NULL,
  tyyppi VARCHAR(8) NOT NULL CHECK (tyyppi IN ('KUNTA', 'MAAKUNTA')),
  koodi VARCHAR(16) NOT NULL
);
