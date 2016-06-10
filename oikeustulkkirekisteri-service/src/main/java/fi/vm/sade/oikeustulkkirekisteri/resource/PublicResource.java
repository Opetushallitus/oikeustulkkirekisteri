package fi.vm.sade.oikeustulkkirekisteri.resource;

import com.wordnik.swagger.annotations.Api;
import fi.vm.sade.oikeustulkkirekisteri.service.OikeustulkkiService;
import fi.vm.sade.oikeustulkkirekisteri.service.dto.KieliRajausDto;
import fi.vm.sade.oikeustulkkirekisteri.service.dto.OikeustulkkiPublicHakuDto;
import fi.vm.sade.oikeustulkkirekisteri.service.dto.OikeustulkkiPublicListDto;
import fi.vm.sade.oikeustulkkirekisteri.service.dto.OikeustulkkiPublicViewDto;
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

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public OikeustulkkiPublicViewDto get(@PathVariable  Long id) {
        return oikeustulkkiService.getJulkinen(id);
    }

    @RequestMapping(value = "/hae", method = RequestMethod.GET)
    public List<OikeustulkkiPublicListDto> haeGet(OikeustulkkiPublicHakuDto haku) {
        return oikeustulkkiService.haeJulkinen(normalize(haku));
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
