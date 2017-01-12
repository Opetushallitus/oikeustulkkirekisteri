package fi.vm.sade.oikeustulkkirekisteri.external.api;

import fi.vm.sade.authentication.model.YhteystietoTyyppi;
import fi.vm.sade.oikeustulkkirekisteri.external.api.dto.HenkiloRestDto;
import fi.vm.sade.oikeustulkkirekisteri.external.api.dto.YhteystiedotDto;
import fi.vm.sade.oikeustulkkirekisteri.external.api.dto.YhteystiedotRyhmaDto;
import fi.vm.sade.oikeustulkkirekisteri.util.CustomOrderComparator;
import java.util.Comparator;

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
    public static final String VAKINAINEN_KOTIMAAN_OSOITE_TYYPPI = "yhteystietotyyppi4";
    public static final String VAKINAINEN_ULKOMAAN_OSOITE_TYYPPI = "yhteystietotyyppi5";
    public static final String OIKEUSTULKKIREKISTERI_TYYPPI = "yhteystietotyyppi11";
    public static final Predicate<YhteystiedotRyhmaDto> YT_RYHMA_FILTER_READ = r -> !r.isRemoved() && !r.getRyhmaKuvaus().equals(KOTIOSOITE_TYYPPI);
    private HenkiloYhteystietoUtil() {
    }

    public static Stream<YhteystiedotDto> findYhteystieto(HenkiloRestDto henkilo, Predicate<YhteystiedotRyhmaDto> predicate, YhteystietoTyyppi tyyppi) {
        Comparator<String> comparator = new CustomOrderComparator<>(VAKINAINEN_KOTIMAAN_OSOITE_TYYPPI, VAKINAINEN_ULKOMAAN_OSOITE_TYYPPI, OIKEUSTULKKIREKISTERI_TYYPPI);
        return henkilo.getYhteystiedotRyhma().stream().sorted(comparing(YhteystiedotRyhmaDto::getRyhmaKuvaus, nullsLast(comparator.thenComparing(naturalOrder()))))
                .filter(predicate).flatMap(yt -> yt.getYhteystiedot().stream())
                .filter(yt -> yt.getYhteystietoTyyppi() == tyyppi)
                .filter(yt -> yt.getYhteystietoArvo() != null && !yt.getYhteystietoArvo().isEmpty());
    }
    
    public static Optional<String> findReadableTyoYhteystietoArvo(HenkiloRestDto henkilo, YhteystietoTyyppi tyyppi) {
        return findYhteystieto(henkilo, YT_RYHMA_FILTER_READ, tyyppi).map(YhteystiedotDto::getYhteystietoArvo).findFirst();
    }
}
