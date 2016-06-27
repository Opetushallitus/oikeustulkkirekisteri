package fi.vm.sade.oikeustulkkirekisteri.external.api;

import fi.vm.sade.authentication.model.YhteystietoTyyppi;
import fi.vm.sade.oikeustulkkirekisteri.external.api.dto.HenkiloRestDto;
import fi.vm.sade.oikeustulkkirekisteri.external.api.dto.YhteystiedotDto;
import fi.vm.sade.oikeustulkkirekisteri.external.api.dto.YhteystiedotRyhmaDto;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * User: tommiratamaa
 * Date: 21.6.2016
 * Time: 14.54
 */
public class HenkiloYhteystietoUtil {
    public static final String KOTIOSOITE_TYYPPI = "yhteystietotyyppi1";
    public static final String TYOOSOITE_TYYPPI = "yhteystietotyyppi2";
    public static final Predicate<YhteystiedotRyhmaDto> YT_RYHMA_FILTER_READ = r -> !r.isRemoved() && !r.getRyhmaKuvaus().equals(KOTIOSOITE_TYYPPI);
    public static final Predicate<YhteystiedotRyhmaDto> YT_RYHMA_FILTER_SET = YT_RYHMA_FILTER_READ.and(r -> !r.isReadOnly());
    private HenkiloYhteystietoUtil() {
    }

    public static Stream<YhteystiedotDto> findYhteystieto(HenkiloRestDto henkilo, Predicate<YhteystiedotRyhmaDto> predicate, YhteystietoTyyppi tyyppi) {
        return henkilo.getYhteystiedotRyhma().stream().filter(predicate).flatMap(yt -> yt.getYhteystiedot().stream())
                .filter(yt -> yt.getYhteystietoTyyppi() == tyyppi);
    }

    public static Optional<YhteystiedotDto> findWritableTyoYhteystieto(HenkiloRestDto henkilo, YhteystietoTyyppi tyyppi) {
        return findYhteystieto(henkilo, YT_RYHMA_FILTER_SET, tyyppi).findFirst();
    }
    
    public static Optional<String> findReadableTyoYhteystietoArvo(HenkiloRestDto henkilo, YhteystietoTyyppi tyyppi) {
        return findYhteystieto(henkilo, YT_RYHMA_FILTER_READ, tyyppi).map(YhteystiedotDto::getYhteystietoArvo).findFirst();
    }
}
