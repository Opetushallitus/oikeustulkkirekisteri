package fi.vm.sade.oikeustulkkirekisteri.external.api;

import fi.vm.sade.authentication.model.YhteystietoTyyppi;
import fi.vm.sade.oikeustulkkirekisteri.external.api.dto.HenkiloRestDto;
import fi.vm.sade.oikeustulkkirekisteri.external.api.dto.YhteystiedotDto;
import fi.vm.sade.oikeustulkkirekisteri.external.api.dto.YhteystiedotRyhmaDto;
import fi.vm.sade.oikeustulkkirekisteri.util.CustomOrderComparator;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.Comparator.comparing;
import static java.util.Comparator.naturalOrder;
import static java.util.Comparator.nullsLast;

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
        return henkilo.getYhteystiedotRyhma().stream().sorted(comparing(YhteystiedotRyhmaDto::getRyhmaKuvaus, nullsLast(new CustomOrderComparator<>(TYOOSOITE_TYYPPI).thenComparing(naturalOrder()))))
                .filter(predicate).flatMap(yt -> yt.getYhteystiedot().stream())
                .filter(yt -> yt.getYhteystietoTyyppi() == tyyppi)
                .filter(yt -> yt.getYhteystietoArvo() != null && !yt.getYhteystietoArvo().isEmpty());
    }

    public static Optional<YhteystiedotDto> findWritableTyoYhteystieto(HenkiloRestDto henkilo, YhteystietoTyyppi tyyppi) {
        return findYhteystieto(henkilo, YT_RYHMA_FILTER_SET, tyyppi).findFirst();
    }
    
    public static Optional<String> findReadableTyoYhteystietoArvo(HenkiloRestDto henkilo, YhteystietoTyyppi tyyppi) {
        return findYhteystieto(henkilo, YT_RYHMA_FILTER_READ, tyyppi).map(YhteystiedotDto::getYhteystietoArvo).findFirst();
    }
}
