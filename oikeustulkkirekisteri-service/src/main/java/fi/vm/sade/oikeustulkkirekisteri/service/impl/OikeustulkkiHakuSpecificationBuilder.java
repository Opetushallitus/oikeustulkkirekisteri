package fi.vm.sade.oikeustulkkirekisteri.service.impl;

import fi.vm.sade.oikeustulkkirekisteri.domain.Kielipari;
import fi.vm.sade.oikeustulkkirekisteri.domain.Oikeustulkki;
import fi.vm.sade.oikeustulkkirekisteri.domain.Oikeustulkki.TutkintoTyyppi;
import fi.vm.sade.oikeustulkkirekisteri.domain.Sijainti;
import fi.vm.sade.oikeustulkkirekisteri.domain.Sijainti.Tyyppi;
import fi.vm.sade.oikeustulkkirekisteri.service.dto.KieliRajaus;
import org.joda.time.LocalDate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;

import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.stream.Stream.concat;
import static java.util.stream.Stream.of;
import static org.springframework.data.jpa.domain.Specifications.where;

/**
 * User: tommiratamaa
 * Date: 2.6.2016
 * Time: 13.08
 */
public class OikeustulkkiHakuSpecificationBuilder {
    public static final Specifications<Oikeustulkki> eiPoistettu = where((root, query, cb) 
            -> cb.and(
                    cb.equal(root.get("poistettu"), false),
                    cb.equal(root.join("tulkki").get("poistettu"), false)
            ));
    
    private OikeustulkkiHakuSpecificationBuilder() {}
    
    public static Specification<Oikeustulkki> voimassa(LocalDate at) {
        if (at == null) {
            return null;
        }
        return (root, query, cb) -> cb.and(
                cb.lessThanOrEqualTo(root.get("alkaa"), at),
                cb.greaterThanOrEqualTo(root.get("paattyy"), at)
            );
    }
    
    public static Specification<Oikeustulkki> kieliparit(Collection<? extends KieliRajaus> kieliRajaus) {
        if (kieliRajaus == null || kieliRajaus.isEmpty()  || (kieliRajaus.size() == 1 && kieliRajaus.iterator().next() == null)) {
            return null;
        }
        return kieliRajaus.stream().filter(kr -> kr.getKielesta() != null && kr.getKieleen() != null)
            .map(kr -> where((Specification<Oikeustulkki>) (root, query, cb) -> {
                Subquery<Long> s = query.subquery(Long.class);
                Root<Oikeustulkki> t = s.from(Oikeustulkki.class);
                Path<Kielipari> kp = t.join("kielet");
                return cb.exists(s.select(t.get("id")).where(
                        cb.and(
                            cb.equal(t.get("id"), root.get("id")),
                            cb.or(
                                cb.and(
                                        cb.equal(kp.get("kielesta").get("koodi"), kr.getKielesta()),
                                        cb.equal(kp.get("kieleen").get("koodi"), kr.getKieleen())
                                ),
                                cb.and(
                                        cb.equal(kp.get("kieleen").get("koodi"), kr.getKielesta()),
                                        cb.equal(kp.get("kielesta").get("koodi"), kr.getKieleen())
                                )
                            )
                        )
                    )
                );
            })).reduce(where(null), Specifications::and);
    }
    
    public static Specification<Oikeustulkki> toimiiMaakunnissa(Collection<String> maakuntaKoodis) {
        if (maakuntaKoodis == null || maakuntaKoodis.isEmpty() || (maakuntaKoodis.size() == 1 && maakuntaKoodis.iterator().next() == null)) {
            return null;
        }
        return maakuntaKoodis.stream().map(maakuntaKoodi -> where((Specification<Oikeustulkki>) (root, query, cb) -> {
                Subquery<Long> s = query.subquery(Long.class);
                Root<Oikeustulkki> t = s.from(Oikeustulkki.class);
                Path<Sijainti> sijainti = t.join("sijainnit");
                return cb.exists(s.select(t.get("id")).where(
                        cb.and(
                            cb.equal(t.get("id"), root.get("id")),
                            cb.or(
                                cb.equal(sijainti.get("tyyppi"), Tyyppi.KOKO_SUOMI),
                                cb.and(
                                        cb.equal(sijainti.get("tyyppi"), Tyyppi.MAAKUNTA),
                                        cb.equal(sijainti.get("koodi"), maakuntaKoodi)
                                )
                            )
                        )
                    )
                );
            })).reduce(where(null), Specifications::and);
    }
    
    public static Specification<Oikeustulkki> latest(Specification<Oikeustulkki> specification) {
        return (root, query, cb) -> {
            Subquery<LocalDate> s = query.subquery(LocalDate.class);
            Root<Oikeustulkki> t = s.from(Oikeustulkki.class);
            return cb.equal(root.get("alkaa"), s.select(cb.greatest((Path)t.get("alkaa")))
                    .where(cb.and(
                            cb.equal(t.join("tulkki").get("id"), root.join("tulkki").get("id")),
                            specification.toPredicate(t, query, cb)
            )));
        };
    }

    public static Specification<Oikeustulkki> voimassaoloRajausAlku(LocalDate alku) {
        if (alku == null) {
            return null;
        }
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("alkaa"), alku);
    }

    public static Specification<Oikeustulkki> voimassaoloRajausLoppu(LocalDate loppu) {
        if (loppu == null) {
            return null;
        }
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("alkaa"), loppu);
    }
    
    public static Specification<Oikeustulkki> tutkintoTyyppi(TutkintoTyyppi tyyppi) {
        if (tyyppi == null) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get("tutkintoTyyppi"), tyyppi);
    }

    public static Specification<Oikeustulkki> julkaisulupa() {
        return (root, query, cb) -> cb.equal(root.get("julkaisulupa"), true);
    }

    public static Specification<Oikeustulkki> henkiloOidIn(Set<String> in) {
        if (in == null || in.isEmpty()) {
            return null;
        }
        return (root, query, cb) -> root.join("tulkki").get("henkiloOid").in(in);
    }
}
