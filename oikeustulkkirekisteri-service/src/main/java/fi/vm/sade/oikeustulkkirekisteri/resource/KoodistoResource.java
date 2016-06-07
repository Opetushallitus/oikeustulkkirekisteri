package fi.vm.sade.oikeustulkkirekisteri.resource;

import com.wordnik.swagger.annotations.Api;
import fi.vm.sade.oikeustulkkirekisteri.service.KoodistoService;
import fi.vm.sade.oikeustulkkirekisteri.service.dto.KoodiDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.Optional.ofNullable;

/**
 * User: tommiratamaa
 * Date: 2.6.2016
 * Time: 11.19
 */
@Api("Koodisto")
@RestController
@RequestMapping("/koodisto")
public class KoodistoResource {
    @Autowired
    private KoodistoService koodistoService;

    @RequestMapping(value = "/kielet", method = RequestMethod.GET)
    public List<KoodiDto> getKielet() {
        return koodistoService.getKielet();
    }
    
    @RequestMapping(value = "/maakunnat", method = RequestMethod.GET)
    public List<KoodiDto> getMaakunnat() {
        return koodistoService.getMaakunnat();
    }
}
