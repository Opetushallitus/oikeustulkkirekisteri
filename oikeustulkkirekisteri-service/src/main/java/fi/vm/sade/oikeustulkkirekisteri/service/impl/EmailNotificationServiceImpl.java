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
import fi.vm.sade.oikeustulkkirekisteri.service.EmailTemplateRenderer;
import fi.vm.sade.oikeustulkkirekisteri.service.OikeustulkkiCacheService;
import fi.vm.sade.oikeustulkkirekisteri.util.AuditUtil;
import fi.vm.sade.ryhmasahkoposti.api.dto.EmailData;
import fi.vm.sade.ryhmasahkoposti.api.dto.EmailMessage;
import fi.vm.sade.ryhmasahkoposti.api.dto.EmailRecipient;
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
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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

    private static final String expiryNotificationTemplateName = "expiry_notification";
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
    @Value("${oikeustulkki.expiration.notification.template.name:expiry_notification}")
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

        LocalDate notificationPeriodStart = LocalDate.now();
        LocalDate notificationPeriodEnd = notificationPeriodStart.plus(Period.parse(notificationInterval));

        logger.info("Finding expiring oikeustulkkis to be notified within {} - {}", notificationPeriodStart, notificationPeriodEnd);
        self.findOikeustulkkisToBeNotifiedWithin(notificationPeriodStart, notificationPeriodEnd).forEach(id -> {
            try {
                self.notifyOikeustulkkiOfExpiration(id, notificationPeriodEnd);
                logger.info("Sent {} to oikeustulkki oikeustulkkiId={}", expiryNotificationTemplateName, id);
            } catch (Exception e) {
                logger.error("Sending " + expiryNotificationTemplateName + " to oikeustulkki oikeustulkkiId=" + id + " failed. " +
                        "Reason: " + e.getMessage(), e);
            }
        });
    }

    @Override
    @Transactional(noRollbackFor = {ProcessingException.class, ClientErrorException.class})
    public void notifyOikeustulkkiOfExpiration(
            Long oikeustulkkiId,
            LocalDate expiryDate
    ) throws IOException {
        Oikeustulkki oikeustulkki = oikeustulkkiRepository.findEiPoistettuById(oikeustulkkiId);
        String henkiloOid = oikeustulkki.getTulkki().getHenkiloOid();
        HenkiloRestDto henkilo = found(oikeustulkkiCacheService.findHenkiloByOid(henkiloOid));

        String vastaanottaja = findOikeustulkkiYhteystietoArvo(henkilo, YHTEYSTIETO_SAHKOPOSTI)
                .orElseThrow(() ->
                        new IllegalStateException("Oikeustulkki henkiloOid=" + henkiloOid + " does not have work email.")
                );

        logger.info("Sending notification to oikeustulkki oikeustulkkiId={}, henkiloOid={}", oikeustulkkiId, henkiloOid);

        SahkopostiMuistutus muistutus = createSahkopostiMuistutus(oikeustulkki, vastaanottaja);
        oikeustulkki.getSahkopostiMuistutukset().add(muistutus);

        EmailData emailData = createEmailData(muistutus, henkilo, oikeustulkki, expiryDate);

        try {
            IdHolderDto result = ryhmasahkopostiClient.sendEmail(emailData);
            logger.info("Notification was sent successfully, sahkopostiId={}", result.getId());

            muistutus.setLahetetty(LocalDateTime.now());
            muistutus.setSahkopostiId(Long.parseLong(result.getId()));

            audit.log(AuditUtil.getUser(), OIKEUSTULKKI_SEND_NOTIFICATION_EMAIL, new Target.Builder()
                    .setField("henkiloOid", henkiloOid)
                    .setField("oikeustulkkiId", String.valueOf(oikeustulkkiId))
                    .build(), new Changes.Builder().build());

        } catch (ProcessingException | ClientErrorException e) {
            muistutus.setVirhe(e.getMessage());
            throw e;
        }
    }

    private SahkopostiMuistutus createSahkopostiMuistutus(Oikeustulkki oikeustulkki, String vastaanottaja) {
        SahkopostiMuistutus muistutus = new SahkopostiMuistutus();
        muistutus.setOikeustulkki(oikeustulkki);
        muistutus.setLahettaja(senderEmail);
        muistutus.setVastaanottaja(vastaanottaja);
        muistutus.setTemplateName(templateName);
        muistutus.setKieli(new Kieli(DEFAULT_LANGUAGE_CODE));

        return muistutus;
    }

    private EmailData createEmailData(
            SahkopostiMuistutus muistutus,
            HenkiloRestDto henkilo,
            Oikeustulkki oikeustulkki,
            LocalDate expiryDate
    ) throws IOException {
        EmailMessage email = createEmailMessage(oikeustulkki, expiryDate, muistutus.getLahettaja());
        EmailRecipient recipient = createExpiryNotificationRecipient(muistutus, henkilo);

        EmailData emailData = new EmailData();
        emailData.setEmail(email);
        emailData.getRecipient().add(recipient);

        return emailData;
    }

    private EmailMessage createEmailMessage(
            Oikeustulkki oikeustulkki,
            LocalDate expiryDate,
            String sender
    ) throws IOException {
        EmailMessage email = new EmailMessage();

        String subject = "Rekisteröintisi on päättymässä";
        String body = getEmailBody(oikeustulkki, expiryDate, sender);

        email.setSubject(subject);
        email.setBody(body);
        email.setCallingProcess(callingProcess);
        email.setFrom(sender);
        email.setSender(sender);
        email.setReplyTo(replyTo);
        email.setLanguageCode(DEFAULT_LANGUAGE_CODE);
        email.setCharset("UTF-8");
        email.setHtml(true);

        return email;
    }

    private String getEmailBody(Oikeustulkki oikeustulkki, LocalDate expiryDate, String sender) throws IOException {
        String templateName = expiryNotificationTemplateName +  "." + DEFAULT_LANGUAGE_CODE + ".html";

        Map<String, String> params = new TreeMap<>();
        params.put("expiryDate", firstLanguagePairExpiryDate(oikeustulkki, expiryDate).format(DATE_FORMATTER));
        params.put("sender", sender);

        EmailTemplateRenderer renderer = new EmailTemplateRendererImpl();

        return renderer.renderTemplate(templateName, params);
    }

    private LocalDate firstLanguagePairExpiryDate(Oikeustulkki oikeustulkki, LocalDate expiryDate) {
        return oikeustulkki.getKielet().stream()
                .map(Kielipari::getVoimassaoloPaattyy)
                .filter(date -> (date.isAfter(LocalDate.now()) || date.isEqual(LocalDate.now())) &&
                        (date.isBefore(expiryDate) || date.isEqual(expiryDate)))
                .sorted()
                .findFirst()
                .orElse(expiryDate);
    }

    private EmailRecipient createExpiryNotificationRecipient(SahkopostiMuistutus muistutus, HenkiloRestDto henkilo) {
        EmailRecipient recipient = new EmailRecipient();

        recipient.setEmail(muistutus.getVastaanottaja());
        recipient.setName(henkilo.getKutsumanimi() + " " + henkilo.getSukunimi());
        recipient.setOid(henkilo.getOidHenkilo());
        recipient.setOidType("henkilo");
        recipient.setLanguageCode(DEFAULT_LANGUAGE_CODE);

        return recipient;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Long> findOikeustulkkisToBeNotifiedWithin(LocalDate start, LocalDate end) {
        return oikeustulkkiRepository.findOikeustulkkisVoimassaBetweenWithoutNotificationsIds(start, end);
    }

}
