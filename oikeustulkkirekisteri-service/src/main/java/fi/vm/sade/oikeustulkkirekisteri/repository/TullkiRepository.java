package fi.vm.sade.oikeustulkkirekisteri.repository;

import fi.vm.sade.oikeustulkkirekisteri.domain.Tulkki;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * User: tommiratamaa
 * Date: 30.5.2016
 * Time: 14.09
 */
public interface TullkiRepository extends JpaRepository<Tulkki,Long> {
}
