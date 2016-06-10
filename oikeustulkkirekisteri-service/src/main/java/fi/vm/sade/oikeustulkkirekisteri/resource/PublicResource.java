package fi.vm.sade.oikeustulkkirekisteri.resource;

import com.wordnik.swagger.annotations.Api;
import fi.vm.sade.oikeustulkkirekisteri.service.OikeustulkkiService;
import fi.vm.sade.oikeustulkkirekisteri.service.dto.OikeustulkkiPublicHakuDto;
import fi.vm.sade.oikeustulkkirekisteri.service.dto.OikeustulkkiPublicListDto;
import fi.vm.sade.oikeustulkkirekisteri.service.dto.OikeustulkkiVirkailijaHakuDto;
import fi.vm.sade.oikeustulkkirekisteri.service.dto.OikeustulkkiVirkailijaListDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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

    @RequestMapping(value = "/hae", method = RequestMethod.POST)
    public List<OikeustulkkiPublicListDto> hae(@RequestBody OikeustulkkiPublicHakuDto haku) {
        return oikeustulkkiService.haeJulkinen(haku);
    }
}
