package fi.vm.sade.oikeustulkkirekisteri.external.api.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * User: tommiratamaa
 * Date: 10.6.2016
 * Time: 17.46
 */
@Getter @Setter
public class YhteystiedotRyhmaDto implements Serializable {
    private Long id;
    private String ryhmaKuvaus;
    private String ryhmaAlkuperaTieto;
    private boolean readOnly;
    private Set<YhteystiedotDto> yhteystieto = new HashSet();

    public YhteystiedotRyhmaDto() {
    }

    public YhteystiedotRyhmaDto(String ryhmaKuvaus, String ryhmaAlkuperaTieto) {
        this.ryhmaKuvaus = ryhmaKuvaus;
        this.ryhmaAlkuperaTieto = ryhmaAlkuperaTieto;
    }

}
