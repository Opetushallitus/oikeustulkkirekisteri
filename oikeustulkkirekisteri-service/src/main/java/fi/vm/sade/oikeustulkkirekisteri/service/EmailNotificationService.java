package fi.vm.sade.oikeustulkkirekisteri.service;

import org.joda.time.LocalDate;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * User: tommiratamaa
 * Date: 21.6.2016
 * Time: 14.05
 */
public interface EmailNotificationService {
    void scheduledSend();

    @Transactional
    void sendNotificationToOikeustulkki(Long id, LocalDate expiresOn);

    List<Long> findExpiringTulkkiIds(LocalDate expiresBefore);
}
