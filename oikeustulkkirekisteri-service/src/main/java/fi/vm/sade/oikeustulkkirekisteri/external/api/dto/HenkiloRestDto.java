package fi.vm.sade.oikeustulkkirekisteri.external.api.dto;

import fi.vm.sade.authentication.model.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * User: tommiratamaa
 * Date: 10.6.2016
 * Time: 17.34
 */
@Getter @Setter
public class HenkiloRestDto {
    private Long id;
    private String oidHenkilo;
    private String etunimet;
    private String sukunimi;
    private String kutsumanimi;
    private String sukupuoli;
    private Date syntymaaika;
    private String hetu;
    private Boolean turvakielto;
    private HenkiloTyyppi henkiloTyyppi;
    private boolean eiSuomalaistaHetua;
    private boolean passivoitu;
    private boolean yksiloity;
    private boolean yksiloityVTJ;
    private boolean yksilointiYritetty;
    private boolean duplicate;
    private KielisyysDto aidinkieli;
    private List<OrganisaatioHenkiloDto> organisaatioHenkilos = new ArrayList<>();
    private List<YhteystiedotRyhmaDto> yhteystiedotRyhma = new ArrayList<>();
}
