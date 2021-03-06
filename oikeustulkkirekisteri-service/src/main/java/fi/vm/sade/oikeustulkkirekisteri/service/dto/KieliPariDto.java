package fi.vm.sade.oikeustulkkirekisteri.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * User: tommiratamaa
 * Date: 7.6.2016
 * Time: 18.53
 */
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class KieliPariDto implements Serializable {
    private String kielesta;
    private String kieleen;

    private LocalDate voimassaoloAlkaa;
    private LocalDate voimassaoloPaattyy;
}
