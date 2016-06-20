package fi.vm.sade.oikeustulkkirekisteri.service.dto;

import lombok.Getter;
import lombok.Setter;
import org.joda.time.LocalDate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * User: tommiratamaa
 * Date: 7.6.2016
 * Time: 19.12
 */
@Getter @Setter
public class OikeustulkkiVirkailijaViewDto extends OikeustulkkiBaseDto implements Serializable {
    private Long id;
    private LocalDate paattyy;
    private String aidinkieli;
    private List<OikeustulkkiMuokkausHistoriaDto> muokkaushistoria = new ArrayList<>();
    private List<OikeustulkkiVirkailijaViewDto> aiemmat = new ArrayList<>();
    private Long uusinId;
}
