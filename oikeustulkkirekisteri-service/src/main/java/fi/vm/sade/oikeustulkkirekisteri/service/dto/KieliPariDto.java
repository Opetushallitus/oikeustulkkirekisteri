package fi.vm.sade.oikeustulkkirekisteri.service.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * User: tommiratamaa
 * Date: 7.6.2016
 * Time: 18.53
 */
@Getter @Setter
public class KieliPariDto implements Serializable {
    private String kielesta;
    private String kieleen;
}
