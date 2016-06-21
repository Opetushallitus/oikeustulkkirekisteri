package fi.vm.sade.oikeustulkkirekisteri.service;

import fi.vm.sade.oikeustulkkirekisteri.service.dto.PermissionCheckRequestDto;
import fi.vm.sade.oikeustulkkirekisteri.service.dto.PermissionCheckResponseDto;
import org.springframework.security.access.prepost.PreAuthorize;

import static fi.vm.sade.oikeustulkkirekisteri.service.Constants.PUBLIC;

/**
 * User: tommiratamaa
 * Date: 20.6.2016
 * Time: 19.23
 */
public interface AccessPermissionService {
    @PreAuthorize(PUBLIC)
    PermissionCheckResponseDto checkHenkiloPermissions(PermissionCheckRequestDto request);
}
