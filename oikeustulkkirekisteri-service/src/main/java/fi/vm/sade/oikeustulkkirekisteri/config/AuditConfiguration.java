package fi.vm.sade.oikeustulkkirekisteri.config;

import fi.vm.sade.auditlog.ApplicationType;
import fi.vm.sade.auditlog.Audit;
import fi.vm.sade.oikeustulkkirekisteri.util.LoggerImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuditConfiguration {

    @Bean
    public Audit audit() {
        return new Audit(new LoggerImpl(), "oikeustulkkirekisteri", ApplicationType.VIRKAILIJA);
    }

}
