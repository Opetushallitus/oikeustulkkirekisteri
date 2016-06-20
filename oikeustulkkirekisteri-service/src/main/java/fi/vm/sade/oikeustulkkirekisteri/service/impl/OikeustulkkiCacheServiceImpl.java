package fi.vm.sade.oikeustulkkirekisteri.service.impl;

import fi.vm.sade.oikeustulkkirekisteri.external.api.HenkiloApi;
import fi.vm.sade.oikeustulkkirekisteri.external.api.HenkiloApi.ExternalPermissionService;
import fi.vm.sade.oikeustulkkirekisteri.external.api.dto.HenkiloRestDto;
import fi.vm.sade.oikeustulkkirekisteri.repository.OikeustulkkiRepository;
import fi.vm.sade.oikeustulkkirekisteri.service.OikeustulkkiCacheService;
import fi.vm.sade.oikeustulkkirekisteri.util.AbstractService;
import org.joda.time.DateTime;
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

import static java.util.Collections.singletonList;
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
public class OikeustulkkiCacheServiceImpl extends AbstractService implements OikeustulkkiCacheService {
    public static final Comparator<? super HenkiloRestDto> BY_NAME = comparing(HenkiloRestDto::getSukunimi)
            .thenComparing(HenkiloRestDto::getSukunimi);
    private static final Logger logger = LoggerFactory.getLogger(OikeustulkkiCacheServiceImpl.class);
    private static final long A_DAY = 24*3600*1000;
    private CopyOnWriteArrayList<HenkiloRestDto> allHenkilosOrdererd;
    private Map<String,HenkiloRestDto> byOid;
    private DateTime fullFetchDoneAt;
    
    // TODO:remove me
    @Value("${henkilo.integration.deploy.in.progress:true}")
    private boolean fallbackToServiceCalls;
    // TODO:remove me
    @Value("${oikeustulkkirekisteri.organisaatio.oid}")
    private String oikeustulkkirekisteriOrganisaatioOid;

    @Autowired
    private OikeustulkkiRepository oikeustulkkiRepository;

    @Resource
    private HenkiloApi henkiloResourceReadClient;
    
    @Override
    @PostConstruct
    @Scheduled(fixedRateString = "${henkilo.fetch.rate.ms:"+A_DAY+"}",
                initialDelayString = "${henkilo.fetch.delay.ms:"+A_DAY+"}")
    @Transactional(readOnly = true)
    public synchronized void scheduledFetch() {
        try {
            allHenkilosOrdererd = new CopyOnWriteArrayList<>(henkiloResourceReadClient.henkilotByHenkiloOidList(oikeustulkkiRepository.findEiPoistettuOids(),
                    ExternalPermissionService.OIKEUSTULKKIREKISTERI));
            byOid = allHenkilosOrdererd.stream().collect(toMap(HenkiloRestDto::getOidHenkilo, h->h));
            fullFetchDoneAt = now();    
        } catch (ClientErrorException e) {
            if (fallbackToServiceCalls) {
                logger.error("Failed to fetch henkilos." + e.getMessage(), e);
                logger.warn("Fallback to individual service calls to Henkilo service.");
            } else {
                throw e;
            }
        }
    }

    @Override
    @SuppressWarnings("TransactionalAnnotations")
    public synchronized void notifyHenkiloUpdated(String oid) {
        // TODO:remove me
        if (byOid == null && fallbackToServiceCalls) {
            logger.warn("Ignoring notifyHenkiloUpdated for oid {} as henkilo integration not yet deployed.", oid);
            return;
        }
        HenkiloRestDto henkilo = henkiloResourceReadClient.findByOid(oid);
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
        if (allHenkilosOrdererd == null) {
            // TODO:remove me
            if (fallbackToServiceCalls && predicate instanceof HenkiloTermPredicate) {
                logger.warn("Fetching results from henkilo service as a fallback method");
                return henkiloResourceReadClient.listHenkilos(((HenkiloTermPredicate) predicate).getTerm(), null, 0, 0,
                        singletonList(oikeustulkkirekisteriOrganisaatioOid), null, null, null,
                        false, true, false, false, null, false).getResults();
            }
            throw new IllegalStateException("Henkilö fetch not yet done. Should be before application initializes");
        }
        return allHenkilosOrdererd.stream().filter(predicate).collect(toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<HenkiloRestDto> findHenkiloByOid(String oid) {
        if (byOid == null) {
            // TODO:remove me
            if (fallbackToServiceCalls) {
                logger.warn("Fetching results from henkilo service as a fallback method");
                return ofNullable(henkiloResourceReadClient.findByOid(oid));
            }
            throw new IllegalStateException("Henkilö fetch not yet done. Should be before application initializes");
        }
        return ofNullable(byOid.get(oid));
    }
}
