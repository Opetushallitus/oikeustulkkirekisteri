package fi.vm.sade.oikeustulkkirekisteri.external.api.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * User: tommiratamaa
 * Date: 30.5.2016
 * Time: 14.54
 */
@Getter @Setter
public class KielisyysDto implements Serializable {
    private String kieliKoodi;
    private String kieliTyyppi;
}
