package fi.vm.sade.oikeustulkkirekisteri.repository;

import fi.vm.sade.oikeustulkkirekisteri.domain.Oikeustulkki;
import org.joda.time.LocalDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;

/**
 * User: tommiratamaa
 * Date: 30.5.2016
 * Time: 14.10
 */
public interface OikeustulkkiRepository extends JpaRepository<Oikeustulkki, Long>, JpaSpecificationExecutor<Oikeustulkki> {
    @Query("select t from Oikeustulkki t where t.id = ?1 and t.poistettu=false and t.tulkki.poistettu=false")
    Oikeustulkki findEiPoistettuById(long id);

    @Query("select max(t.id) from Oikeustulkki t where t.poistettu=false and t.tulkki.poistettu=false and t.tulkki.henkiloOid in (?1)")
    Long findEiPoistettuOikeustulkkiIdByHenkiloOid(Set<String> oids);
    
    @Query("select distinct t.henkiloOid from Oikeustulkki ot inner join ot.tulkki t on t.poistettu=false where ot.poistettu=false")
    List<String> findEiPoistettuOids();

    @Query("select t from Oikeustulkki t where t.id < ?1 and t.tulkki.id = (select t2.tulkki.id from Oikeustulkki t2 where t2.id = ?1) and t.poistettu=false and t.tulkki.poistettu=false")
    List<Oikeustulkki> listAiemmatEiPoistetutById(long id);
    
    @Query("select max(t.id) from Oikeustulkki t where t.id > ?1 and t.tulkki.id = (select t2.tulkki.id from Oikeustulkki t2 where t2.id = ?1) and t.poistettu=false and t.tulkki.poistettu=false")
    Long getUusinUuudempiEiPoistettuById(long id);
    
    @Query("select t from Oikeustulkki t where t.id = ?1 and t.poistettu=false and t.tulkki.poistettu=false and t.julkaisulupa = true")
    Oikeustulkki findEiPoistettuJulkinenById(long id);
    
    @Query("select t.id from Oikeustulkki t where t in (select kp.oikeustulkki from Kielipari kp where kp.voimassaoloPaattyy >= ?1 and kp.voimassaoloPaattyy <= ?2) and t.poistettu=false and t.tulkki.poistettu=false " +
            "and not exists (select m.id from SahkopostiMuistutus m where m.oikeustulkki.id = t.id and m.lahetetty is not null) " +
            "and not exists (select t2.id from Oikeustulkki t2 where t2.id > t.id and t2.poistettu=false and t2.tulkki.id = t.tulkki.id) ")
    List<Long> findOikeustulkkisVoimassaBetweenWithoutNotificationsIds(LocalDate start, LocalDate end);
}
