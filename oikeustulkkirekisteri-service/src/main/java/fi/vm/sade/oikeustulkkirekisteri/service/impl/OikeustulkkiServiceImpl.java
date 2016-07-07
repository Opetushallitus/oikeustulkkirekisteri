package fi.vm.sade.oikeustulkkirekisteri.service.impl;

import fi.vm.sade.authentication.model.HenkiloTyyppi;
import fi.vm.sade.authentication.model.YhteystietoTyyppi;
import fi.vm.sade.generic.common.ValidationException;
import fi.vm.sade.oikeustulkkirekisteri.domain.*;
import fi.vm.sade.oikeustulkkirekisteri.domain.embeddable.Kieli;
import fi.vm.sade.oikeustulkkirekisteri.external.api.HenkiloApi;
import fi.vm.sade.oikeustulkkirekisteri.external.api.dto.*;
import fi.vm.sade.oikeustulkkirekisteri.repository.OikeustulkkiRepository;
import fi.vm.sade.oikeustulkkirekisteri.repository.TullkiRepository;
import fi.vm.sade.oikeustulkkirekisteri.repository.custom.CustomFlushRepository;
import fi.vm.sade.oikeustulkkirekisteri.service.OikeustulkkiCacheService;
import fi.vm.sade.oikeustulkkirekisteri.service.OikeustulkkiService;
import fi.vm.sade.oikeustulkkirekisteri.service.dto.*;
import fi.vm.sade.oikeustulkkirekisteri.util.AbstractService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.joda.time.Period;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import static fi.vm.sade.auditlog.oikeustulkkirekisteri.OikeustulkkiOperation.*;
import static fi.vm.sade.authentication.model.YhteystietoTyyppi.*;
import static fi.vm.sade.oikeustulkkirekisteri.domain.Sijainti.Tyyppi.KOKO_SUOMI;
import static fi.vm.sade.oikeustulkkirekisteri.domain.Sijainti.Tyyppi.MAAKUNTA;
import static fi.vm.sade.oikeustulkkirekisteri.external.api.HenkiloYhteystietoUtil.*;
import static fi.vm.sade.oikeustulkkirekisteri.service.impl.OikeustulkkiHakuSpecificationBuilder.*;
import static fi.vm.sade.oikeustulkkirekisteri.util.FoundUtil.found;
import static fi.vm.sade.oikeustulkkirekisteri.util.FunctionalUtil.or;
import static java.util.Collections.singletonList;
import static java.util.Comparator.comparing;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.*;
import static org.joda.time.LocalDate.now;
import static org.springframework.data.jpa.domain.Specifications.where;

/**
 * User: tommiratamaa
 * Date: 30.5.2016
 * Time: 14.11
 */
@Service
public class OikeustulkkiServiceImpl extends AbstractService implements OikeustulkkiService {
    private static final Integer DEFAULT_COUNT = 20;

    @Autowired
    private OikeustulkkiRepository oikeustulkkiRepository;
    
    @Autowired
    private TullkiRepository tullkiRepository;
    
    @Resource
    private HenkiloApi henkiloResourceServiceUserClient;

    @Autowired
    private OikeustulkkiCacheService oikeustulkkiCacheService;

    @Autowired
    private CustomFlushRepository customFlushRepository;

    @Value("${oikeustulkki.tehtavanimike:Oikeustulkki}")
    private String oikeustulkkiTehtavanimike;
    
    @Value("${oikeustulkki.voimassaolo:P5Y}")
    private String oikeustulkkiVoimassaolo;
    
    private static Specifications<Oikeustulkki> spec(OikeustulkkiVirkailijaHakuDto dto) {
        Specifications<Oikeustulkki> where =  eiPoistettu.and(voimassaoloRajausAlku(dto.getVoimassaAlku()))
                .and(voimassaoloRajausLoppu(dto.getVoimassaLoppu()))
                .and(tutkintoTyyppi(dto.getTutkintoTyyppi()))
                .and(toimiiMaakunnissa(singletonList(dto.getMaakuntaKoodi())))
                .and(kieliparit(dto.getKieliparit()))
                .and(dto.getVoimassaNyt() == null ? null : dto.getVoimassaNyt() ? voimassa(now()) : Specifications.not(voimassa(now())));
        return where(latest(where)).and(where);
    }
    
    private static Specifications<Oikeustulkki> spec(OikeustulkkiPublicHakuDto dto) {
        Specifications<Oikeustulkki> where = eiPoistettu.and(voimassaoloRajausLoppu(now()))
                .and(julkaisulupa()).and(kieliparit(dto.getKieliparit()))
                .and(toimiiMaakunnissa(singletonList(dto.getMaakuntaKoodi())));
        return where(latest(where)).and(where);
    }
    
    @Override
    @Transactional
    public long createOikeustulkki(OikeustulkkiCreateDto dto) {
        Optional<HenkiloRestDto> existingHenkilo = listHenkilosByTermi(henkiloResourceServiceUserClient,
                dto.getHetu()).stream().findFirst();
        Oikeustulkki oikeustulkki = new Oikeustulkki();
        if (existingHenkilo.isPresent()) {
            oikeustulkki.setTulkki(tullkiRepository.findByHenkiloOid(existingHenkilo.get().getOidHenkilo()));
            if (oikeustulkki.getTulkki() == null) {
                oikeustulkki.setTulkki(new Tulkki(existingHenkilo.get().getOidHenkilo()));
            }
        } else if (oikeustulkki.getTulkki() == null) {
            oikeustulkki.setTulkki(createTulkki(dto));
        }
        convert(dto, oikeustulkki);
        oikeustulkki.setPaattyy(oikeustulkki.getAlkaa().plus(Period.parse(oikeustulkkiVoimassaolo)));
        updateHenkilo(oikeustulkki.getTulkki().getHenkiloOid(), dto);
        oikeustulkkiRepository.save(oikeustulkki);
        auditLog.log(builder(OIKEUSTULKKI_CREATE)
                .henkiloOid(oikeustulkki.getTulkki().getHenkiloOid())
                .oikeustulkkiId(oikeustulkki.getId())
                .build());
        oikeustulkkiCacheService.notifyHenkiloUpdated(oikeustulkki.getTulkki().getHenkiloOid());
        return oikeustulkki.getId();
    }
    
    @Override
    @Transactional
    public void editOikeustulkki(OikeustulkkiEditDto dto) throws ValidationException {
        Oikeustulkki oikeustulkki = found(oikeustulkkiRepository.findEiPoistettuById(dto.getId()));
        if (dto.getPaattyy().isBefore(dto.getAlkaa())) {
            throw new ValidationException("Validation period end before start", "oikeustulkki.paattyy.before.alkaa");
        }
        convert(dto, oikeustulkki);
        oikeustulkki.setPaattyy(dto.getPaattyy());
        updateHenkilo(oikeustulkki.getTulkki().getHenkiloOid(), dto);
        OikeustulkkiMuokkaus muokkaus = new OikeustulkkiMuokkaus();
        muokkaus.setMuokkaaja(SecurityContextHolder.getContext().getAuthentication().getName());
        muokkaus.setOikeustulkki(oikeustulkki);
        muokkaus.setMuokkausviesti(dto.getMuokkausviesti());
        oikeustulkki.getMuokkaukset().add(muokkaus);
        auditLog.log(builder(OIKEUSTULKKI_UPDATE)
                .henkiloOid(oikeustulkki.getTulkki().getHenkiloOid())
                .oikeustulkkiId(oikeustulkki.getId())
                .build());
        oikeustulkkiCacheService.notifyHenkiloUpdated(oikeustulkki.getTulkki().getHenkiloOid());
    }

    @Override
    @Transactional
    public void deleteOikeustulkki(long id) {
        Oikeustulkki oikeustulkki = found(oikeustulkkiRepository.findEiPoistettuById(id));
        oikeustulkki.markPoistettu(SecurityContextHolder.getContext().getAuthentication().getName());
        auditLog.log(builder(OIKEUSTULKKI_DELETE)
                .henkiloOid(oikeustulkki.getTulkki().getHenkiloOid())
                .oikeustulkkiId(oikeustulkki.getId())
                .build());
    }

    @Override
    @Transactional(readOnly = true)
    public OikeustulkkiVirkailijaViewDto getOikeustulkkiVirkailija(long id) {
        Oikeustulkki oikeustulkki = found(oikeustulkkiRepository.findEiPoistettuById(id));
        OikeustulkkiVirkailijaViewDto dto = produceViewDto(oikeustulkki);
        dto.setAiemmat(oikeustulkkiRepository.listAiemmatEiPoistetutById(id).stream().map(this::produceViewDto).collect(toList()));
        dto.setUusinId(oikeustulkkiRepository.getUusinUuudempiEiPoistettuById(oikeustulkki.getId()));
        auditLog.log(builder(OIKEUSTULKKI_READ)
                .henkiloOid(oikeustulkki.getTulkki().getHenkiloOid())
                .oikeustulkkiId(oikeustulkki.getId())
                .build());
        return dto;
    }

    private OikeustulkkiVirkailijaViewDto produceViewDto(Oikeustulkki oikeustulkki) {
        HenkiloRestDto henkilo = found(oikeustulkkiCacheService.findHenkiloByOid(oikeustulkki.getTulkki().getHenkiloOid()));
        OikeustulkkiVirkailijaViewDto viewDto = convert(oikeustulkki, henkilo, new OikeustulkkiVirkailijaViewDto());
        viewDto.setPaattyy(oikeustulkki.getPaattyy());
        viewDto.setId(oikeustulkki.getId());
        viewDto.setAidinkieli(ofNullable(henkilo.getAidinkieli()).map(KielisyysDto::getKieliKoodi).orElse(null));
        Map<String,HenkiloRestDto> fetched = new HashMap<>();
        Function<String,HenkiloRestDto> getHenkilos = or(fetched::get, oid -> {
            try {
                HenkiloRestDto h = henkiloResourceServiceUserClient.findByOid(oid);
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
        henkilo.setHenkiloTyyppi(HenkiloTyyppi.OPPIJA); // although deprecated is required by henkilöpalvelu impl, 
        // virkalija so that can be serached and edited through henkilopalvelu
        return new Tulkki(henkiloResourceServiceUserClient.createHenkilo(henkilo));
    }

    private void updateHenkilo(String henkiloOid, OikeustulkkiBaseDto dto) {
        HenkiloRestDto henkilo = henkiloResourceServiceUserClient.findByOid(henkiloOid);
        henkilo.setEtunimet(dto.getEtunimet());
        henkilo.setSukunimi(dto.getSukunimi());
        henkilo.setKutsumanimi(dto.getKutsumanimi());
        updateYhteystieto(henkilo, YHTEYSTIETO_KATUOSOITE, dto.getOsoite().getKatuosoite());
        updateYhteystieto(henkilo, YHTEYSTIETO_KUNTA, dto.getOsoite().getPostitoimipaikka());
        updateYhteystieto(henkilo, YHTEYSTIETO_KAUPUNKI, dto.getOsoite().getPostitoimipaikka());
        updateYhteystieto(henkilo, YHTEYSTIETO_POSTINUMERO, dto.getOsoite().getPostinumero());
        updateYhteystieto(henkilo, YHTEYSTIETO_SAHKOPOSTI, dto.getEmail());
        updateYhteystieto(henkilo, YHTEYSTIETO_MATKAPUHELINNUMERO, dto.getPuhelinnumero());
        updateYhteystieto(henkilo, YHTEYSTIETO_PUHELINNUMERO, dto.getPuhelinnumero());
        henkiloResourceServiceUserClient.updateHenkilo(henkiloOid, henkilo);
    }

    private void updateYhteystieto(HenkiloRestDto henkilo, YhteystietoTyyppi tyyppi, String arvo) {
        Optional<YhteystiedotDto> yhteystiedot = findWritableTyoYhteystieto(henkilo, tyyppi);
        if (yhteystiedot.isPresent()) {
            yhteystiedot.get().setYhteystietoArvo(arvo);
        } else {
            if (findReadableTyoYhteystietoArvo(henkilo, tyyppi).isPresent()) {
                // not writable, skip
                return;
            }
            Optional<YhteystiedotRyhmaDto> ryhma = henkilo.getYhteystiedotRyhma().stream().filter(YT_RYHMA_FILTER_SET).findFirst();
            if (!ryhma.isPresent()) {
                YhteystiedotRyhmaDto r = new YhteystiedotRyhmaDto();
                r.setRyhmaKuvaus(TYOOSOITE_TYYPPI);
                ryhma = of(r);
                henkilo.getYhteystiedotRyhma().add(r);
            }
            YhteystiedotDto yt = new YhteystiedotDto();
            yt.setYhteystietoTyyppi(tyyppi);
            yt.setYhteystietoArvo(arvo);
            ryhma.get().getYhteystiedot().add(yt);
        }
    }

    private Oikeustulkki convert(OikeustulkkiBaseDto dto, Oikeustulkki oikeustulkki)  {
        oikeustulkki.setAlkaa(dto.getAlkaa());
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
            .map(kp -> new Kielipari(oikeustulkki, new Kieli(kp.getKielesta()), new Kieli(kp.getKieleen()))).collect(toList()));
        return oikeustulkki;
    }

    private<T extends OikeustulkkiBaseDto> T convert(Oikeustulkki from, HenkiloRestDto henkilo, T to) {
        to.setAlkaa(from.getAlkaa());
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
        to.setEmail(findReadableTyoYhteystietoArvo(henkilo, YHTEYSTIETO_SAHKOPOSTI).orElse(null));
        to.setPuhelinnumero(findReadableTyoYhteystietoArvo(henkilo, YHTEYSTIETO_MATKAPUHELINNUMERO)
                .orElseGet(() -> findReadableTyoYhteystietoArvo(henkilo, YHTEYSTIETO_PUHELINNUMERO).orElse(null)));
        to.setMuuYhteystieto(from.getMuuYhteystieto());
        to.setKokoSuomi(isKokoSuomi(from.getSijainnit().stream()));
        to.setMaakunnat(maakuntaKoodis(from.getSijainnit().stream()));
        OsoiteEditDto osoite = new OsoiteEditDto();
        osoite.setKatuosoite(findReadableTyoYhteystietoArvo(henkilo, YHTEYSTIETO_KATUOSOITE).orElse(null));
        osoite.setPostinumero(findReadableTyoYhteystietoArvo(henkilo, YHTEYSTIETO_POSTINUMERO).orElse(null));
        osoite.setPostitoimipaikka(findReadableTyoYhteystietoArvo(henkilo, YHTEYSTIETO_KUNTA).orElse(null));
        to.setOsoite(osoite);
        to.setKieliParit(convert(from.getKielet().stream()));
        return to;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<OikeustulkkiVirkailijaListDto> haeVirkailija(OikeustulkkiVirkailijaHakuDto hakuDto) {
        List<OikeustulkkiVirkailijaListDto> results = doHaku(new Haku<>(hakuDto, hakuDto.getTermi(), spec(hakuDto)),
                this::combineHenkiloVirkailija);
        auditLog.log(builder(OIKEUSTULKKI_READ)
            .henkiloOidList(results.stream().map(OikeustulkkiVirkailijaListDto::getHenkiloOid).collect(toList()))
            .build());
        return results;
    }

    private Function<Oikeustulkki, OikeustulkkiVirkailijaListDto> combineHenkiloVirkailija(Function<String, HenkiloRestDto> h) {
        return ot -> {
            HenkiloRestDto henkilo = h.apply(ot.getTulkki().getHenkiloOid());
            OikeustulkkiVirkailijaListDto dto = new OikeustulkkiVirkailijaListDto();
            dto.setId(ot.getId());
            dto.setAlkaa(ot.getAlkaa());
            dto.setHetu(henkilo.getHetu());
            dto.setPaattyy(ot.getPaattyy());
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
        return doHaku(new Haku<>(hakuDto, hakuDto.getTermi(), spec(hakuDto)),
                this::combineHenkilo);
    }

    @Override
    @Transactional(readOnly = true)
    public OikeustulkkiPublicViewDto getJulkinen(long id) {
        Oikeustulkki oikeustulkki = found(oikeustulkkiRepository.findEiPoistettuJulkinenById(id));
        HenkiloRestDto henkilo = found(henkiloResourceServiceUserClient.findByOid(oikeustulkki.getTulkki().getHenkiloOid()));
        OikeustulkkiPublicViewDto viewDto = new OikeustulkkiPublicViewDto();
        viewDto.setId(oikeustulkki.getId());
        viewDto.setEtunimet(henkilo.getEtunimet());
        viewDto.setSukunimi(henkilo.getSukunimi());
        viewDto.setPaattyy(oikeustulkki.getPaattyy());
        viewDto.setKieliParit(convert(oikeustulkki.getKielet().stream()));
        viewDto.setKokoSuomi(isKokoSuomi(oikeustulkki.getSijainnit().stream()));
        viewDto.setMaakuntaKoodis(maakuntaKoodis(oikeustulkki.getSijainnit().stream()));
        setJulkisetYhteystiedot(oikeustulkki, henkilo, viewDto);
        return viewDto;
    }

    private void setJulkisetYhteystiedot(Oikeustulkki oikeustulkki, HenkiloRestDto henkilo, JulkisetYhteystiedot viewDto) {
        if (oikeustulkki.isJulkaisulupaEmail()) {
            viewDto.setEmail(findReadableTyoYhteystietoArvo(henkilo, YHTEYSTIETO_SAHKOPOSTI).orElse(null));
        }
        if (oikeustulkki.isJulkaisulupaPuhelinnumero()) {
            viewDto.setPuhelinnumero(findReadableTyoYhteystietoArvo(henkilo, YHTEYSTIETO_MATKAPUHELINNUMERO)
                    .orElseGet(() -> findReadableTyoYhteystietoArvo(henkilo, YHTEYSTIETO_PUHELINNUMERO).orElse(null)));
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
            dto.setPaattyy(ot.getPaattyy());
            dto.setEtunimet(henkilo.getEtunimet());
            dto.setSukunimi(henkilo.getSukunimi());
            dto.setKieliParit(convert(ot.getKielet().stream()));
            dto.setKokoSuomi(isKokoSuomi(ot.getSijainnit().stream()));
            dto.setMaakunnat(maakuntaKoodis(ot.getSijainnit().stream()));
            return dto;
        };
    }

    private boolean isKokoSuomi(Stream<Sijainti> sijainnit) {
        return sijainnit.filter(s -> s.getTyyppi() == KOKO_SUOMI).findAny().isPresent();
    }

    private List<String> maakuntaKoodis(Stream<Sijainti> sijainnit) {
        return sijainnit.filter(s -> s.getTyyppi() == MAAKUNTA).map(Sijainti::getKoodi)
                .sorted().collect(toList());
    }

    private List<KieliPariDto> convert(Stream<Kielipari> from) {
        return from.map(kp -> new KieliPariDto(kp.getKielesta().getKoodi(), kp.getKieleen().getKoodi()))
                .sorted(comparing(KieliPariDto::getKielesta).thenComparing(KieliPariDto::getKieleen))
                .collect(toList());
    }

    private <DtoType,HakuType extends OikeustulkkiHakuehto> List<DtoType> doHaku(Haku<HakuType> haku,
                             Function<Function<String,HenkiloRestDto>, Function<Oikeustulkki, DtoType>> combiner) {
        HakuType hakuDto = haku.getHaku();
        Integer count = ofNullable(hakuDto.getCount()).orElse(DEFAULT_COUNT),
                page = ofNullable(hakuDto.getPage()).orElse(1),
                index = (page-1)*count;
        List<HenkiloRestDto> henkiloResults = listHenkilosByTermi(haku.getTerm()).stream().collect(toList());
        Map<String,HenkiloRestDto> henkilosByOid = henkiloResults.stream().collect(toMap(HenkiloRestDto::getOidHenkilo, h -> h));
        Map<String,List<Oikeustulkki>> oikeustulkkis = oikeustulkkiRepository.findAll(where(haku.getSpecification())
                    .and(henkiloOidIn(henkilosByOid.keySet()))).stream()
                .sorted(comparing(Oikeustulkki::getAlkaa)).collect(groupingBy(ot -> ot.getTulkki().getHenkiloOid(), mapping(ot->ot, toList())));
        
        return henkiloResults.stream().flatMap(ot -> ofNullable(oikeustulkkis.get(ot.getOidHenkilo()))
                .map(List::stream).orElseGet(Stream::empty)).map(combiner.apply(henkilosByOid::get))
                //.skip(index).limit(count) //TODO:support in UI?
                .collect(toList());
    }

    private List<HenkiloRestDto> listHenkilosByTermi(String term) {
        return oikeustulkkiCacheService.findHenkilos(new HenkiloTermPredicate(term));
    }

    private List<HenkiloRestDto> listHenkilosByTermi(HenkiloApi api, String term) {
        return api.listHenkilos(term, null, 0, 0, null, null, null, null,
                false, true, false, false, null, false).getResults();
    }
    
    @Getter @AllArgsConstructor
    private static class Haku<HakuType extends OikeustulkkiHakuehto> {
        private final HakuType haku;
        private final String term;
        private final Specification<Oikeustulkki> specification;
    }
}
