package fi.vm.sade.oikeustulkkirekisteri.external.api.dto;

import fi.vm.sade.koodisto.service.types.common.KoodiMetadataType;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

/**
 * User: tommiratamaa
 * Date: 30.5.2016
 * Time: 14.13
 */
@Getter @Setter
public class KoodistoKoodiDto implements Serializable {
    private String koodiUri;
    private String koodiArvo;
    private List<KoodiMetadataType> metadata;
    
    public Map<String,String> getNimi() {
        return metadata.stream().collect(toMap(m -> m.getKieli().name(), KoodiMetadataType::getNimi));
    }
}
