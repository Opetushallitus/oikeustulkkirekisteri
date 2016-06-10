package fi.vm.sade.oikeustulkkirekisteri.service.dto;

import lombok.Getter;
import lombok.Setter;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.io.Serializable;

/**
 * User: tommiratamaa
 * Date: 7.6.2016
 * Time: 20.14
 */
@Getter @Setter
public class OikeustulkkiMuokkausHistoriaDto implements Serializable {
    private DateTime muokattu;
    private LocalDate muokattuPvm;
    private String muokkaaja;
    private String muokkausviesti;
    private String muokkajanNimi;

    public OikeustulkkiMuokkausHistoriaDto(DateTime muokattu, String muokkaaja, String muokkausviesti, String muokkajanNimi) {
        this.muokattu = muokattu;
        this.muokattuPvm = muokattu.toLocalDate();
        this.muokkaaja = muokkaaja;
        this.muokkausviesti = muokkausviesti;
        this.muokkajanNimi = muokkajanNimi;
    }
}
