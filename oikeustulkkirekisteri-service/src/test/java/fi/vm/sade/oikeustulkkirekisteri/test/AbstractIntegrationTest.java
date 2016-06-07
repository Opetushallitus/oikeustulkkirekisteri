package fi.vm.sade.oikeustulkkirekisteri.test;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * User: tommiratamaa
 * Date: 3.6.2016
 * Time: 14.52
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/spring/application-context.xml")
@ActiveProfiles("test")
public abstract class AbstractIntegrationTest {
    @Before
    public void setUpSecurityContext() {
        SecurityContext ctx = SecurityContextHolder.createEmptyContext();
        ctx.setAuthentication(new UsernamePasswordAuthenticationToken("test","test"));
        SecurityContextHolder.setContext(ctx);
    }
}
