package fi.vm.sade.oikeustulkkirekisteri.external.api;

import fi.vm.sade.authentication.model.YhteystietoTyyppi;
import static fi.vm.sade.authentication.model.YhteystietoTyyppi.YHTEYSTIETO_KATUOSOITE;
import static fi.vm.sade.authentication.model.YhteystietoTyyppi.YHTEYSTIETO_MAA;
import static fi.vm.sade.oikeustulkkirekisteri.external.api.HenkiloYhteystietoUtil.findOikeustulkkiYhteystietoArvo;
import static fi.vm.sade.oikeustulkkirekisteri.external.api.Yhteystietotyypit.OIKEUSTULKKIREKISTERI_TYYPPI;
import fi.vm.sade.oikeustulkkirekisteri.external.api.dto.HenkiloRestDto;
import fi.vm.sade.oikeustulkkirekisteri.external.api.dto.YhteystiedotDto;
import fi.vm.sade.oikeustulkkirekisteri.external.api.dto.YhteystiedotRyhmaDto;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Test;

public class HenkiloYhteystietoUtilTest {

    private final static AtomicLong SEQ = new AtomicLong();

    @Test
    public void findReadableTyoYhteystietoArvoShouldReturnEmptyWithEmpty() {
        HenkiloRestDto henkilo = new HenkiloRestDto();

        Optional<String> katuosoite = findOikeustulkkiYhteystietoArvo(henkilo, YHTEYSTIETO_KATUOSOITE);

        assertThat(katuosoite).isEmpty();
    }

    @Test
    public void findReadableTyoYhteystietoArvoShouldSkipNullArvo() {
        HenkiloRestDto henkilo = new HenkiloRestDto();
        YhteystiedotRyhmaDto yhteystiedotRyhma1 = createYhteystiedotRyhma(OIKEUSTULKKIREKISTERI_TYYPPI, "alkupera-oikeustulkkirekisteri");
        yhteystiedotRyhma1.getYhteystieto().add(createYhteystiedot(YHTEYSTIETO_KATUOSOITE, "oikeustulkkirekisteri-katuosoite"));
        YhteystiedotRyhmaDto yhteystiedotRyhma2 = createYhteystiedotRyhma("yhteystietotyyppi-vtj1", "alkupera-vtj");
        yhteystiedotRyhma2.getYhteystieto().add(createYhteystiedot(YHTEYSTIETO_KATUOSOITE, "vtj-katuosoite1"));
        YhteystiedotRyhmaDto yhteystiedotRyhma3 = createYhteystiedotRyhma("yhteystietotyyppi-vtj2", "alkupera-vtj");
        yhteystiedotRyhma3.getYhteystieto().add(createYhteystiedot(YHTEYSTIETO_KATUOSOITE, null));
        henkilo.getYhteystiedotRyhma().addAll(Arrays.asList(yhteystiedotRyhma1, yhteystiedotRyhma2, yhteystiedotRyhma3));

        Optional<String> katuosoite = findOikeustulkkiYhteystietoArvo(henkilo, YHTEYSTIETO_KATUOSOITE);

        assertThat(katuosoite).hasValue("oikeustulkkirekisteri-katuosoite");
    }

    @Test
    public void findReadableTyoYhteystietoArvoShouldSkipEmptyArvo() {
        HenkiloRestDto henkilo = new HenkiloRestDto();
        YhteystiedotRyhmaDto yhteystiedotRyhma1 = createYhteystiedotRyhma(OIKEUSTULKKIREKISTERI_TYYPPI, "alkupera-oikeustulkkirekisteri");
        yhteystiedotRyhma1.getYhteystieto().add(createYhteystiedot(YHTEYSTIETO_MAA, "oikeustulkkirekisteri-maa"));
        YhteystiedotRyhmaDto yhteystiedotRyhma2 = createYhteystiedotRyhma("yhteystietotyyppi-vtj1", "alkupera1");
        yhteystiedotRyhma2.getYhteystieto().add(createYhteystiedot(YHTEYSTIETO_MAA, "vtj-maa1"));
        YhteystiedotRyhmaDto yhteystiedotRyhma3 = createYhteystiedotRyhma("yhteystietotyyppi-vtj2", "alkupera1");
        yhteystiedotRyhma3.getYhteystieto().add(createYhteystiedot(YHTEYSTIETO_MAA, ""));
        henkilo.getYhteystiedotRyhma().addAll(Arrays.asList(yhteystiedotRyhma1, yhteystiedotRyhma2, yhteystiedotRyhma3));

        Optional<String> katuosoite = findOikeustulkkiYhteystietoArvo(henkilo, YHTEYSTIETO_MAA);

        assertThat(katuosoite).hasValue("oikeustulkkirekisteri-maa");
    }

    private YhteystiedotRyhmaDto createYhteystiedotRyhma(String kuvaus, String alkuperaTieto) {
        YhteystiedotRyhmaDto yhteystiedotRyhma = new YhteystiedotRyhmaDto();
        yhteystiedotRyhma.setId(SEQ.incrementAndGet());
        yhteystiedotRyhma.setRyhmaKuvaus(kuvaus);
        yhteystiedotRyhma.setRyhmaAlkuperaTieto(alkuperaTieto);
        return yhteystiedotRyhma;
    }

    private YhteystiedotDto createYhteystiedot(YhteystietoTyyppi tyyppi, String arvo) {
        YhteystiedotDto yhteystiedot = new YhteystiedotDto();
        yhteystiedot.setYhteystietoTyyppi(tyyppi);
        yhteystiedot.setYhteystietoArvo(arvo);
        return yhteystiedot;
    }

}
