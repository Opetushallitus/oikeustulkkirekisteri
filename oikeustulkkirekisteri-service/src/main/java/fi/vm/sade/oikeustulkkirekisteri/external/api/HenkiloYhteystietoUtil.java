package fi.vm.sade.oikeustulkkirekisteri.external.api;

import fi.vm.sade.authentication.model.YhteystietoTyyppi;
import static fi.vm.sade.oikeustulkkirekisteri.external.api.Yhteystietotyypit.KOTIOSOITE_TYYPPI;
import static fi.vm.sade.oikeustulkkirekisteri.external.api.Yhteystietotyypit.OIKEUSTULKKIREKISTERI_TYYPPI;
import static fi.vm.sade.oikeustulkkirekisteri.external.api.Yhteystietotyypit.VTJ_JARJESTYS;
import fi.vm.sade.oikeustulkkirekisteri.external.api.dto.HenkiloRestDto;
import fi.vm.sade.oikeustulkkirekisteri.external.api.dto.YhteystiedotDto;
import fi.vm.sade.oikeustulkkirekisteri.external.api.dto.YhteystiedotRyhmaDto;
import fi.vm.sade.oikeustulkkirekisteri.util.CustomOrderComparator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.Comparator.comparing;
import static java.util.Comparator.naturalOrder;
import static java.util.Comparator.nullsLast;
import java.util.List;

/**
 * User: tommiratamaa
 * Date: 21.6.2016
 * Time: 14.54
 */
public class HenkiloYhteystietoUtil {

    public static final Predicate<YhteystiedotRyhmaDto> YT_RYHMA_FILTER_READ = r -> !r.getRyhmaKuvaus().equals(KOTIOSOITE_TYYPPI);
    private HenkiloYhteystietoUtil() {
    }

    public static Stream<YhteystiedotDto> findYhteystieto(HenkiloRestDto henkilo, Predicate<YhteystiedotRyhmaDto> predicate, YhteystietoTyyppi tyyppi, Comparator<String> comparator) {
        return henkilo.getYhteystiedotRyhma().stream()
                .sorted(comparing(YhteystiedotRyhmaDto::getRyhmaKuvaus, nullsLast(comparator.thenComparing(naturalOrder()))))
                .filter(predicate)
                .flatMap(yt -> yt.getYhteystieto().stream())
                .filter(yt -> yt.getYhteystietoTyyppi() == tyyppi)
                .filter(yt -> yt.getYhteystietoArvo() != null && !yt.getYhteystietoArvo().isEmpty());
    }

    public static Optional<String> findVtjYhteystietoArvo(HenkiloRestDto henkilo, YhteystietoTyyppi tyyppi) {
        List<String> order = new ArrayList<>();
        order.addAll(Arrays.asList(VTJ_JARJESTYS));
        order.add(OIKEUSTULKKIREKISTERI_TYYPPI);
        CustomOrderComparator<String> comparator = new CustomOrderComparator<>(order);
        return findYhteystieto(henkilo, YT_RYHMA_FILTER_READ, tyyppi, comparator)
                .map(YhteystiedotDto::getYhteystietoArvo).findFirst();
    }

    public static Optional<String> findOikeustulkkiYhteystietoArvo(HenkiloRestDto henkilo, YhteystietoTyyppi tyyppi) {
        List<String> order = new ArrayList<>();
        order.add(OIKEUSTULKKIREKISTERI_TYYPPI);
        order.addAll(Arrays.asList(VTJ_JARJESTYS));
        CustomOrderComparator<String> comparator = new CustomOrderComparator<>(order);
        return findYhteystieto(henkilo, YT_RYHMA_FILTER_READ, tyyppi, comparator)
                .map(YhteystiedotDto::getYhteystietoArvo).findFirst();
    }
}
