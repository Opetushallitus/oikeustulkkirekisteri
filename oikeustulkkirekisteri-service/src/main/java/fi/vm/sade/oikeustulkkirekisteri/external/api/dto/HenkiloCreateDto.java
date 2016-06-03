package fi.vm.sade.oikeustulkkirekisteri.external.api.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * User: tommiratamaa
 * Date: 30.5.2016
 * Time: 14.50
 */
@Getter @Setter
public class HenkiloCreateDto implements Serializable {
    private String etunimet;
    private String kutsumanimi;
    private String sukunimi;
    private String hetu;
    private Date syntymaaika;
    private String oidHenkilo;
    private String sukupuoli;
    private KielisyysDto asiointiKieli;
    private KansalaisuusDto kansalaisuus;
    private KielisyysDto aidinkieli;
    private List<OrganisaatioHenkiloDto> organisaatioHenkilo = new ArrayList<>();
}
