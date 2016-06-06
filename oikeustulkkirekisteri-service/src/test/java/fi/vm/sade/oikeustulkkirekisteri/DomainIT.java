package fi.vm.sade.oikeustulkkirekisteri;

import fi.vm.sade.oikeustulkkirekisteri.domain.Oikeustulkki;
import fi.vm.sade.oikeustulkkirekisteri.domain.Tulkki;
import fi.vm.sade.oikeustulkkirekisteri.repository.OikeustulkkiRepository;
import fi.vm.sade.oikeustulkkirekisteri.test.AbstractIntegrationTest;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import static org.joda.time.LocalDate.now;
import static org.junit.Assert.assertNotNull;

/**
 * TODO: nullpointer in org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean.createNativeEntityManagerFactory
 * 
 * User: tommiratamaa
 * Date: 3.6.2016
 * Time: 14.48
 */
@Ignore
@Transactional
@DirtiesContext
public class DomainIT extends AbstractIntegrationTest {
    @Autowired
    private OikeustulkkiRepository oikeustulkkiRepository;

    @Test
    public void testPersistOikeustulkki() {
        Oikeustulkki ot = new Oikeustulkki();
        ot.setTulkki(new Tulkki("12345.oid"));
        ot.setAlkaa(now());
        ot.setPaattyy(now().plusYears(5));
        ot.setLisatiedot("Lisätiedot");
        oikeustulkkiRepository.save(ot);
        assertNotNull(ot.getId());
        assertNotNull(ot.getTulkki().getId());
    }
}
