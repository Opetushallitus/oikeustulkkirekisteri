package fi.vm.sade.oikeustulkkirekisteri.service;

import fi.vm.sade.oikeustulkkirekisteri.external.api.dto.HenkiloRestDto;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * For internal use only.
 * 
 * User: tommiratamaa
 * Date: 20.6.2016
 * Time: 17.42
 */
public interface OikeustulkkiCacheService {
    void scheduledFetch();

    void notifyHenkiloUpdated(String oid);
    
    List<HenkiloRestDto> findHenkilos(Predicate<HenkiloRestDto> predicate);

    Optional<HenkiloRestDto> findHenkiloByOid(String oid);
}
