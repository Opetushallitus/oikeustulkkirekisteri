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
    private boolean removed = false;
    private Set<YhteystiedotDto> yhteystiedot = new HashSet();

    public YhteystiedotRyhmaDto() {
    }

    public YhteystiedotRyhmaDto(String ryhmaKuvaus) {
        this.ryhmaKuvaus = ryhmaKuvaus;
    }

}
