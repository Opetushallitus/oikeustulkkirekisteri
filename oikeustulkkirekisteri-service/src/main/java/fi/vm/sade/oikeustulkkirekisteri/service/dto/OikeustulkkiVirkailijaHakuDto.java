package fi.vm.sade.oikeustulkkirekisteri.service.dto;

import lombok.Getter;
import lombok.Setter;
import org.joda.time.LocalDate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * User: tommiratamaa
 * Date: 2.6.2016
 * Time: 13.01
 */
@Getter @Setter
public class OikeustulkkiVirkailijaHakuDto implements Serializable, OikeustulkkiHakuehto {
    private String hetu;
    private String nimi;
    private String oid;
    private Boolean voimassaNyt;
    private LocalDate voimassaAlku;
    private LocalDate voimassaLoppu;
    private List<KieliRajausDto> kieliparit = new ArrayList<>();
    private Integer page;
    private Integer count;
}
