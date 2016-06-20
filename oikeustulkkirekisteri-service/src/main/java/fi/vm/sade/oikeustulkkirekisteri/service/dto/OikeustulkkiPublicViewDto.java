package fi.vm.sade.oikeustulkkirekisteri.service.dto;

import lombok.Getter;
import lombok.Setter;
import org.joda.time.LocalDate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * User: tommiratamaa
 * Date: 10.6.2016
 * Time: 17.03
 */
@Getter @Setter
public class OikeustulkkiPublicViewDto implements JulkisetYhteystiedot, Serializable {
    private Long id;
    private String etunimet;
    private String sukunimi;
    private String email;
    private String puhelinnumero;
    private String muuYhteystieto;
    private LocalDate paattyy;
    private boolean kokoSuomi;
    private List<String> maakuntaKoodis = new ArrayList<>();
    private List<KieliPariDto> kieliParit = new ArrayList<>();
}
