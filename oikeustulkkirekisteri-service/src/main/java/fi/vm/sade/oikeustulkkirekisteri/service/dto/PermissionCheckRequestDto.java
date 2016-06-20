package fi.vm.sade.oikeustulkkirekisteri.service.dto;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * User: tommiratamaa
 * Date: 20.6.2016
 * Time: 19.20
 */
@Getter @Setter
public class PermissionCheckRequestDto implements Serializable {
    @NotNull @NotEmpty 
    private List<String> personOidsForSamePerson;
    private List<String> organisationOids;
    private List<String> loggedInUserRoles;
}
