package fi.vm.sade.oikeustulkkirekisteri.service.dto;

import lombok.Getter;
import lombok.Setter;
import org.joda.time.LocalDate;

import java.io.Serializable;

/**
 * User: tommiratamaa
 * Date: 2.6.2016
 * Time: 13.01
 */
@Getter @Setter
public class OikeustulkkiVirkailijaHakuDto implements Serializable, OikeustulkkiHakuehto, KieliRajaus {
    private String hetu;
    private String nimi;
    private String oid;
    private Boolean voimassaNyt;
    private String kielesta;
    private String kieleen;
    private LocalDate voimassaAlku;
    private LocalDate voimassaLoppu;
    private Integer page;
    private Integer count;
}
