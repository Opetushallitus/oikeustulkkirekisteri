package fi.vm.sade.oikeustulkkirekisteri.external.api.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

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
    @Deprecated // voidaan poistaa kun KJHH-1225 on maalissa
    private String henkiloTyyppi;
}