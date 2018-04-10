package fi.vm.sade.oikeustulkkirekisteri.service.impl;

import fi.vm.sade.oikeustulkkirekisteri.external.api.OppijanumerorekisteriApi;
import fi.vm.sade.oikeustulkkirekisteri.external.api.dto.HenkiloRestDto;
import fi.vm.sade.oikeustulkkirekisteri.repository.OikeustulkkiRepository;
import fi.vm.sade.oikeustulkkirekisteri.service.OikeustulkkiCacheService;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ws.rs.ClientErrorException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;

import static fi.vm.sade.oikeustulkkirekisteri.util.FunctionalUtil.retrying;
import static java.util.Comparator.comparing;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static org.joda.time.DateTime.now;

/**
 * User: tommiratamaa
 * Date: 20.6.2016
 * Time: 17.57
 */
@Service
@Profile("default")
public class OikeustulkkiCacheServiceImpl implements OikeustulkkiCacheService {
    public static final Comparator<? super HenkiloRestDto> BY_NAME = comparing(HenkiloRestDto::getSukunimi)
            .thenComparing(HenkiloRestDto::getSukunimi);
    private static final Logger logger = LoggerFactory.getLogger(OikeustulkkiCacheServiceImpl.class);
    private static final long DEFAULT_CHECK_INTERVAL_MILLIS = 60*1000; // check cache state (/retry if failed) every 1min
    @Value("${henkilo.cache.timeout.period:PT5M}") // refer to ISO8601
    private String cacheTimeoutPeriod; // timeout for cache, defaults to 5 minutes
    private CopyOnWriteArrayList<HenkiloRestDto> allHenkilosOrdererd;
    private Map<String,HenkiloRestDto> byOid;
    private DateTime fullFetchDoneAt;
    private DateTime lastAccessedAt;
    
    @Autowired
    private OikeustulkkiRepository oikeustulkkiRepository;

    @Resource
    private OppijanumerorekisteriApi oppijanumerorekisteriApi;
    
    @Override
    @PostConstruct
    @Transactional(readOnly = true)
    @Scheduled(fixedRateString = "${henkilo.fetch.check.rate.ms:"+ DEFAULT_CHECK_INTERVAL_MILLIS +"}",
                initialDelayString = "${henkilo.fetch.check.delay.ms:"+ DEFAULT_CHECK_INTERVAL_MILLIS +"}")
    public void scheduledFetch() {
        try {
            checkCache();
        } catch (ClientErrorException e) {
            logger.error("Failed to fetch henkilos." + e.getMessage(), e);
        }
    }
    
    private void checkCache() {
        if (cacheNeedsRefresh()) {
            try {
                fetch();
            } catch (ClientErrorException e) {
                throw new IllegalStateException("Henkilö fetch failed: " + e.getMessage(), e);
            }
        }
    }

    private boolean cacheNeedsRefresh() {
        return byOid == null || (fullFetchDoneAt != null && lastAccessedAt != null
                && lastAccessedAt.minus(Period.parse(cacheTimeoutPeriod)).isAfter(fullFetchDoneAt));
    }

    private synchronized void fetch() throws ClientErrorException {
        if (cacheNeedsRefresh()) { // double check if two fetch calls synchronized
            logger.info("FETCHING all oikeustulkkihenkilös");
            allHenkilosOrdererd = new CopyOnWriteArrayList<>(oikeustulkkiRepository.findEiPoistettuOids().stream()
                    .peek(oid -> logger.debug(" > Fetching oid {}", oid))
                    .map(retrying(oppijanumerorekisteriApi::findByOid, 2))
                    .peek(h -> logger.debug(" < Got result for oid {}", h.getOidHenkilo())).collect(toList()));
            byOid = allHenkilosOrdererd.stream().collect(toMap(HenkiloRestDto::getOidHenkilo, h->h));
            fullFetchDoneAt = now();
            logger.info("FETCH all oikeustulkkihenkilös DONE");
        }
    }

    @Override
    @Transactional(readOnly = true, noRollbackFor = RuntimeException.class)
    public synchronized void notifyHenkiloUpdated(String oid) {
        try {
            checkCache();
        } catch (IllegalStateException e) {
            logger.error("Failed to fetch henkilos." + e.getMessage(), e);
            return; // not fatal
        }
        HenkiloRestDto henkilo = oppijanumerorekisteriApi.findByOid(oid);
        byOid.put(oid, henkilo);
        Optional<HenkiloRestDto> existing = allHenkilosOrdererd.stream()
                .filter(h -> h.getOidHenkilo().equals(oid)).findFirst();
        if (existing.isPresent()) {
            allHenkilosOrdererd.set(allHenkilosOrdererd.indexOf(existing.get()), henkilo);
        } else {
            allHenkilosOrdererd.add(henkilo);
        }
        allHenkilosOrdererd.sort(BY_NAME);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<HenkiloRestDto> findHenkilos(Predicate<HenkiloRestDto> predicate) {
        lastAccessedAt = now();
        checkCache();
        return allHenkilosOrdererd.stream().filter(predicate).collect(toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<HenkiloRestDto> findHenkiloByOid(String oid) {
        lastAccessedAt = now();
        checkCache();
        return ofNullable(byOid.get(oid));
    }
}
