package fi.vm.sade.oikeustulkkirekisteri.service.dto;

import lombok.Getter;
import lombok.Setter;
import org.joda.time.LocalDate;

import javax.validation.constraints.NotNull;

/**
 * User: tommiratamaa
 * Date: 7.6.2016
 * Time: 18.41
 */
@Getter @Setter
public class OikeustulkkiEditDto extends OikeustulkkiBaseDto {
    @NotNull 
    private Long id;
    @NotNull
    private LocalDate paattyy;
    private String muokkausviesti;
}
