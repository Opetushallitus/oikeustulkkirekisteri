package fi.vm.sade.oikeustulkkirekisteri.config;

import fi.vm.sade.properties.OphProperties;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Paths;

import static java.util.Optional.ofNullable;

/**
 * User: tommiratamaa
 * Date: 27.6.2016
 * Time: 13.30
 */
@Configuration
public class UrlConfiguration extends OphProperties {
    public UrlConfiguration() {
        addFiles("/oikeustulkkirekisteri-service-oph.properties");
        if (!ofNullable(System.getProperty("spring.profiles.active")).orElse("").contains("test")) {
            addOptionalFiles(Paths.get(System.getProperties().getProperty("user.home"), "/oph-configuration/common.properties").toString());
        }
    }
}
