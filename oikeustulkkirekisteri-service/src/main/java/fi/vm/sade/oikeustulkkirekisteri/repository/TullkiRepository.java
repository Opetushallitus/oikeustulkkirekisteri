package fi.vm.sade.oikeustulkkirekisteri.repository;

import fi.vm.sade.oikeustulkkirekisteri.domain.Tulkki;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * User: tommiratamaa
 * Date: 30.5.2016
 * Time: 14.09
 */
public interface TullkiRepository extends JpaRepository<Tulkki,Long> {
    @Query("select t from Tulkki t where t.henkiloOid = ?1 and t.poistettu=false")
    Tulkki findByHenkiloOid(String henkiloOid);
}
