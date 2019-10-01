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
public class OikeustulkkiVirkailijaListDto implements Serializable {
    private Long id;
    private String henkiloOid;
    private String hetu;
    private String etunimi;
    private String sukunimi;
    private boolean kokoSuomi;
    private List<String> maakunnat = new ArrayList<>();
    private List<KieliPariDto> kieliParit = new ArrayList<>();
}
