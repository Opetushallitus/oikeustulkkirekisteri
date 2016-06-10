package fi.vm.sade.oikeustulkkirekisteri.resource;

import com.wordnik.swagger.annotations.Api;
import fi.vm.sade.generic.common.ValidationException;
import fi.vm.sade.oikeustulkkirekisteri.service.OikeustulkkiService;
import fi.vm.sade.oikeustulkkirekisteri.service.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * User: tommiratamaa
 * Date: 31.5.2016
 * Time: 12.42
 */
@Api("Oikeustulkki")
@RestController
@RequestMapping("/oikeustulkki")
public class OikeustulkkiResource {
    private static final Logger logger = LoggerFactory.getLogger(OikeustulkkiResource.class);
    
    @Autowired
    private OikeustulkkiService oikeustulkkiService;

    @RequestMapping(value = "/hae", method = RequestMethod.POST)
    public List<OikeustulkkiVirkailijaListDto> hae(@RequestBody OikeustulkkiVirkailijaHakuDto haku) {
        return oikeustulkkiService.haeVirkailija(haku);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public OikeustulkkiVirkailijaViewDto getOikeustulkki(@PathVariable Long id) {
        logger.info("getOikeustulkki {} with user roles={}", id, SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).collect(Collectors.toList()));
        return oikeustulkkiService.getOikeustulkkiVirkailija(id);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public void deleteOikeustulkki(@PathVariable Long id) {
        oikeustulkkiService.deleteOikeustulkki(id);
    }

    @ResponseBody
    @RequestMapping(value = "", method = RequestMethod.POST)
    public long createOikeustulkki(@Valid @RequestBody OikeustulkkiCreateDto dto) {
        return oikeustulkkiService.createOikeustulkki(dto);
    }

    @ResponseBody
    @RequestMapping(value = "", method = RequestMethod.PUT)
    public void editOikeustulkki(@Valid @RequestBody OikeustulkkiEditDto dto) throws ValidationException {
        oikeustulkkiService.editOikeustulkki(dto);
    }
}
