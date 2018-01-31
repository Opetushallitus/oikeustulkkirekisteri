package fi.vm.sade.oikeustulkkirekisteri.external.api.dto;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * User: tommiratamaa
 * Date: 10.6.2016
 * Time: 17.34
 */
@Getter @Setter
public class HenkiloRestDto {
    private String oidHenkilo;
    private String etunimet;
    private String sukunimi;
    private String kutsumanimi;
    private String hetu;
    private boolean yksiloityVTJ;
    private KielisyysDto aidinkieli;
    private List<YhteystiedotRyhmaDto> yhteystiedotRyhma = new ArrayList<>();

    private Map<String, Object> others = new LinkedHashMap<>();

    @JsonAnyGetter
    public Map<String, Object> getOthers() {
        return others;
    }

    @JsonAnySetter
    public void setOther(String name, Object value) {
        others.put(name, value);
    }
}
