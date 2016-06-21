package fi.vm.sade.oikeustulkkirekisteri.service.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * User: tommiratamaa
 * Date: 20.6.2016
 * Time: 19.23
 */
@Getter @Setter  
public class PermissionCheckResponseDto implements Serializable {
    private boolean accessAllowed = false;
    private String errorMessage;
}
