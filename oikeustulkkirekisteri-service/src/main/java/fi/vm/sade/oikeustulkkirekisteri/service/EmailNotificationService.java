package fi.vm.sade.oikeustulkkirekisteri.service;

import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

/**
 * User: tommiratamaa
 * Date: 21.6.2016
 * Time: 14.05
 */
public interface EmailNotificationService {
    void scheduledSend();

    @Transactional
    void notifyOikeustulkkiOfExpiration(Long oikeustulkkiId, LocalDate expiryDate) throws IOException;

    List<Long> findOikeustulkkisToBeNotifiedWithin(LocalDate start, LocalDate end);
}
