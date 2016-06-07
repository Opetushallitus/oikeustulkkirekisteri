package fi.vm.sade.oikeustulkkirekisteri.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.joda.time.DateTime;

import java.io.Serializable;

/**
 * User: tommiratamaa
 * Date: 7.6.2016
 * Time: 20.14
 */
@AllArgsConstructor
@Getter @Setter
public class OikeustulkkiMuokkausHistoriaDto implements Serializable {
    private DateTime muokattu;
    private String muokkaaja;
    private String muokkausviesti;
    private String muokkajanNimi;
}
