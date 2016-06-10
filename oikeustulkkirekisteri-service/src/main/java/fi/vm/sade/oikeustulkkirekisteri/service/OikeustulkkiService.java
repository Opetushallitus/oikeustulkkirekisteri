package fi.vm.sade.oikeustulkkirekisteri.service;

import fi.vm.sade.generic.common.ValidationException;
import fi.vm.sade.oikeustulkkirekisteri.service.dto.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

import static fi.vm.sade.oikeustulkkirekisteri.service.Constants.CRUD_PERMISSION;
import static fi.vm.sade.oikeustulkkirekisteri.service.Constants.PUBLIC;

/**
 * User: tommiratamaa
 * Date: 30.5.2016
 * Time: 14.11
 */
public interface OikeustulkkiService {
    @PreAuthorize(CRUD_PERMISSION)
    long createOikeustulkki(OikeustulkkiCreateDto dto);

    @PreAuthorize(CRUD_PERMISSION)
    void editOikeustulkki(OikeustulkkiEditDto dto) throws ValidationException;

    @PreAuthorize(CRUD_PERMISSION)
    void deleteOikeustulkki(long id);

    @PreAuthorize(CRUD_PERMISSION)
    OikeustulkkiVirkailijaViewDto getOikeustulkkiVirkailija(long id);

    @PreAuthorize(CRUD_PERMISSION)
    List<OikeustulkkiVirkailijaListDto> haeVirkailija(OikeustulkkiVirkailijaHakuDto hakuDto);

    @PreAuthorize(PUBLIC)
    List<OikeustulkkiPublicListDto> haeJulkinen(OikeustulkkiPublicHakuDto hakuDto);
    
    @PreAuthorize(PUBLIC)
    OikeustulkkiPublicViewDto getJulkinen(long id);
}
