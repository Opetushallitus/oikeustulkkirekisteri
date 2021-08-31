package fi.vm.sade.oikeustulkkirekisteri.service.impl;

import fi.vm.sade.auditlog.Audit;
import fi.vm.sade.auditlog.Changes;
import fi.vm.sade.auditlog.Target;
import fi.vm.sade.oikeustulkkirekisteri.domain.Kielipari;
import fi.vm.sade.oikeustulkkirekisteri.domain.Oikeustulkki;
import fi.vm.sade.oikeustulkkirekisteri.domain.SahkopostiMuistutus;
import fi.vm.sade.oikeustulkkirekisteri.domain.embeddable.Kieli;
import fi.vm.sade.oikeustulkkirekisteri.external.api.RyhmasahkopostiApi;
import fi.vm.sade.oikeustulkkirekisteri.external.api.dto.HenkiloRestDto;
import fi.vm.sade.oikeustulkkirekisteri.external.api.dto.IdHolderDto;
import fi.vm.sade.oikeustulkkirekisteri.repository.OikeustulkkiRepository;
import fi.vm.sade.oikeustulkkirekisteri.service.EmailNotificationService;
import fi.vm.sade.oikeustulkkirekisteri.service.OikeustulkkiCacheService;
import fi.vm.sade.oikeustulkkirekisteri.util.AuditUtil;
import fi.vm.sade.ryhmasahkoposti.api.dto.EmailData;
import fi.vm.sade.ryhmasahkoposti.api.dto.EmailMessage;
import fi.vm.sade.ryhmasahkoposti.api.dto.EmailRecipient;
import fi.vm.sade.ryhmasahkoposti.api.dto.ReportedRecipientReplacementDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.ProcessingException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static fi.vm.sade.oikeustulkkirekisteri.external.api.HenkiloYhteystietoUtil.findOikeustulkkiYhteystietoArvo;
import static fi.vm.sade.oikeustulkkirekisteri.external.api.dto.YhteystietoTyyppi.YHTEYSTIETO_SAHKOPOSTI;
import static fi.vm.sade.oikeustulkkirekisteri.util.FoundUtil.found;
import static fi.vm.sade.oikeustulkkirekisteri.util.OikeustulkkiOperation.OIKEUSTULKKI_SEND_NOTIFICATION_EMAIL;

/**
 * User: tommiratamaa
 * Date: 21.6.2016
 * Time: 14.06
 */
@Service
public class EmailNotificationServiceImpl implements EmailNotificationService {
    private static final Logger logger = LoggerFactory.getLogger(EmailNotificationServiceImpl.class);

    private static final String DEFAULT_LANGUAGE_CODE = "fi";
    private static final long DEFAULT_CHECK_INTERVAL_MILLIS = 3600*1000; // check cache state (/retry if failed) every hour
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    @Resource
    private RyhmasahkopostiApi ryhmasahkopostiClient;
    
    @Value("${oikeustulkki.expiration.notification.interval.period:P3M}")
    private String notificationInterval;// default 3 months
    @Value("${oikeustulkki.expiration.notification.sender:oikeustulkkirekisteri@oph.fi}")
    private String senderEmail;
    @Value("${oikeustulkki.expiration.notification.calling.process:oikeustulkkirekisteri@oph.fi}")
    private String replyTo;
    @Value("${oikeustulkki.expiration.notification.template.name:oikeustulkki_vanhenemismuistutus}")
    private String templateName;
    @Value("${oikeustulkki.expiration.notification.calling.process:oikeustulkkirekisteri}")
    private String callingProcess;
    
    @Autowired
    private OikeustulkkiRepository oikeustulkkiRepository;

    @Autowired
    private OikeustulkkiCacheService oikeustulkkiCacheService;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private Audit audit;

    @Override
    @SuppressWarnings("TransactionalAnnotations")
    @Scheduled(fixedRateString = "${email.notification.send.rate.ms:"+ DEFAULT_CHECK_INTERVAL_MILLIS +"}",
            initialDelayString = "${email.notification.send.delay.ms:"+ DEFAULT_CHECK_INTERVAL_MILLIS +"}")
    public void scheduledSend() {
        EmailNotificationService self = applicationContext.getBean(EmailNotificationService.class); // tx-aop-proxy
        LocalDate expiresOnOrBefore = LocalDate.now().plus(Period.parse(notificationInterval));
        logger.info("Finding oikeustulkkis expiring on or before {}", expiresOnOrBefore);
        self.findExpiringTulkkiIds(expiresOnOrBefore).forEach(id -> {
            try {
                self.sendNotificationToOikeustulkki(id, expiresOnOrBefore);
                logger.info("Sent notification to oikeustulkki id={}", id);
            } catch (Exception e) {
                logger.error("Sending notification to oikeustulkki id="+id+" failed. Reason: " + e.getMessage(), e);
            }
        });
    }
    
    @Override
    @Transactional(noRollbackFor = {ProcessingException.class, ClientErrorException.class})
    public void sendNotificationToOikeustulkki(Long id, LocalDate expiresOn) {
        Oikeustulkki oikeustulkki = oikeustulkkiRepository.findEiPoistettuById(id);
        logger.info("Sending notification to oikeustulkki id={}, henkiloOid={}", id, oikeustulkki.getTulkki().getHenkiloOid());
        
        SahkopostiMuistutus muistutus = new SahkopostiMuistutus();
        muistutus.setOikeustulkki(oikeustulkki);
        muistutus.setLahettaja(senderEmail);
        muistutus.setTemplateName(templateName);
        oikeustulkki.getSahkopostiMuistutukset().add(muistutus);
        HenkiloRestDto henkilo = found(oikeustulkkiCacheService.findHenkiloByOid(oikeustulkki.getTulkki().getHenkiloOid()));
        muistutus.setVastaanottaja(findOikeustulkkiYhteystietoArvo(henkilo, YHTEYSTIETO_SAHKOPOSTI)
                .orElseThrow(() -> new IllegalStateException("Oikeustulkki henkiloOid="+oikeustulkki.getTulkki().getHenkiloOid()
                        + " does not have work email.")));
        muistutus.setKieli(new Kieli(DEFAULT_LANGUAGE_CODE));

        EmailData emailData = new EmailData();
        EmailMessage email = new EmailMessage();
        email.setCallingProcess(callingProcess);
        email.setFrom(senderEmail);
        email.setSender(senderEmail);
        email.setReplyTo(replyTo);
        email.setTemplateName(templateName);
        email.setLanguageCode(DEFAULT_LANGUAGE_CODE);
        email.setCharset("UTF-8");
        email.setHtml(true);
        emailData.setEmail(email);
        EmailRecipient recipient = new EmailRecipient();
        recipient.setEmail(muistutus.getVastaanottaja());
        recipient.setName(henkilo.getKutsumanimi() + " " + henkilo.getSukunimi());
        recipient.setOid(henkilo.getOidHenkilo());
        recipient.setOidType("henkilo");
        List<ReportedRecipientReplacementDTO> replacements = new ArrayList<>();
        Optional<LocalDate> voimassaoloPaattyy = oikeustulkki.getKielet().stream()
                .map(Kielipari::getVoimassaoloPaattyy)
                .filter(date -> (date.isAfter(LocalDate.now()) || date.isEqual(LocalDate.now())) &&
                        (date.isBefore(expiresOn) || date.isEqual(expiresOn)))
                .sorted()
                .findFirst();
        replacements.add(new ReportedRecipientReplacementDTO("vanhenee",
                voimassaoloPaattyy.orElse(expiresOn).format(DATE_FORMATTER)));
        recipient.setRecipientReplacements(replacements);
        recipient.setLanguageCode(DEFAULT_LANGUAGE_CODE);
        emailData.getRecipient().add(recipient);
        
        try {
            IdHolderDto result = ryhmasahkopostiClient.sendEmail(emailData);
            logger.info("Sent email {}", result);
            muistutus.setLahetetty(LocalDateTime.now());
            audit.log(AuditUtil.getUser(), OIKEUSTULKKI_SEND_NOTIFICATION_EMAIL, new Target.Builder()
                    .setField("henkiloOid", oikeustulkki.getTulkki().getHenkiloOid())
                    .setField("oikeustulkkiId", String.valueOf(id))
                    .build(), new Changes.Builder().build());
            muistutus.setSahkopostiId(Long.parseLong(result.getId()));
        } catch (ProcessingException|ClientErrorException e) {
            muistutus.setVirhe(e.getMessage());
            throw e;
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Long> findExpiringTulkkiIds(LocalDate expiresOnOrBefore) {
        return oikeustulkkiRepository.findOikeustulkkisVoimassaBetweenWithoutNotificationsIds(
                LocalDate.now(), expiresOnOrBefore);
    }
    
}
