package fi.vm.sade.oikeustulkkirekisteri.service.dto;

import fi.vm.sade.oikeustulkkirekisteri.domain.Oikeustulkki.TutkintoTyyppi;
import lombok.Getter;
import lombok.Setter;

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
    private String termi; // hetu|nimi|oid
    private String maakuntaKoodi;
    private TutkintoTyyppi tutkintoTyyppi;
    private List<KieliRajausDto> kieliparit = new ArrayList<>();
    private Integer page;
    private Integer count;
}
