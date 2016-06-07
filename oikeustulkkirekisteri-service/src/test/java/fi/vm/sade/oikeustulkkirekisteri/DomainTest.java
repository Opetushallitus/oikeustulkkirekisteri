package fi.vm.sade.oikeustulkkirekisteri;

import fi.vm.sade.oikeustulkkirekisteri.domain.Kielipari;
import fi.vm.sade.oikeustulkkirekisteri.domain.Oikeustulkki;
import fi.vm.sade.oikeustulkkirekisteri.domain.Oikeustulkki.TutkintoTyyppi;
import fi.vm.sade.oikeustulkkirekisteri.domain.Sijainti;
import fi.vm.sade.oikeustulkkirekisteri.domain.Sijainti.Tyyppi;
import fi.vm.sade.oikeustulkkirekisteri.domain.Tulkki;
import fi.vm.sade.oikeustulkkirekisteri.domain.embeddable.Kieli;
import fi.vm.sade.oikeustulkkirekisteri.repository.OikeustulkkiRepository;
import fi.vm.sade.oikeustulkkirekisteri.test.AbstractIntegrationTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import static fi.vm.sade.oikeustulkkirekisteri.domain.Oikeustulkki.TutkintoTyyppi.OIKEUSTULKIN_ERIKOISAMMATTITUTKINTO;
import static org.joda.time.LocalDate.now;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

/**
 * User: tommiratamaa
 * Date: 3.6.2016
 * Time: 14.48
 */
@Transactional
@DirtiesContext
public class DomainTest extends AbstractIntegrationTest {
    @Autowired
    private OikeustulkkiRepository oikeustulkkiRepository;

    @Test
    public void testPersistOikeustulkki() {
        Oikeustulkki ot = new Oikeustulkki();
        ot.setTulkki(new Tulkki("12345.oid"));
        ot.setAlkaa(now());
        ot.setTutkintoTyyppi(OIKEUSTULKIN_ERIKOISAMMATTITUTKINTO);
        ot.setPaattyy(now().plusYears(5));
        ot.setLisatiedot("Lisätiedot");
        ot.getSijainnit().add(new Sijainti(ot, Tyyppi.KOKO_SUOMI));
        ot.getKielet().add(new Kielipari(ot, new Kieli("fi"), new Kieli("sv")));
        oikeustulkkiRepository.save(ot);
        assertNotNull(ot.getId());
        assertNotNull(ot.getTulkki().getId());
        assertEquals(1, ot.getKielet().size());
        assertNotNull(ot.getKielet().iterator().next().getId());
        assertEquals(1, ot.getSijainnit().size());
        assertNotNull(ot.getSijainnit().iterator().next().getId());
    }
}
