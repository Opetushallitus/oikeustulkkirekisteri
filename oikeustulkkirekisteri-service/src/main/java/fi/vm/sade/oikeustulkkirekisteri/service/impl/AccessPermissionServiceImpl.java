package fi.vm.sade.oikeustulkkirekisteri.service.impl;

import fi.vm.sade.oikeustulkkirekisteri.repository.OikeustulkkiRepository;
import fi.vm.sade.oikeustulkkirekisteri.service.AccessPermissionService;
import fi.vm.sade.oikeustulkkirekisteri.service.dto.PermissionCheckRequestDto;
import fi.vm.sade.oikeustulkkirekisteri.service.dto.PermissionCheckResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;

/**
 * User: tommiratamaa
 * Date: 20.6.2016
 * Time: 19.24
 */
@Service
public class AccessPermissionServiceImpl implements AccessPermissionService {
    @Autowired
    private OikeustulkkiRepository oikeustulkkiRepository;

    @Override
    @Transactional(readOnly = true)
    public PermissionCheckResponseDto checkHenkiloPermissions(PermissionCheckRequestDto request) {
        PermissionCheckResponseDto response = new PermissionCheckResponseDto();
        if (request.getPersonOidsForSamePerson() == null || request.getPersonOidsForSamePerson().isEmpty()) {
            response.setErrorMessage("No person OIDs provided.");
            return response;
        }
        if (oikeustulkkiRepository.findEiPoistettuOikeustulkkiIdByHenkiloOid(new HashSet<>(request.getPersonOidsForSamePerson())) != null) {
            response.setAccessAllowed(true);
        } else {
            response.setErrorMessage("No oikeustulkki found.");
        }
        return response;
    }
}
