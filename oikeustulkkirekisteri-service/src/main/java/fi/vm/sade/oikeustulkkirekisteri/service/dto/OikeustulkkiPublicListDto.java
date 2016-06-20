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
public class OikeustulkkiPublicListDto implements Serializable, JulkisetYhteystiedot {
    private Long id;
    private String etunimet;
    private String sukunimi;
    private String email;
    private String puhelinnumero;
    private String muuYhteystieto;
    private LocalDate paattyy;
    private boolean kokoSuomi;
    private List<String> maakunnat = new ArrayList<>();
    private List<KieliPariDto> kieliParit = new ArrayList<>();
}
