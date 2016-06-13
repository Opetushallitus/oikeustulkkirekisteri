package fi.vm.sade.oikeustulkkirekisteri.util;

import fi.vm.sade.auditlog.ApplicationType;
import fi.vm.sade.auditlog.Audit;
import fi.vm.sade.auditlog.oikeustulkkirekisteri.LogMessage;
import fi.vm.sade.auditlog.oikeustulkkirekisteri.LogMessage.LogMessageBuilder;
import fi.vm.sade.auditlog.oikeustulkkirekisteri.OikeustulkkiOperation;

/**
 * User: tommiratamaa
 * Date: 13.6.2016
 * Time: 13.42
 */
public abstract class AbstractService {
    protected Audit auditLog = new Audit("oikeustulkkirekisteri", ApplicationType.VIRKAILIJA);
    
    protected LogMessageBuilder builder(OikeustulkkiOperation operation) {
        return LogMessage.builder().setOperaatio(operation);
    }
}
