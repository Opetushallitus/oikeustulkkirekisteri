package fi.vm.sade.oikeustulkkirekisteri.repository;

import fi.vm.sade.oikeustulkkirekisteri.domain.Oikeustulkki;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * User: tommiratamaa
 * Date: 30.5.2016
 * Time: 14.10
 */
public interface OikeustulkkiRepository extends JpaRepository<Oikeustulkki, Long>, JpaSpecificationExecutor<Oikeustulkki> {
}
