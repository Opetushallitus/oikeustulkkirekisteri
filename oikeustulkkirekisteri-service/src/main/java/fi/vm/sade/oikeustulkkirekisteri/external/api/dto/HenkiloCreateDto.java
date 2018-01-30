package fi.vm.sade.oikeustulkkirekisteri.external.api.dto;

import fi.vm.sade.authentication.model.HenkiloTyyppi;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

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
    private HenkiloTyyppi henkiloTyyppi;
    private String sukupuoli;
    private KielisyysDto asiointiKieli;
    private Set<KansalaisuusDto> kansalaisuus;
    private KielisyysDto aidinkieli;
}