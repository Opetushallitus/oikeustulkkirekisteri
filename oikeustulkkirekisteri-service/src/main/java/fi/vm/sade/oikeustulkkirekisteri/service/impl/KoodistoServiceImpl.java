package fi.vm.sade.oikeustulkkirekisteri.service.impl;

import fi.vm.sade.oikeustulkkirekisteri.external.api.KoodistoApi;
import fi.vm.sade.oikeustulkkirekisteri.external.api.dto.KoodistoKoodiDto;
import fi.vm.sade.oikeustulkkirekisteri.service.KoodistoService;
import fi.vm.sade.oikeustulkkirekisteri.service.dto.KoodiDto;
import fi.vm.sade.oikeustulkkirekisteri.util.AbstractService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.Comparator.*;
import static java.util.stream.Collectors.toList;

/**
 * User: tommiratamaa
 * Date: 30.5.2016
 * Time: 14.12
 */
@Service
@SuppressWarnings("TransactionalAnnotations")
public class KoodistoServiceImpl extends AbstractService implements KoodistoService {
    private static final String EI_TIEDOSSA_MAAKUNTA = "99";
    private static final Function<KoodistoKoodiDto, KoodiDto> CONVERT_DTO = k -> new KoodiDto(k.getKoodiArvo(), k.getKoodiUri(), k.getNimi());
    private static final Comparator<Map<String,String>> NIMI_COMPARATOR = comparing((Map<String,String> m) -> m.get("FI"), nullsLast(naturalOrder()))
                .thenComparing(m -> m.get("SV"), nullsLast(naturalOrder()));

    @Resource
    private KoodistoApi koodistoResourceClient;

    @Override
    @Cacheable("kielet")
    public List<KoodiDto> getKielet() {
        return convertedAndSorted(koodistoResourceClient.listKoodis("kieli").stream()).collect(toList());
    }

    @Override
    @Cacheable("maakunnat")
    public List<KoodiDto> getMaakunnat() {
        return convertedAndSorted(koodistoResourceClient.listKoodis("maakunta").stream()
                .filter(k -> !EI_TIEDOSSA_MAAKUNTA.equals(k.getKoodiArvo()))).collect(toList());
    }
    
    private Stream<KoodiDto> convertedAndSorted(Stream<KoodistoKoodiDto> from) {
        return from.map(CONVERT_DTO).sorted(comparing(KoodiDto::getNimi, NIMI_COMPARATOR));
    }
}
