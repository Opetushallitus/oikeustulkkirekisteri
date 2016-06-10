package fi.vm.sade.oikeustulkkirekisteri.repository;

import fi.vm.sade.oikeustulkkirekisteri.domain.Oikeustulkki;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * User: tommiratamaa
 * Date: 30.5.2016
 * Time: 14.10
 */
public interface OikeustulkkiRepository extends JpaRepository<Oikeustulkki, Long>, JpaSpecificationExecutor<Oikeustulkki> {
    @Query("select t from Oikeustulkki t where t.id = ?1 and t.poistettu=false and t.tulkki.poistettu=false")
    Oikeustulkki findEiPoistettuById(long id);

    @Query("select t from Oikeustulkki t where t.id < ?1 and t.tulkki.id = (select t2.tulkki.id from Oikeustulkki t2 where t2.id = ?1) and t.poistettu=false and t.tulkki.poistettu=false order by t.alkaa desc ")
    List<Oikeustulkki> listAiemmatEiPoistetutById(long id);
    
    @Query("select max(t.id) from Oikeustulkki t where t.id > ?1 and t.tulkki.id = (select t2.tulkki.id from Oikeustulkki t2 where t2.id = ?1) and t.poistettu=false and t.tulkki.poistettu=false")
    Long getUusinUuudempiEiPoistettuById(long id);
    
    @Query("select t from Oikeustulkki t where t.id = ?1 and t.poistettu=false and t.tulkki.poistettu=false and t.julkaisulupa = true")
    Oikeustulkki findEiPoistettuJulkinenById(long id);
}
