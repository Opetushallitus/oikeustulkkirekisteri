package fi.vm.sade.oikeustulkkirekisteri.service.impl;

import fi.vm.sade.oikeustulkkirekisteri.external.api.KoodistoApi;
import fi.vm.sade.oikeustulkkirekisteri.external.api.dto.KoodistoKoodiDto;
import fi.vm.sade.oikeustulkkirekisteri.service.KoodistoService;
import fi.vm.sade.oikeustulkkirekisteri.service.dto.KoodiDto;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
public class KoodistoServiceImpl implements KoodistoService {
    private static final String EI_TIEDOSSA_KUNTA = "999";
    private static final Function<KoodistoKoodiDto, KoodiDto> CONVERT_DTO = k -> new KoodiDto(k.getKoodiArvo(), k.getKoodiUri(), k.getNimi());
    private static final Comparator<Map<String,String>> NIMI_COMPARATOR = (a,b) -> comparing((Map<String,String> m) -> m.get("FI"), nullsLast(naturalOrder()))
                .thenComparing(m -> m.get("SV"), nullsLast(naturalOrder())).compare(a,b);

    @Resource
    private KoodistoApi koodistoResourceClient;
    
    @Override
    @Cacheable("maakunnat")
    public List<KoodiDto> getMaakunnat() {
        return convertedAndSorted(koodistoResourceClient.listKoodis("maakunta").stream()).collect(toList());
    }
    
    private Stream<KoodiDto> convertedAndSorted(Stream<KoodistoKoodiDto> from) {
        return from.map(CONVERT_DTO).sorted(comparing(KoodiDto::getNimi, NIMI_COMPARATOR))
                .filter(k -> !EI_TIEDOSSA_KUNTA.equals(k.getArvo()));
    }
    
    @Override
    @Cacheable(value = "kunnat", key = "#maakuntas", condition = "#maakuntas.size() < 2")
    public List<KoodiDto> getKunnat(Set<String> maakuntas) {
        if (maakuntas.isEmpty()) {
            return convertedAndSorted(koodistoResourceClient.listKoodis("kunta").stream()).collect(toList());
        }
        return convertedAndSorted(maakuntas.stream().flatMap(mk -> koodistoResourceClient.listAlakoodit(mk).stream())).collect(toList());
    }
}
