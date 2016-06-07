package fi.vm.sade.oikeustulkkirekisteri.service;

import fi.vm.sade.oikeustulkkirekisteri.service.dto.KoodiDto;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

import static fi.vm.sade.oikeustulkkirekisteri.service.Constants.PUBLIC;

/**
 * User: tommiratamaa
 * Date: 30.5.2016
 * Time: 14.12
 */
public interface KoodistoService {
    @PreAuthorize(PUBLIC)
    List<KoodiDto> getKielet();

    @PreAuthorize(PUBLIC)
    List<KoodiDto> getMaakunnat();
}
