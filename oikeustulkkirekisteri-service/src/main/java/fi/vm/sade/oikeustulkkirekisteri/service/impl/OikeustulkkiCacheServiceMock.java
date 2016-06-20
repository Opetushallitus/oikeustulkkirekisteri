package fi.vm.sade.oikeustulkkirekisteri.service.impl;

import fi.vm.sade.oikeustulkkirekisteri.external.api.dto.HenkiloRestDto;
import fi.vm.sade.oikeustulkkirekisteri.service.OikeustulkkiCacheService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

import static fi.vm.sade.oikeustulkkirekisteri.service.impl.OikeustulkkiCacheServiceImpl.BY_NAME;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;

/**
 * User: tommiratamaa
 * Date: 20.6.2016
 * Time: 18.45
 */
@Service @Profile({"test"})
@SuppressWarnings("TransactionalAnnotations")
public class OikeustulkkiCacheServiceMock implements OikeustulkkiCacheService {
    private Map<String,HenkiloRestDto> byOid = new HashMap<>();
    private Map<String,HenkiloRestDto> toUpdate = new HashMap<>();
    
    @Override
    public void notifyHenkiloUpdated(String oid) {
        HenkiloRestDto dto = toUpdate.get(oid);
        if (dto != null) {
            byOid.put(oid, dto);
        }
    }
    
    public void onNotifyUpdate(HenkiloRestDto dto) {
        toUpdate.put(dto.getOidHenkilo(), dto);
    }
    
    public void put(HenkiloRestDto dto) {
        toUpdate.put(dto.getOidHenkilo(), dto);
    }

    @Override
    public List<HenkiloRestDto> findHenkilos(Predicate<HenkiloRestDto> predicate) {
        return byOid.values().stream().filter(predicate).sorted(BY_NAME).collect(toList());
    }

    @Override
    public Optional<HenkiloRestDto> findHenkiloByOid(String oid) {
        return ofNullable(byOid.get(oid));
    }
}
