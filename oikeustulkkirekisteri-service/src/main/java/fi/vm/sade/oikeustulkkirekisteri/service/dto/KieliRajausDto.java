package fi.vm.sade.oikeustulkkirekisteri.service.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * User: tommiratamaa
 * Date: 6.6.2016
 * Time: 11.17
 */
@Getter @Setter
public class KieliRajausDto implements Serializable, KieliRajaus {
    private String kielesta;
    private String kieleen;
}
