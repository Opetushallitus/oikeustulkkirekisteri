package fi.vm.sade.oikeustulkkirekisteri.resource;

import com.wordnik.swagger.annotations.Api;
import fi.vm.sade.oikeustulkkirekisteri.service.AccessPermissionService;
import fi.vm.sade.oikeustulkkirekisteri.service.dto.PermissionCheckRequestDto;
import fi.vm.sade.oikeustulkkirekisteri.service.dto.PermissionCheckResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;

/**
 * User: tommiratamaa
 * Date: 20.6.2016
 * Time: 19.19
 */
@Api("Käyttöoikeus")
@Controller
@RequestMapping(value  =  "/permission")
public class PermissionResource {
    @Autowired
    private AccessPermissionService permissionService;

    @ResponseBody
    @RequestMapping(value = "henkilo", method = RequestMethod.POST)
    public PermissionCheckResponseDto checkHenkiloAccess(@Valid @RequestBody PermissionCheckRequestDto request) {
        return permissionService.checkHenkiloPermissions(request);
    }
}
