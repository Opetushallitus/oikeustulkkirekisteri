package fi.vm.sade.oikeustulkkirekisteri.service.dto;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

import java.io.Serializable;

/**
 * User: tommiratamaa
 * Date: 7.6.2016
 * Time: 15.36
 */
@Getter @Setter
public class OsoiteEditDto implements Serializable {
    @NotEmpty
    private String katuosoite;
    @NotEmpty
    private String postinumero;
    @NotEmpty
    private String postitoimipaikka;
}
