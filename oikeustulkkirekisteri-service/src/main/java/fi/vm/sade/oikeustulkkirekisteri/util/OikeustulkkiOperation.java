package fi.vm.sade.oikeustulkkirekisteri.util;

import fi.vm.sade.auditlog.Operation;

public enum OikeustulkkiOperation implements Operation {

    OIKEUSTULKKI_CREATE,
    OIKEUSTULKKI_READ,
    OIKEUSTULKKI_UPDATE,
    OIKEUSTULKKI_DELETE,
    OIKEUSTULKKI_SEND_NOTIFICATION_EMAIL,

}
