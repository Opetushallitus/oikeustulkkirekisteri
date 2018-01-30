package fi.vm.sade.oikeustulkkirekisteri.external.api.dto;

import fi.vm.sade.authentication.model.YhteystietoTyyppi;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * User: tommiratamaa
 * Date: 10.6.2016
 * Time: 17.48
 */
@Getter @Setter
public class YhteystiedotDto implements Serializable {
    private YhteystietoTyyppi yhteystietoTyyppi;
    private String yhteystietoArvo;

    public YhteystiedotDto() {
    }

    public YhteystiedotDto(YhteystietoTyyppi yhteystietoTyyppi) {
        this.yhteystietoTyyppi = yhteystietoTyyppi;
    }
}
