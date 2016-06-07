CREATE TABLE oikeustulkki_muokkaus (
  id BIGSERIAL NOT NULL PRIMARY KEY,
  oikeustulkki BIGINT REFERENCES oikeustulkki(id) NOT NULL,
  muokattu TIMESTAMP NOT NULL DEFAULT NOW(),
  muokkaaja TEXT NOT NULL
);