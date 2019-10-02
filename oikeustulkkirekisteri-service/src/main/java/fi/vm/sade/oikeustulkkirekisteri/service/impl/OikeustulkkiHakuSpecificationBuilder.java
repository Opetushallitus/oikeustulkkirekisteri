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

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

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

    public static Specification<Oikeustulkki> jokuKielipariVoimassa(LocalDate at) {
        if (at == null) {
            return null;
        }
        return (root, query, cb) -> {
            Subquery<Integer> subquery = query.subquery(Integer.class);
            Root<Kielipari> kielipari = subquery.from(Kielipari.class);
            subquery.select(cb.literal(1));
            subquery.where(cb.and(
                    cb.equal(kielipari.get("oikeustulkki"), root),
                    cb.lessThanOrEqualTo(kielipari.get("voimassaoloAlkaa"), at),
                    cb.greaterThanOrEqualTo(kielipari.get("voimassaoloPaattyy"), at)));
            return cb.exists(subquery);
        };
    }

    public static Specification<Oikeustulkki> kieliparit(Collection<? extends KieliRajaus> kieliRajaus) {
        return kieliparit(kieliRajaus, null);
    }

    public static Specification<Oikeustulkki> kieliparit(Collection<? extends KieliRajaus> kieliRajaus, LocalDate at) {
        if (kieliRajaus == null || kieliRajaus.isEmpty()  || (kieliRajaus.size() == 1 && kieliRajaus.iterator().next() == null)) {
            return null;
        }
        return kieliRajaus.stream().filter(kr -> kr.getKielesta() != null || kr.getKieleen() != null)
            .map(kr -> where((Specification<Oikeustulkki>) (root, query, cb) -> {
                Subquery<Long> s = query.subquery(Long.class);
                Root<Oikeustulkki> t = s.from(Oikeustulkki.class);
                Join<Oikeustulkki, Kielipari> k = t.join("kielet");
                List<Predicate> predicates = new ArrayList<>();
                predicates.add(cb.equal(t.get("id"), root.get("id")));
                predicates.add(kielipariEq(k, kr, cb));
                if (at != null) {
                    predicates.add(cb.lessThanOrEqualTo(k.get("voimassaoloAlkaa"), at));
                    predicates.add(cb.greaterThanOrEqualTo(k.get("voimassaoloPaattyy"), at));
                }
                return cb.exists(s.select(t.get("id")).where(
                        cb.and(predicates.toArray(new Predicate[predicates.size()]))
                    )
                );
            })).reduce(where(null), Specifications::and);
    }

    private static Predicate kielipariEq(Path<Kielipari> kp, KieliRajaus kr, CriteriaBuilder cb) {
        if (kr.getKielesta() == null && kr.getKieleen() == null) {
            return null;
        }
        if (kr.getKieleen() == null) {
            return cb.or(
                    cb.equal(kp.get("kielesta").get("koodi"), kr.getKielesta()),
                    cb.equal(kp.get("kieleen").get("koodi"), kr.getKielesta())
            );
        } else if (kr.getKielesta() == null) {
            return cb.or(
                    cb.equal(kp.get("kieleen").get("koodi"), kr.getKieleen()),
                    cb.equal(kp.get("kielesta").get("koodi"), kr.getKieleen())
            );
        }
        return cb.or(
                        cb.and(
                                cb.equal(kp.get("kielesta").get("koodi"), kr.getKielesta()),
                                cb.equal(kp.get("kieleen").get("koodi"), kr.getKieleen())
                        ),
                        cb.and(
                                cb.equal(kp.get("kieleen").get("koodi"), kr.getKielesta()),
                                cb.equal(kp.get("kielesta").get("koodi"), kr.getKieleen())
                        )
                );
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
