package fi.vm.sade.oikeustulkkirekisteri.service;

import fi.vm.sade.oikeustulkkirekisteri.service.dto.KoodiDto;

import java.util.List;
import java.util.Set;

import static java.util.Collections.emptySet;

/**
 * User: tommiratamaa
 * Date: 30.5.2016
 * Time: 14.12
 */
public interface KoodistoService {
    List<KoodiDto> getKielet();
    
    List<KoodiDto> getMaakunnat();
    
    List<KoodiDto> getKunnat(Set<String> maakuntas);
    
    default List<KoodiDto> getKunnat() {
        return getKunnat(emptySet());
    }
}
