package fi.vm.sade.oikeustulkkirekisteri.service.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Map;

/**
 * User: tommiratamaa
 * Date: 30.5.2016
 * Time: 14.13
 */
@Getter @Setter
public class KoodistoKoodiDto implements Serializable {
    private String koodi;
    private Map<String,String> nimi;
}
