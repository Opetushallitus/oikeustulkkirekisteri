package fi.vm.sade.oikeustulkkirekisteri.service.impl;

import fi.vm.sade.oikeustulkkirekisteri.service.KoodistoService;
import fi.vm.sade.oikeustulkkirekisteri.service.dto.KoodistoKoodiDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * User: tommiratamaa
 * Date: 30.5.2016
 * Time: 14.12
 */
@Service
public class KoodistoServiceImpl implements KoodistoService {
    
    
    @Override
    public List<KoodistoKoodiDto> getMaakunnat() {
        return null;
    }

    @Override
    public List<KoodistoKoodiDto> getKunnat(Set<String> maakuntas) {
        return null;
    }
}
