package fi.vm.sade.oikeustulkkirekisteri.service.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * User: tommiratamaa
 * Date: 2.6.2016
 * Time: 15.15
 */
@Getter @Setter
public class OikeustulkkiJulkinenHakuDto implements Serializable, OikeustulkkiHakuehto, KieliRajaus {
    private String nimi;
    private String kielesta;
    private String kieleen;
    private Integer page;
    private Integer count;
}
