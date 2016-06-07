package fi.vm.sade.oikeustulkkirekisteri.service;

import fi.vm.sade.oikeustulkkirekisteri.service.dto.OikeustulkkiPublicHakuDto;
import fi.vm.sade.oikeustulkkirekisteri.service.dto.OikeustulkkiPublicListDto;
import fi.vm.sade.oikeustulkkirekisteri.service.dto.OikeustulkkiVirkailijaHakuDto;
import fi.vm.sade.oikeustulkkirekisteri.service.dto.OikeustulkkiVirkailijaListDto;

import java.util.List;

/**
 * User: tommiratamaa
 * Date: 30.5.2016
 * Time: 14.11
 */
public interface OikeustulkkiService {
    List<OikeustulkkiVirkailijaListDto> haeVirkailija(OikeustulkkiVirkailijaHakuDto hakuDto);

    List<OikeustulkkiPublicListDto> haeJulkinen(OikeustulkkiPublicHakuDto hakuDto);
}
