package fi.vm.sade.oikeustulkkirekisteri.util;

import fi.vm.sade.auditlog.Logger;
import org.slf4j.LoggerFactory;

public class LoggerImpl implements Logger {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(LoggerImpl.class);

    @Override
    public void log(String msg) {
        LOGGER.info(msg);
    }

}
