CREATE TABLE sahkoposti_muistutus (
  id BIGSERIAL NOT NULL PRIMARY KEY,
  luotu TIMESTAMP DEFAULT NOW(),
  oikeustulkki BIGINT REFERENCES oikeustulkki(id) NOT NULL,
  lahetetty TIMESTAMP,
  lahettaja TEXT NOT NULL,
  vastaanottaja TEXT NOT NULL,
  template_name TEXT NOT NULL,
  sahkoposti_id BIGINT,
  kieli VARCHAR(3) NOT NULL,
  virhe TEXT
);