package fi.vm.sade.oikeustulkkirekisteri.external.interceptor;

import fi.vm.sade.authentication.cas.TicketCachePolicy;

public class NoOpTicketCachePolicy extends TicketCachePolicy {

    @Override
    protected String getTicketFromCache(String cacheKey) {
        return null;
    }

    @Override
    protected void putTicketToCache(String cacheKey, String ticket) {
        // nop
    }

}
