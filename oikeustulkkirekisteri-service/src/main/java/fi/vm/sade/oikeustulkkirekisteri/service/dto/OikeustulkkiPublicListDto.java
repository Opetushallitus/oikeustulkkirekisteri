package fi.vm.sade.oikeustulkkirekisteri.service.dto;

import lombok.Getter;
import lombok.Setter;

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
    private Long id;
    private String etunimet;
    private String sukunimi;
    private boolean kokoSuomi;
    private List<String> maakunnat = new ArrayList<>();
    private List<KieliPariDto> kieliParit = new ArrayList<>();
}
