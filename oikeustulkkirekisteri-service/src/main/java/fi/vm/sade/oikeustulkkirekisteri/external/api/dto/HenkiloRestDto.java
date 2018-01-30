package fi.vm.sade.oikeustulkkirekisteri.external.api.dto;

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
    private String oidHenkilo;
    private String etunimet;
    private String sukunimi;
    private String kutsumanimi;
    private String sukupuoli;
    private Date syntymaaika;
    private String hetu;
    private Boolean turvakielto;
    @Deprecated // voidaan poistaa kun KJHH-1225 on maalissa
    private String henkiloTyyppi;
    private boolean eiSuomalaistaHetua;
    private boolean passivoitu;
    private boolean yksiloity;
    private boolean yksiloityVTJ;
    private boolean yksilointiYritetty;
    private boolean duplicate;
    private KielisyysDto aidinkieli;
    private List<YhteystiedotRyhmaDto> yhteystiedotRyhma = new ArrayList<>();
}
