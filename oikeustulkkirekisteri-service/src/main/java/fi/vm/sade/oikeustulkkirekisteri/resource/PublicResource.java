package fi.vm.sade.oikeustulkkirekisteri.resource;

import com.wordnik.swagger.annotations.Api;
import fi.vm.sade.oikeustulkkirekisteri.service.KoodistoService;
import fi.vm.sade.oikeustulkkirekisteri.service.OikeustulkkiService;
import fi.vm.sade.oikeustulkkirekisteri.service.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * User: tommiratamaa
 * Date: 7.6.2016
 * Time: 20.27
 */
@Api("Oikeustulkki - Julkinen API")
@RestController
@RequestMapping("/public")
public class PublicResource {
    @Autowired
    private OikeustulkkiService oikeustulkkiService;
    
    @Autowired
    private KoodistoService koodistoService;

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public OikeustulkkiPublicViewDto get(@PathVariable  Long id) {
        return oikeustulkkiService.getJulkinen(id);
    }

    @RequestMapping(value = "/hae", method = RequestMethod.GET)
    public List<OikeustulkkiPublicListDto> haeGet(OikeustulkkiPublicHakuDto haku) {
        return oikeustulkkiService.haeJulkinen(normalize(haku));
    }

    @RequestMapping(value = "/kielet", method = RequestMethod.GET)
    public List<KoodiDto> getKielet() {
        return koodistoService.getKielet();
    }

    @RequestMapping(value = "/maakunnat", method = RequestMethod.GET)
    public List<KoodiDto> getMaakunnat() {
        return koodistoService.getMaakunnat();
    }

    private OikeustulkkiPublicHakuDto normalize(OikeustulkkiPublicHakuDto haku) {
        if (haku.getKielesta() != null && haku.getKieleen() != null) {
            if (haku.getKieliparit() == null) {
                haku.setKieliparit(new ArrayList<>());
            }
            haku.getKieliparit().add(new KieliRajausDto(haku.getKielesta(), haku.getKieleen()));
        }
        return haku;
    }

    @RequestMapping(value = "/hae", method = RequestMethod.POST)
    public List<OikeustulkkiPublicListDto> hae(@RequestBody OikeustulkkiPublicHakuDto haku) {
        return oikeustulkkiService.haeJulkinen(normalize(haku));
    }
}
