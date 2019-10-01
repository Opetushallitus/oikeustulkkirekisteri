UPDATE kielipari
SET voimassaolo_alkaa = ot.alkaa, voimassaolo_paattyy = ot.paattyy
FROM oikeustulkki ot
WHERE ot.id = kielipari.oikeustulkki;

ALTER TABLE kielipari ALTER COLUMN voimassaolo_alkaa SET NOT NULL;
ALTER TABLE kielipari ALTER COLUMN voimassaolo_paattyy SET NOT NULL;

ALTER TABLE oikeustulkki DROP COLUMN alkaa;
ALTER TABLE oikeustulkki DROP COLUMN paattyy;
