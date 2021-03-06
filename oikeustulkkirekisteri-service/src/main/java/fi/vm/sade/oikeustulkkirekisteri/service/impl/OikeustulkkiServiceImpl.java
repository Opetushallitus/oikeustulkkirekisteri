package fi.vm.sade.oikeustulkkirekisteri.service.impl;

import fi.vm.sade.auditlog.Audit;
import fi.vm.sade.auditlog.Changes;
import fi.vm.sade.auditlog.Target;
import fi.vm.sade.generic.common.ValidationException;
import fi.vm.sade.oikeustulkkirekisteri.domain.*;
import fi.vm.sade.oikeustulkkirekisteri.domain.embeddable.Kieli;
import fi.vm.sade.oikeustulkkirekisteri.external.api.OppijanumerorekisteriApi;
import fi.vm.sade.oikeustulkkirekisteri.external.api.dto.*;
import fi.vm.sade.oikeustulkkirekisteri.repository.OikeustulkkiRepository;
import fi.vm.sade.oikeustulkkirekisteri.repository.TullkiRepository;
import fi.vm.sade.oikeustulkkirekisteri.repository.custom.CustomFlushRepository;
import fi.vm.sade.oikeustulkkirekisteri.service.OikeustulkkiCacheService;
import fi.vm.sade.oikeustulkkirekisteri.service.OikeustulkkiService;
import fi.vm.sade.oikeustulkkirekisteri.service.dto.*;
import fi.vm.sade.oikeustulkkirekisteri.util.AuditUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static fi.vm.sade.oikeustulkkirekisteri.domain.Sijainti.Tyyppi.KOKO_SUOMI;
import static fi.vm.sade.oikeustulkkirekisteri.domain.Sijainti.Tyyppi.MAAKUNTA;
import static fi.vm.sade.oikeustulkkirekisteri.external.api.HenkiloYhteystietoUtil.findOikeustulkkiYhteystietoArvo;
import static fi.vm.sade.oikeustulkkirekisteri.external.api.HenkiloYhteystietoUtil.findVtjYhteystietoArvo;
import static fi.vm.sade.oikeustulkkirekisteri.external.api.YhteystietojenAlkuperat.OIKEUSTULKKIREKISTERI_ALKUPERA;
import static fi.vm.sade.oikeustulkkirekisteri.external.api.Yhteystietotyypit.OIKEUSTULKKIREKISTERI_TYYPPI;
import static fi.vm.sade.oikeustulkkirekisteri.external.api.dto.YhteystietoTyyppi.*;
import static fi.vm.sade.oikeustulkkirekisteri.service.impl.OikeustulkkiHakuSpecificationBuilder.*;
import static fi.vm.sade.oikeustulkkirekisteri.util.FoundUtil.found;
import static fi.vm.sade.oikeustulkkirekisteri.util.FunctionalUtil.or;
import static fi.vm.sade.oikeustulkkirekisteri.util.FunctionalUtil.retrying;
import static fi.vm.sade.oikeustulkkirekisteri.util.OikeustulkkiOperation.*;
import static java.util.Collections.singletonList;
import static java.util.Comparator.comparing;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.*;
import static org.springframework.data.jpa.domain.Specifications.where;

/**
 * User: tommiratamaa
 * Date: 30.5.2016
 * Time: 14.11
 */
@Service
public class OikeustulkkiServiceImpl implements OikeustulkkiService {
    private static final Logger logger = LoggerFactory.getLogger(OikeustulkkiServiceImpl.class);
    
    private static final Integer DEFAULT_COUNT = 20;

    @Autowired
    private OikeustulkkiRepository oikeustulkkiRepository;
    
    @Autowired
    private TullkiRepository tullkiRepository;

    @Resource
    private OppijanumerorekisteriApi oppijanumerorekisteriServiceUserClient;

    @Autowired
    private OikeustulkkiCacheService oikeustulkkiCacheService;

    @Autowired
    private CustomFlushRepository customFlushRepository;

    @Autowired
    private Audit audit;

    @Value("${oikeustulkki.tehtavanimike:Oikeustulkki}")
    private String oikeustulkkiTehtavanimike;
    
    @Value("${oikeustulkki.voimassaolo:P5Y}")
    private String oikeustulkkiVoimassaolo;
    
    private static Specifications<Oikeustulkki> spec(OikeustulkkiVirkailijaHakuDto dto) {
        Specifications<Oikeustulkki> where =  eiPoistettu
                .and(tutkintoTyyppi(dto.getTutkintoTyyppi()))
                .and(toimiiMaakunnissa(singletonList(dto.getMaakuntaKoodi())))
                .and(kieliparit(dto.getKieliparit()));
        return where(where);
    }
    
    private static Specifications<Oikeustulkki> spec(OikeustulkkiPublicHakuDto dto) {
        Specifications<Oikeustulkki> where = eiPoistettu
                .and(jokuKielipariVoimassa(LocalDate.now()))
                .and(julkaisulupa()).and(kieliparit(dto.getKieliparit(), LocalDate.now()))
                .and(toimiiMaakunnissa(singletonList(dto.getMaakuntaKoodi())));
        return where(where);
    }
    
    @Override
    @Transactional
    public long createOikeustulkki(OikeustulkkiCreateDto dto) throws ValidationException {
        logger.info("OikeustulkkiService.createOikeustulkki");
        Optional<HenkiloRestDto> existingHenkilo = retrying(() 
                -> listHenkilosByHetu(oppijanumerorekisteriServiceUserClient, dto.getHetu()).stream().findFirst(), 2)
                    .get().orFail(ex -> new ValidationException("Searching existing henkilo by HETU failed.",
                        "henkilpalvelu.search.by.hetu.failed"));
        Oikeustulkki oikeustulkki = new Oikeustulkki();
        if (existingHenkilo.isPresent()) {
            logger.info("Found existing henkilo by hetu oid={}", existingHenkilo.get().getOidHenkilo());
            if (!isSameHenkiloByName(existingHenkilo.get(), dto)) {
                throw new ValidationException("Name of existing henkilo " + existingHenkilo.get().getEtunimet() 
                                + " " + existingHenkilo.get().getSukunimi()
                            + " does not match inserted " + dto.getEtunimet() + " " + dto.getSukunimi(),
                        "oikeustulkki.different.henkilo.found.by.hetu");
            }
            oikeustulkki.setTulkki(tullkiRepository.findByHenkiloOid(existingHenkilo.get().getOidHenkilo()));
            if (oikeustulkki.getTulkki() == null) {
                logger.info("Created new Tulkki by existing henkilo.");
                oikeustulkki.setTulkki(new Tulkki(existingHenkilo.get().getOidHenkilo()));
            }
        } else if (oikeustulkki.getTulkki() == null) {
            logger.info("Creating new Tulkki into oppijanumerorekisteri");
            oikeustulkki.setTulkki(createTulkki(dto));
        }
        convert(dto, oikeustulkki);
        updateHenkilo(oikeustulkki.getTulkki().getHenkiloOid(), dto);
        oikeustulkkiRepository.save(oikeustulkki);
        audit.log(AuditUtil.getUser(), OIKEUSTULKKI_CREATE, new Target.Builder()
                .setField("henkiloOid", oikeustulkki.getTulkki().getHenkiloOid())
                .setField("oikeustulkkiId", String.valueOf(oikeustulkki.getId()))
                .build(), new Changes.Builder().build());
        oikeustulkkiCacheService.notifyHenkiloUpdated(oikeustulkki.getTulkki().getHenkiloOid());
        logger.info("OikeustulkkiService.createOikeustulkki DONE");
        return oikeustulkki.getId();
    }

    private boolean isSameHenkiloByName(HenkiloRestDto henkilo, OikeustulkkiCreateDto tulkki) {
        Set<String> henkiloEtunimet = etunimetLower(henkilo.getEtunimet()).collect(toSet());
        return tulkki.getSukunimi().trim().equalsIgnoreCase(henkilo.getSukunimi().trim())
                && etunimetLower(tulkki.getEtunimet()).anyMatch(henkiloEtunimet::contains);
    }
    
    private Stream<String> etunimetLower(String etunimet) {
        return etunimet == null || etunimet.trim().isEmpty() ? Stream.empty() : Stream.of(etunimet.trim().split("\\s+")).map(String::toLowerCase);
    }
    
    @Override
    @Transactional
    public void editOikeustulkki(OikeustulkkiEditDto dto) throws ValidationException {
        logger.info("OikeustulkkiService.editOikeustulkki id={}", dto.getId());
        Oikeustulkki oikeustulkki = found(oikeustulkkiRepository.findEiPoistettuById(dto.getId()));
        convert(dto, oikeustulkki);
        updateHenkilo(oikeustulkki.getTulkki().getHenkiloOid(), dto);
        OikeustulkkiMuokkaus muokkaus = new OikeustulkkiMuokkaus();
        muokkaus.setMuokkaaja(SecurityContextHolder.getContext().getAuthentication().getName());
        muokkaus.setOikeustulkki(oikeustulkki);
        muokkaus.setMuokkausviesti(dto.getMuokkausviesti());
        oikeustulkki.getMuokkaukset().add(muokkaus);
        audit.log(AuditUtil.getUser(), OIKEUSTULKKI_UPDATE, new Target.Builder()
                .setField("henkiloOid", oikeustulkki.getTulkki().getHenkiloOid())
                .setField("oikeustulkkiId", String.valueOf(oikeustulkki.getId()))
                .build(), new Changes.Builder().build());
        oikeustulkkiCacheService.notifyHenkiloUpdated(oikeustulkki.getTulkki().getHenkiloOid());
        logger.info("OikeustulkkiService.editOikeustulkki id={} DONE", dto.getId());
    }

    @Override
    @Transactional
    public void deleteOikeustulkki(long id) {
        Oikeustulkki oikeustulkki = found(oikeustulkkiRepository.findEiPoistettuById(id));
        oikeustulkki.markPoistettu(SecurityContextHolder.getContext().getAuthentication().getName());
        audit.log(AuditUtil.getUser(), OIKEUSTULKKI_DELETE, new Target.Builder()
                .setField("henkiloOid", oikeustulkki.getTulkki().getHenkiloOid())
                .setField("oikeustulkkiId", String.valueOf(oikeustulkki.getId()))
                .build(), new Changes.Builder().build());
    }

    @Override
    @Transactional(readOnly = true)
    public OikeustulkkiVirkailijaViewDto getOikeustulkkiVirkailija(long id) {
        Oikeustulkki oikeustulkki = found(oikeustulkkiRepository.findEiPoistettuById(id));
        OikeustulkkiVirkailijaViewDto dto = produceViewDto(oikeustulkki);
        dto.setAiemmat(oikeustulkkiRepository.listAiemmatEiPoistetutById(id).stream().map(this::produceViewDto).collect(toList()));
        dto.setUusinId(oikeustulkkiRepository.getUusinUuudempiEiPoistettuById(oikeustulkki.getId()));
        audit.log(AuditUtil.getUser(), OIKEUSTULKKI_READ, new Target.Builder()
                .setField("henkiloOid", oikeustulkki.getTulkki().getHenkiloOid())
                .setField("oikeustulkkiId", String.valueOf(oikeustulkki.getId()))
                .build(), new Changes.Builder().build());
        return dto;
    }

    private OikeustulkkiVirkailijaViewDto produceViewDto(Oikeustulkki oikeustulkki) {
        HenkiloRestDto henkilo = found(oikeustulkkiCacheService.findHenkiloByOid(oikeustulkki.getTulkki().getHenkiloOid()));
        OikeustulkkiVirkailijaViewDto viewDto = convert(oikeustulkki, henkilo, new OikeustulkkiVirkailijaViewDto());
        viewDto.setId(oikeustulkki.getId());
        viewDto.setYksiloityVTJ(henkilo.isYksiloityVTJ());
        viewDto.setAidinkieli(ofNullable(henkilo.getAidinkieli()).map(KielisyysDto::getKieliKoodi).orElse(null));
        Map<String,HenkiloRestDto> fetched = new HashMap<>();
        Function<String,HenkiloRestDto> getHenkilos = or(fetched::get, oid -> {
            try {
                HenkiloRestDto h = oppijanumerorekisteriServiceUserClient.findByOid(oid);
                fetched.put(oid, h);
                return h;
            } catch(Exception e) {
                return null; // not found, e.g. not a valid OID, removed etc.
            }
        });
        viewDto.getMuokkaushistoria().addAll(oikeustulkki.getMuokkaukset()
                .stream().sorted(comparing(OikeustulkkiMuokkaus::getMuokattu))
                .map(m -> {
                    HenkiloRestDto muokkaaja = getHenkilos.apply(m.getMuokkaaja());
                    return new OikeustulkkiMuokkausHistoriaDto(
                            m.getMuokattu(),
                            m.getMuokkaaja(),
                            m.getMuokkausviesti(),
                            muokkaaja !=null ? muokkaaja.getEtunimet() + " " + muokkaaja.getSukunimi() : m.getMuokkaaja()
                    );
                }).collect(toList()));
        return viewDto;
    }

    private Tulkki createTulkki(OikeustulkkiCreateDto dto) {
        HenkiloCreateDto henkilo = new HenkiloCreateDto();
        henkilo.setHetu(dto.getHetu());
        henkilo.setEtunimet(dto.getEtunimet());
        henkilo.setSukunimi(dto.getSukunimi());
        henkilo.setKutsumanimi(dto.getKutsumanimi());
        logger.info("Calling createHenkilo");
        String oid = oppijanumerorekisteriServiceUserClient.createHenkilo(henkilo);
        logger.info("Received oid={} resposnse from authenticationService's createHenkilo", oid);
        return new Tulkki(oid);
    }

    private void updateHenkilo(String henkiloOid, OikeustulkkiBaseDto dto) throws ValidationException {
        logger.info("Updating henkilo details, fetching current ones by oid={}", henkiloOid);
        HenkiloRestDto henkilo = retrying(() -> oppijanumerorekisteriServiceUserClient.findByOid(henkiloOid), 2).get()
                .orFail(ex -> new ValidationException("Finding existing henkilo by OID failed.",
                        "henkilpalvelu.search.by.oid.failed"));
        if (!henkilo.isYksiloityVTJ()) {
            henkilo.setHetu(dto.getHetu());
            henkilo.setEtunimet(dto.getEtunimet());
            henkilo.setSukunimi(dto.getSukunimi());
        }
        henkilo.setKutsumanimi(dto.getKutsumanimi());
        YhteystiedotRyhmaDto ryhma = findOrAddYhteystiedot(henkilo.getYhteystiedotRyhma(), OIKEUSTULKKIREKISTERI_TYYPPI, OIKEUSTULKKIREKISTERI_ALKUPERA);
        updateYhteystieto(ryhma, YHTEYSTIETO_KATUOSOITE, dto.getOsoite().getKatuosoite());
        updateYhteystieto(ryhma, YHTEYSTIETO_KUNTA, dto.getOsoite().getPostitoimipaikka());
        updateYhteystieto(ryhma, YHTEYSTIETO_KAUPUNKI, dto.getOsoite().getPostitoimipaikka());
        updateYhteystieto(ryhma, YHTEYSTIETO_POSTINUMERO, dto.getOsoite().getPostinumero());
        updateYhteystieto(ryhma, YHTEYSTIETO_SAHKOPOSTI, dto.getEmail());
        updateYhteystieto(ryhma, YHTEYSTIETO_MATKAPUHELINNUMERO, dto.getPuhelinnumero());
        updateYhteystieto(ryhma, YHTEYSTIETO_PUHELINNUMERO, dto.getPuhelinnumero());
        logger.info("Updating henkilo details, oid={}", henkiloOid);
        retrying(() -> oppijanumerorekisteriServiceUserClient.updateHenkilo(henkilo), 2).get()
                .orFail(ex -> new ValidationException("Updating henkilö failed.",
                        "henkilpalvelu.updating.henkilo.failed"));
        logger.info("Updated henkilo details, oid={}", henkiloOid);
    }

    private YhteystiedotRyhmaDto findOrAddYhteystiedot(List<YhteystiedotRyhmaDto> ryhmat, String kuvaus, String alkupera) {
        return ryhmat.stream()
                .filter(t -> kuvaus.equals(t.getRyhmaKuvaus()))
                .filter(t -> alkupera.equals(t.getRyhmaAlkuperaTieto()))
                .findFirst()
                .orElseGet(() -> {
                    YhteystiedotRyhmaDto ryhma = new YhteystiedotRyhmaDto(kuvaus, alkupera);
                    ryhmat.add(ryhma);
                    return ryhma;
                });
    }

    private void updateYhteystieto(YhteystiedotRyhmaDto ryhma, YhteystietoTyyppi tyyppi, String arvo) {
        findOrAddYhteystieto(ryhma.getYhteystieto(), tyyppi).setYhteystietoArvo(arvo);
    }

    private YhteystiedotDto findOrAddYhteystieto(Set<YhteystiedotDto> yhteystiedot, YhteystietoTyyppi tyyppi) {
        return yhteystiedot.stream()
                .filter(t -> tyyppi.equals(t.getYhteystietoTyyppi()))
                .findFirst()
                .orElseGet(() -> {
                    YhteystiedotDto yhteystieto = new YhteystiedotDto(tyyppi);
                    yhteystiedot.add(yhteystieto);
                    return yhteystieto;
                });
    }

    private Oikeustulkki convert(OikeustulkkiBaseDto dto, Oikeustulkki oikeustulkki)  {
        oikeustulkki.setTutkintoTyyppi(dto.getTutkintoTyyppi());
        oikeustulkki.setLisatiedot(dto.getLisatiedot());
        oikeustulkki.setJulkaisulupaPuhelinnumero(dto.isJulkaisulupaPuhelinnumero());
        oikeustulkki.setJulkaisulupaMuuYhteystieto(dto.isJulkaisulupaMuuYhteystieto());
        oikeustulkki.setJulkaisulupaEmail(dto.isJulkaisulupaEmail());
        oikeustulkki.setJulkaisulupa(dto.isJulkaisulupa());
        oikeustulkki.setMuuYhteystieto(dto.getMuuYhteystieto()); // missing from henkilöpalvelu, thus saved here
        oikeustulkki.getSijainnit().clear();
        oikeustulkki.getKielet().clear();
        customFlushRepository.flush();
        if (dto.isKokoSuomi()) {
            oikeustulkki.getSijainnit().add(new Sijainti(oikeustulkki, KOKO_SUOMI));
        } else {
            oikeustulkki.getSijainnit().addAll(dto.getMaakunnat().stream()
                    .map(mk -> new Sijainti(oikeustulkki, MAAKUNTA, mk)).collect(toList()));
        }
        oikeustulkki.getKielet().addAll(dto.getKieliParit().stream()
            .map(kp ->
                new Kielipari(
                    oikeustulkki,
                    new Kieli(kp.getKielesta()),
                    new Kieli(kp.getKieleen()),
                    kp.getVoimassaoloAlkaa(),
                    kp.getVoimassaoloPaattyy()
                )
            ).collect(toList())
        );

        return oikeustulkki;
    }

    private<T extends OikeustulkkiBaseDto> T convert(Oikeustulkki from, HenkiloRestDto henkilo, T to) {
        to.setTutkintoTyyppi(from.getTutkintoTyyppi());
        to.setLisatiedot(from.getLisatiedot());
        to.setEtunimet(henkilo.getEtunimet());
        to.setSukunimi(henkilo.getSukunimi());
        to.setHetu(henkilo.getHetu());
        to.setJulkaisulupa(from.isJulkaisulupa());
        to.setJulkaisulupaPuhelinnumero(from.isJulkaisulupaPuhelinnumero());
        to.setKutsumanimi(henkilo.getKutsumanimi());
        to.setJulkaisulupaEmail(from.isJulkaisulupaEmail());
        to.setJulkaisulupaMuuYhteystieto(from.isJulkaisulupaMuuYhteystieto());
        // sähköposti ja puhelinnumero halutaan näyttää ensisijaisesti oikeustulkkirekisterin yhteystiedoista
        to.setEmail(findOikeustulkkiYhteystietoArvo(henkilo, YHTEYSTIETO_SAHKOPOSTI).orElse(null));
        to.setPuhelinnumero(findOikeustulkkiYhteystietoArvo(henkilo, YHTEYSTIETO_MATKAPUHELINNUMERO)
                .orElseGet(() -> findOikeustulkkiYhteystietoArvo(henkilo, YHTEYSTIETO_PUHELINNUMERO).orElse(null)));
        to.setMuuYhteystieto(from.getMuuYhteystieto());
        to.setKokoSuomi(isKokoSuomi(from.getSijainnit().stream()));
        to.setMaakunnat(maakuntaKoodis(from.getSijainnit().stream()));
        OsoiteEditDto osoite = new OsoiteEditDto();
        // osoitetiedot halutaan näyttää ensisijaisesti vtj:n yhteystiedoista
        osoite.setKatuosoite(findVtjYhteystietoArvo(henkilo, YHTEYSTIETO_KATUOSOITE).orElse(null));
        osoite.setPostinumero(findVtjYhteystietoArvo(henkilo, YHTEYSTIETO_POSTINUMERO).orElse(null));
        osoite.setPostitoimipaikka(findVtjYhteystietoArvo(henkilo, YHTEYSTIETO_KUNTA).orElse(null));
        to.setOsoite(osoite);
        to.setKieliParit(convert(from.getKielet().stream()));

        return to;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<OikeustulkkiVirkailijaListDto> haeVirkailija(OikeustulkkiVirkailijaHakuDto hakuDto) {
        List<OikeustulkkiVirkailijaListDto> results = doHaku(new Haku<>(hakuDto, hakuDto.getTermi(), spec(hakuDto), true),
                this::combineHenkiloVirkailija);
        audit.log(AuditUtil.getUser(), OIKEUSTULKKI_READ, new Target.Builder()
                .setField("henkiloOids", results.stream().map(OikeustulkkiVirkailijaListDto::getHenkiloOid).collect(joining(",")))
                .build(), new Changes.Builder().build());
        return results;
    }

    private Function<Oikeustulkki, OikeustulkkiVirkailijaListDto> combineHenkiloVirkailija(Function<String, HenkiloRestDto> h) {
        return ot -> {
            HenkiloRestDto henkilo = h.apply(ot.getTulkki().getHenkiloOid());
            OikeustulkkiVirkailijaListDto dto = new OikeustulkkiVirkailijaListDto();
            dto.setId(ot.getId());
            dto.setHetu(henkilo.getHetu());
            dto.setHenkiloOid(henkilo.getOidHenkilo());
            dto.setEtunimi(henkilo.getEtunimet());
            dto.setSukunimi(henkilo.getSukunimi());
            dto.setKieliParit(convert(ot.getKielet().stream()));
            dto.setKokoSuomi(isKokoSuomi(ot.getSijainnit().stream()));
            dto.setMaakunnat(maakuntaKoodis(ot.getSijainnit().stream()));
            return dto;
        };
    }

    @Override
    @Transactional(readOnly = true)
    public List<OikeustulkkiPublicListDto> haeJulkinen(OikeustulkkiPublicHakuDto hakuDto) {
        return doHaku(new Haku<>(hakuDto, hakuDto.getTermi(), spec(hakuDto), false),
                this::combineHenkilo);
    }

    @Override
    @Transactional(readOnly = true)
    public OikeustulkkiPublicViewDto getJulkinen(long id) {
        Oikeustulkki oikeustulkki = found(oikeustulkkiRepository.findEiPoistettuJulkinenById(id));
        HenkiloRestDto henkilo = found(oppijanumerorekisteriServiceUserClient.findByOid(oikeustulkki.getTulkki().getHenkiloOid()));
        OikeustulkkiPublicViewDto viewDto = new OikeustulkkiPublicViewDto();
        viewDto.setId(oikeustulkki.getId());
        viewDto.setEtunimet(henkilo.getEtunimet());
        viewDto.setSukunimi(henkilo.getSukunimi());
        viewDto.setKieliParit(convert(oikeustulkki.getKielet().stream().filter(kielipariVoimassa(LocalDate.now()))));
        viewDto.setKokoSuomi(isKokoSuomi(oikeustulkki.getSijainnit().stream()));
        viewDto.setMaakuntaKoodis(maakuntaKoodis(oikeustulkki.getSijainnit().stream()));
        setJulkisetYhteystiedot(oikeustulkki, henkilo, viewDto);
        return viewDto;
    }

    private void setJulkisetYhteystiedot(Oikeustulkki oikeustulkki, HenkiloRestDto henkilo, JulkisetYhteystiedot viewDto) {
        if (oikeustulkki.isJulkaisulupaEmail()) {
            viewDto.setEmail(findOikeustulkkiYhteystietoArvo(henkilo, YHTEYSTIETO_SAHKOPOSTI).orElse(null));
        }
        if (oikeustulkki.isJulkaisulupaPuhelinnumero()) {
            viewDto.setPuhelinnumero(findOikeustulkkiYhteystietoArvo(henkilo, YHTEYSTIETO_MATKAPUHELINNUMERO)
                    .orElseGet(() -> findOikeustulkkiYhteystietoArvo(henkilo, YHTEYSTIETO_PUHELINNUMERO).orElse(null)));
        }
        if (oikeustulkki.isJulkaisulupaMuuYhteystieto()) {
            viewDto.setMuuYhteystieto(oikeustulkki.getMuuYhteystieto());
        }
    }

    private Function<Oikeustulkki, OikeustulkkiPublicListDto> combineHenkilo(Function<String, HenkiloRestDto> h) {
        return ot -> {
            HenkiloRestDto henkilo = h.apply(ot.getTulkki().getHenkiloOid());
            OikeustulkkiPublicListDto dto = new OikeustulkkiPublicListDto();
            dto.setId(ot.getId());
            dto.setEtunimet(henkilo.getEtunimet());
            dto.setSukunimi(henkilo.getSukunimi());
            dto.setKieliParit(convert(ot.getKielet().stream().filter(kielipariVoimassa(LocalDate.now()))));
            dto.setKokoSuomi(isKokoSuomi(ot.getSijainnit().stream()));
            dto.setMaakunnat(maakuntaKoodis(ot.getSijainnit().stream()));
            return dto;
        };
    }

    private boolean isKokoSuomi(Stream<Sijainti> sijainnit) {
        return sijainnit.anyMatch(s -> s.getTyyppi() == KOKO_SUOMI);
    }

    private List<String> maakuntaKoodis(Stream<Sijainti> sijainnit) {
        return sijainnit.filter(s -> s.getTyyppi() == MAAKUNTA).map(Sijainti::getKoodi)
                .sorted().collect(toList());
    }

    private List<KieliPariDto> convert(Stream<Kielipari> from) {
        return from.map(kp -> new KieliPariDto(kp.getKielesta().getKoodi(), kp.getKieleen().getKoodi(), kp.getVoimassaoloAlkaa(), kp.getVoimassaoloPaattyy()))
                .sorted(comparing(KieliPariDto::getKielesta).thenComparing(KieliPariDto::getKieleen))
                .collect(toList());
    }

    private <DtoType,HakuType extends OikeustulkkiHakuehto> List<DtoType> doHaku(Haku<HakuType> haku,
                             Function<Function<String,HenkiloRestDto>, Function<Oikeustulkki, DtoType>> combiner) {
        HakuType hakuDto = haku.getHaku();
        Integer count = ofNullable(hakuDto.getCount()).orElse(DEFAULT_COUNT),
                page = ofNullable(hakuDto.getPage()).orElse(1),
                index = (page-1)*count;
        HenkiloTermPredicate predicate = new HenkiloTermPredicate(haku.getTerm());
        if (haku.isAllowHetu()) {
            predicate = predicate.matchingHetu();
        }
        List<HenkiloRestDto> henkiloResults = new ArrayList<>(oikeustulkkiCacheService.findHenkilos(predicate));
        Map<String,HenkiloRestDto> henkilosByOid = henkiloResults.stream().collect(toMap(HenkiloRestDto::getOidHenkilo, h -> h));
        Map<String,List<Oikeustulkki>> oikeustulkkis = oikeustulkkiRepository.findAll(where(haku.getSpecification())
                    .and(henkiloOidIn(henkilosByOid.keySet()))).stream()
                .collect(groupingBy(ot -> ot.getTulkki().getHenkiloOid(), mapping(ot->ot, toList())));
        
        return henkiloResults.stream().flatMap(ot -> ofNullable(oikeustulkkis.get(ot.getOidHenkilo()))
                .map(List::stream).orElseGet(Stream::empty)).map(combiner.apply(henkilosByOid::get))
                //.skip(index).limit(count) //TODO:support in UI?
                .collect(toList());
    }

    private List<HenkiloRestDto> listHenkilosByHetu(OppijanumerorekisteriApi api, String hetu) {
        return api.list(hetu, false, false).getResults();
    }
    
    @Getter @AllArgsConstructor
    private static class Haku<HakuType extends OikeustulkkiHakuehto> {
        private final HakuType haku;
        private final String term;
        private final Specification<Oikeustulkki> specification;
        private final boolean allowHetu;
    }

    private static Predicate<Kielipari> kielipariVoimassa(LocalDate at) {
        return kielipari -> (kielipari.getVoimassaoloAlkaa().isBefore(at) || kielipari.getVoimassaoloPaattyy().isEqual(at))
                && (kielipari.getVoimassaoloPaattyy().isAfter(at) || kielipari.getVoimassaoloPaattyy().isEqual(at));
    }

}
