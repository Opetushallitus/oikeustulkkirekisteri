package fi.vm.sade.oikeustulkkirekisteri.service;

import fi.vm.sade.oikeustulkkirekisteri.service.dto.KoodistoKoodiDto;

import java.util.List;
import java.util.Set;

/**
 * User: tommiratamaa
 * Date: 30.5.2016
 * Time: 14.12
 */
public interface KoodistoService {
    List<KoodistoKoodiDto> getMaakunnat();
    
    List<KoodistoKoodiDto> getKunnat(Set<String> maakuntas);
}
