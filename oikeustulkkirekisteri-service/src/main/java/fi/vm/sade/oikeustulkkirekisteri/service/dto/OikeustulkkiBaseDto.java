package fi.vm.sade.oikeustulkkirekisteri.service.dto;

import fi.vm.sade.oikeustulkkirekisteri.domain.Oikeustulkki.TutkintoTyyppi;
import fi.vm.sade.oikeustulkkirekisteri.validation.ValidHetu;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;
import org.joda.time.LocalDate;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * User: tommiratamaa
 * Date: 7.6.2016
 * Time: 12.25
 */
@Getter @Setter
public abstract class OikeustulkkiBaseDto implements Serializable {
    @NotEmpty
    private String etunimet;
    @NotEmpty
    private String sukunimi;
    @NotNull @ValidHetu
    private String hetu;
    @NotNull @Valid
    private OsoiteEditDto osoite;
    @NotNull @Email
    private String email;
    private boolean julkaisulupaEmail;
    private String puhelinnumero;
    private boolean julkaisulupaPuhelinnumero;
    private String muuYhteystieto;
    private boolean julkaisulupaMuuYhteystieto;
    private String lisatiedot;
    private boolean julkaisulupa;
    @NotEmpty
    private String kutsumanimi;
    @NotNull
    private TutkintoTyyppi tutkintoTyyppi;
    @NotNull @NotEmpty
    private List<KieliPariDto> kieliParit = new ArrayList<>();
    private boolean kokoSuomi = false;
    private List<String> maakunnat = new ArrayList<>();
}
