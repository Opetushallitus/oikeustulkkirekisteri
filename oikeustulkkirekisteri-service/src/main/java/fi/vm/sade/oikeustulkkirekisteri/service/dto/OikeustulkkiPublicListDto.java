package fi.vm.sade.oikeustulkkirekisteri.service.dto;

import lombok.Getter;
import lombok.Setter;
import org.joda.time.LocalDate;

import java.io.Serializable;

/**
 * User: tommiratamaa
 * Date: 2.6.2016
 * Time: 15.14
 */
@Getter @Setter
public class OikeustulkkiPublicListDto implements Serializable {
    private String etunimi;
    private String sukunimi;
    private LocalDate paattyy;
    private String kielesta;
    private String kieleen;
}
