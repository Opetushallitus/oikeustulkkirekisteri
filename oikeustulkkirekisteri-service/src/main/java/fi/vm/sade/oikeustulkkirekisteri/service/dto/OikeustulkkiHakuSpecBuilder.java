package fi.vm.sade.oikeustulkkirekisteri.service.dto;

import fi.vm.sade.oikeustulkkirekisteri.domain.Oikeustulkki;
import org.joda.time.LocalDate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;

import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.springframework.data.jpa.domain.Specifications.where;

/**
 * User: tommiratamaa
 * Date: 2.6.2016
 * Time: 13.08
 */
public class OikeustulkkiHakuSpecBuilder {
    public static final Specifications<Oikeustulkki> empty = where(null);
    
    public static Specification<Oikeustulkki> voimassa(LocalDate at) {
        if (at == null) {
            return null;
        }
        return (root, query, cb) 
                -> cb.and(
                cb.lessThanOrEqualTo(root.get("alkaa"), at),
                cb.greaterThanOrEqualTo(root.get("paattyy"), at)
            );
    }
    
    public static Specification<Oikeustulkki> kielipari(KieliRajaus kieliRajaus) {
        if (kieliRajaus == null || (kieliRajaus.getKielesta() == null && kieliRajaus.getKieleen() == null)) {
            return null;
        }
        return (root, query, cb) -> {
            Subquery<Oikeustulkki> s = query.subquery(Oikeustulkki.class);
            Root<Oikeustulkki> t = s.correlate(root);
            List<Predicate> predicateList = new ArrayList<>();
            if (kieliRajaus.getKielesta() != null) {
                predicateList.add(cb.equal(t.join("kielet").get("kielesta").get("koodi"), kieliRajaus.getKielesta()));
            }
            if (kieliRajaus.getKieleen() != null) {
                predicateList.add(cb.equal(t.join("kielet").get("kieleen").get("koodi"), kieliRajaus.getKieleen()));
            }
            return cb.exists(s.select(t.get("id")).where(predicateList.toArray(new Predicate[predicateList.size()])));
        };
    }
    
    public static Specification<Oikeustulkki> voimassaoloAlku(LocalDate alku) {
        if (alku == null) {
            return null;
        }
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("alkaa"), alku);
    }

    public static Specification<Oikeustulkki> voimassaoloLoppu(LocalDate loppu) {
        if (loppu == null) {
            return null;
        }
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("alkaa"), loppu);
    }

    public static Specification<Oikeustulkki> henkiloOidIn(Set<String> in) {
        if (in == null || in.isEmpty()) {
            return null;
        }
        return (root, query, cb) -> root.join("tulkki").get("henkiloOid").in(in);
    }

    private OikeustulkkiHakuSpecBuilder() {}
}
