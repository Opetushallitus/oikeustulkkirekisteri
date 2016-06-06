package fi.vm.sade.oikeustulkkirekisteri.service.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * User: tommiratamaa
 * Date: 2.6.2016
 * Time: 15.15
 */
@Getter @Setter
public class OikeustulkkiJulkinenHakuDto implements Serializable, OikeustulkkiHakuehto {
    private String nimi;
    private List<KieliRajausDto> kieliparit = new ArrayList<>();
    private Integer page;
    private Integer count;
}
