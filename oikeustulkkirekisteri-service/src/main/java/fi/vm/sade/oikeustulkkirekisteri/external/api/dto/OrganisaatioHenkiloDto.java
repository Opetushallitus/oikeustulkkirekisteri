package fi.vm.sade.oikeustulkkirekisteri.external.api.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * User: tommiratamaa
 * Date: 30.5.2016
 * Time: 15.00
 */
@Getter @Setter
public class OrganisaatioHenkiloDto_ implements Serializable {
    private String organisaatioOid;
    private String tehtavanimike;
}
