package fi.vm.sade.oikeustulkkirekisteri.service.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * User: tommiratamaa
 * Date: 2.6.2016
 * Time: 10.56
 */
@Getter @Setter
public class KoodiDto implements Serializable {
    private String arvo;
    private String uri;
    private Map<String,String> nimi = new HashMap<>();

    public KoodiDto() {
    }

    public KoodiDto(String arvo, String uri, Map<String, String> nimi) {
        this.arvo = arvo;
        this.uri = uri;
        this.nimi = nimi;
    }
}
