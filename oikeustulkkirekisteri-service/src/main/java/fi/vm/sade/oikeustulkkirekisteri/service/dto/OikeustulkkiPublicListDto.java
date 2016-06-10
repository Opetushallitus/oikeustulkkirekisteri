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
 * Time: 15.14
 */
@Getter @Setter
public class OikeustulkkiPublicListDto implements Serializable {
    private String etunimi;
    private String sukunimi;
    private String maakuntaKoodi;
    private LocalDate paattyy;
    private List<KieliPariDto> kieliParit = new ArrayList<>();
}
