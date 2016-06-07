package fi.vm.sade.oikeustulkkirekisteri.service.impl;

import fi.vm.sade.authentication.model.Henkilo;
import fi.vm.sade.authentication.model.Yhteystiedot;
import fi.vm.sade.authentication.model.YhteystiedotRyhma;
import fi.vm.sade.authentication.model.YhteystietoTyyppi;
import fi.vm.sade.generic.common.ValidationException;
import fi.vm.sade.oikeustulkkirekisteri.domain.*;
import fi.vm.sade.oikeustulkkirekisteri.domain.embeddable.Kieli;
import fi.vm.sade.oikeustulkkirekisteri.external.api.HenkiloApi;
import fi.vm.sade.oikeustulkkirekisteri.external.api.dto.HenkiloCreateDto;
import fi.vm.sade.oikeustulkkirekisteri.external.api.dto.OrganisaatioHenkiloDto;
import fi.vm.sade.oikeustulkkirekisteri.external.api.dto.PaginationObject;
import fi.vm.sade.oikeustulkkirekisteri.repository.OikeustulkkiRepository;
import fi.vm.sade.oikeustulkkirekisteri.repository.TullkiRepository;
import fi.vm.sade.oikeustulkkirekisteri.service.OikeustulkkiService;
import fi.vm.sade.oikeustulkkirekisteri.service.dto.*;
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
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static fi.vm.sade.authentication.model.YhteystietoTyyppi.*;
import static fi.vm.sade.oikeustulkkirekisteri.domain.Sijainti.Tyyppi.KOKO_SUOMI;
import static fi.vm.sade.oikeustulkkirekisteri.domain.Sijainti.Tyyppi.MAAKUNTA;
import static fi.vm.sade.oikeustulkkirekisteri.service.dto.OikeustulkkiHakuSpecBuilder.*;
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
public class OikeustulkkiServiceImpl implements OikeustulkkiService {
    private static final Integer DEFAULT_COUNT = 20;
    private static final String KOTIOSOITE_TYYPPI = "yhteystietotyyppi1";
    private static final String TYOOSOITE_TYYPPI = "yhteystietotyyppi2";
    private static final Predicate<YhteystiedotRyhma> YT_RYHMA_FILTER_READ = r -> !r.isRemoved() && !r.getRyhmaKuvaus().equals(KOTIOSOITE_TYYPPI);
    private static final Predicate<YhteystiedotRyhma> YT_RYHMA_FILTER_SET = YT_RYHMA_FILTER_READ.and(r -> !r.isReadOnly());
    
    @Autowired
    private OikeustulkkiRepository oikeustulkkiRepository;
    
    @Autowired
    private TullkiRepository tullkiRepository;
    
    @Resource
    private HenkiloApi henkiloResourceClient;
    
    @Resource
    private HenkiloApi henkiloResourceReadClient;
    
    @Value("${oikeustulkkirekisteri.organisaatio.oid}")
    private String oikeustulkkirekisteriOrganisaatioOid;
    
    @Value("${oikeustulkki.tehtavanimike:Oikeustulkki}")
    private String oikeustulkkiTehtavanimike;
    
    @Value("${oikeustulkki.voimassaolo:P5Y}")
    private String oikeustulkkiVoimassaolo;
    
    @Override
    @Transactional
    public long createOikeustulkki(OikeustulkkiCreateDto dto) {
        Optional<Henkilo> existingHenkilo = listHenkilosByTermi(henkiloResourceReadClient, 
                dto.getHetu(), 1, 0).getResults().stream().findFirst();
        Oikeustulkki oikeustulkki = new Oikeustulkki();
        if (existingHenkilo.isPresent()) {
            oikeustulkki.setTulkki(tullkiRepository.findByHenkiloOid(existingHenkilo.get().getOidHenkilo()));
        }
        if (oikeustulkki.getTulkki() == null) {
            oikeustulkki.setTulkki(createTulkki(dto));
        }
        convert(dto, oikeustulkki);
        oikeustulkki.setPaattyy(oikeustulkki.getAlkaa().plus(Period.parse(oikeustulkkiVoimassaolo)));
        updateHenkilo(oikeustulkki.getTulkki().getHenkiloOid(), dto);
        oikeustulkkiRepository.save(oikeustulkki);
        return oikeustulkki.getId();
    }
    
    @Override
    @Transactional
    public void editOikeustulkki(OikeustulkkiMuokkausDto dto) throws ValidationException {
        Oikeustulkki oikeustulkki = found(oikeustulkkiRepository.findEiPoistettuById(dto.getId()));
        if (dto.getPaattyy().isBefore(dto.getAlkaa())) {
            throw new ValidationException("Validation period end before start", "oikeustulkki.paattyy.before.alkaa");
        }
        convert(dto, oikeustulkki);
        oikeustulkki.setPaattyy(dto.getPaattyy());
        OikeustulkkiMuokkaus muokkaus = new OikeustulkkiMuokkaus();
        muokkaus.setMuokkaaja(SecurityContextHolder.getContext().getAuthentication().getName());
        muokkaus.setOikeustulkki(oikeustulkki);
        muokkaus.setMuokkausviesti(dto.getMuokkausviesti());
        oikeustulkki.getMuokkaukset().add(muokkaus);
    }
    
    @Override
    @Transactional
    public void deleteOikeustulkki(long id) {
        Oikeustulkki oikeustulkki = found(oikeustulkkiRepository.findEiPoistettuById(id));
        oikeustulkki.markPoistettu(SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @Override
    @Transactional(readOnly = true)
    public OikeustulkkiVirkailijaViewDto getOikeustulkkiVirkailija(long id) {
        Oikeustulkki oikeustulkki = found(oikeustulkkiRepository.findEiPoistettuById(id));
        Henkilo henkilo = found(henkiloResourceClient.findByOid(oikeustulkki.getTulkki().getHenkiloOid()));
        OikeustulkkiVirkailijaViewDto viewDto = convert(oikeustulkki, henkilo, new OikeustulkkiVirkailijaViewDto());
        viewDto.setPaattyy(oikeustulkki.getPaattyy());
        Map<String,Henkilo> fetched = new HashMap<>();
        Function<String,Henkilo> getHenkilos = or(fetched::get, oid -> {
            Henkilo h = henkiloResourceReadClient.findByOid(oid);
            fetched.put(oid, h);
            return h;
        });
        viewDto.getMuokkaushistoria().addAll(oikeustulkki.getMuokkaukset()
                .stream().sorted(comparing(OikeustulkkiMuokkaus::getMuokattu))
                .map(m -> {
                    Henkilo muokkaaja = getHenkilos.apply(m.getMuokkaaja());
                    return new OikeustulkkiMuokkausHistoriaDto(
                            m.getMuokattu(),
                            m.getMuokkaaja(),
                            m.getMuokkausviesti(),
                            muokkaaja.getEtunimet() + " " + muokkaaja.getSukunimi()
                    );
                }).collect(toList()));
        return viewDto;
    }

    private Tulkki createTulkki(OikeustulkkiCreateDto dto) {
        HenkiloCreateDto henkilo = new HenkiloCreateDto();
        henkilo.setHetu(dto.getHetu());
        henkilo.setEtunimet(dto.getEtunimet());
        henkilo.setSukunimi(dto.getSukunimi());
        List<OrganisaatioHenkiloDto> orgHenkilos = new ArrayList<>();
        OrganisaatioHenkiloDto orgHenkilo = new OrganisaatioHenkiloDto();
        orgHenkilo.setOrganisaatioOid(oikeustulkkirekisteriOrganisaatioOid);
        orgHenkilo.setTehtavanimike(oikeustulkkiTehtavanimike);
        orgHenkilos.add(orgHenkilo);
        henkilo.setOrganisaatioHenkilo(orgHenkilos);
        return new Tulkki(henkiloResourceClient.createHenkilo(henkilo));
    }

    private void updateHenkilo(String henkiloOid, OikeustulkkiCreateDto dto) {
        Henkilo henkilo = henkiloResourceReadClient.findByOid(henkiloOid);
        updateYhteystieto(henkilo, YHTEYSTIETO_KATUOSOITE, dto.getOsoite().getKatuosoite());
        updateYhteystieto(henkilo, YHTEYSTIETO_KUNTA, dto.getOsoite().getPostitoimipaikka());
        updateYhteystieto(henkilo, YHTEYSTIETO_POSTINUMERO, dto.getOsoite().getPostinumero());
        updateYhteystieto(henkilo, YHTEYSTIETO_SAHKOPOSTI, dto.getEmail());
        updateYhteystieto(henkilo, YHTEYSTIETO_MATKAPUHELINNUMERO, dto.getPuhelinnumero());
        henkiloResourceClient.updateHenkilo(henkiloOid, henkilo);
    }

    private void updateYhteystieto(Henkilo henkilo, YhteystietoTyyppi tyyppi, String arvo) {
        Optional<Yhteystiedot> yhteystiedot = henkilo.getYhteystiedot().stream().filter(YT_RYHMA_FILTER_SET)
                .flatMap(r -> r.getYhteystiedot().stream()).filter(yt -> yt.getYhteystietoTyyppi() == tyyppi)
                .findFirst();
        if (yhteystiedot.isPresent()) {
            yhteystiedot.get().setYhteystietoArvo(arvo);
        } else {
            if (findYhteystieto(henkilo, tyyppi).isPresent()) {
                // not writable, skip
                return;
            }
            Optional<YhteystiedotRyhma> ryhma = henkilo.getYhteystiedot().stream().filter(YT_RYHMA_FILTER_SET).findFirst();
            if (!ryhma.isPresent()) {
                YhteystiedotRyhma r = new YhteystiedotRyhma();
                r.setRyhmaKuvaus(TYOOSOITE_TYYPPI);
                ryhma = of(r);
            }
            Yhteystiedot yt = new Yhteystiedot();
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
        oikeustulkki.setMuuYhteystieto(dto.getMuuYhteystieto());
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

    private<T extends OikeustulkkiBaseDto> T convert(Oikeustulkki from, Henkilo henkilo, T to) {
        to.setAlkaa(from.getAlkaa());
        to.setTutkintoTyyppi(from.getTutkintoTyyppi());
        to.setLisatiedot(from.getLisatiedot());
        to.setJulkaisulupaPuhelinnumero(from.isJulkaisulupaPuhelinnumero());
        to.setJulkaisulupaEmail(from.isJulkaisulupaEmail());
        to.setJulkaisulupaMuuYhteystieto(from.isJulkaisulupaMuuYhteystieto());
        to.setEmail(findYhteystieto(henkilo, YHTEYSTIETO_SAHKOPOSTI).orElse(null));
        to.setPuhelinnumero(findYhteystieto(henkilo, YHTEYSTIETO_MATKAPUHELINNUMERO)
                .orElseGet(() -> findYhteystieto(henkilo, YHTEYSTIETO_PUHELINNUMERO).orElse(null)));
        to.setMuuYhteystieto(from.getMuuYhteystieto());
        to.setKokoSuomi(from.getSijainnit().stream().filter(s -> s.getTyyppi() == KOKO_SUOMI).findAny().isPresent());
        to.setMaakunnat(from.getSijainnit().stream().filter(s -> s.getTyyppi() == MAAKUNTA).map(Sijainti::getKoodi)
                .sorted().collect(toList()));
        OsoiteEditDto osoite = new OsoiteEditDto();
        osoite.setKatuosoite(findYhteystieto(henkilo, YHTEYSTIETO_KATUOSOITE).orElse(null));
        osoite.setPostinumero(findYhteystieto(henkilo, YHTEYSTIETO_POSTINUMERO).orElse(null));
        osoite.setPostitoimipaikka(findYhteystieto(henkilo, YHTEYSTIETO_KUNTA).orElse(null));
        to.setOsoite(osoite);
        return to;
    }

    private Optional<String> findYhteystieto(Henkilo henkilo, YhteystietoTyyppi tyyppi) {
        return henkilo.getYhteystiedot().stream().filter(YT_RYHMA_FILTER_READ).flatMap(yt -> yt.getYhteystiedot().stream())
            .filter(yt -> yt.getYhteystietoTyyppi() == tyyppi)
            .map(Yhteystiedot::getYhteystietoArvo).findFirst();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OikeustulkkiVirkailijaListDto> haeVirkailija(OikeustulkkiVirkailijaHakuDto hakuDto) {
        return doHaku(henkiloResourceClient,
                new Haku<>(hakuDto, hakuDto.getTermi(), spec(hakuDto)),
                this::combineHenkiloVirkailija);
    }
    
    private static Specifications<Oikeustulkki> spec(OikeustulkkiVirkailijaHakuDto dto) {
        Specifications<Oikeustulkki> where =  eiPoistettu.and(voimassaoloRajausAlku(dto.getVoimassaAlku()))
                .and(voimassaoloRajausLoppu(dto.getVoimassaLoppu()))
                .and(toimiiMaakunnissa(singletonList(dto.getMaakuntaKoodi())))
                .and(kieliparit(dto.getKieliparit()))
                .and(dto.getVoimassaNyt() == null ? null : dto.getVoimassaNyt() ? voimassa(now()) : Specifications.not(voimassa(now())));
        return where(latest(where)).and(where);
    }
    
    private Function<Oikeustulkki, OikeustulkkiVirkailijaListDto> combineHenkiloVirkailija(Function<String, Henkilo> h) {
        return ot -> {
            Henkilo henkilo = h.apply(ot.getTulkki().getHenkiloOid());
            OikeustulkkiVirkailijaListDto dto = new OikeustulkkiVirkailijaListDto();
            dto.setId(ot.getId());
            dto.setAlkaa(ot.getAlkaa());
            dto.setHetu(henkilo.getHetu());
            dto.setPaattyy(ot.getPaattyy());
            dto.setHenkiloOid(henkilo.getOidHenkilo());
            dto.setEtunimi(henkilo.getEtunimet());
            dto.setSukunimi(henkilo.getSukunimi());
            return dto;
        };
    }

    @Override
    @Transactional(readOnly = true)
    public List<OikeustulkkiPublicListDto> haeJulkinen(OikeustulkkiPublicHakuDto hakuDto) {
        return doHaku(henkiloResourceReadClient,
                new Haku<>(hakuDto, hakuDto.getTermi(), spec(hakuDto)),
                this::combineHenkilo);
    }
    
    private static Specifications<Oikeustulkki> spec(OikeustulkkiPublicHakuDto dto) {
        Specifications<Oikeustulkki> where = eiPoistettu.and(voimassaoloRajausLoppu(now()))
                .and(kieliparit(dto.getKieliparit()))
                .and(toimiiMaakunnissa(singletonList(dto.getMaakuntaKoodi())));
        return where(latest(where)).and(where);
    }

    private Function<Oikeustulkki, OikeustulkkiPublicListDto> combineHenkilo(Function<String, Henkilo> h) {
        return ot -> {
            Henkilo henkilo = h.apply(ot.getTulkki().getHenkiloOid());
            OikeustulkkiPublicListDto dto = new OikeustulkkiPublicListDto();
            dto.setPaattyy(ot.getPaattyy());
            dto.setEtunimi(henkilo.getEtunimet());
            dto.setSukunimi(henkilo.getSukunimi());
            return dto;
        };
    }

    @Getter @AllArgsConstructor
    private static class Haku<HakuType extends OikeustulkkiHakuehto> {
        private final HakuType haku;
        private final String mostRestrictiveHenkiloCondition;
        private final Specification<Oikeustulkki> oikeustulkkiSpecification;
    }

    private <DtoType,HakuType extends OikeustulkkiHakuehto> List<DtoType> doHaku(HenkiloApi api, Haku<HakuType> haku,
                             Function<Function<String,Henkilo>, Function<Oikeustulkki, DtoType>> combiner) {
        HakuType hakuDto = haku.getHaku();
        Integer count = ofNullable(hakuDto.getCount()).orElse(DEFAULT_COUNT),
                page = ofNullable(hakuDto.getPage()).orElse(1),
                index = (page-1)*count;
        List<Henkilo> henkiloResults = listHenkilosByTermi(api, haku.getMostRestrictiveHenkiloCondition(), count, index)
                .getResults().stream().collect(toList());
        Map<String,Henkilo> henkilosByOid = henkiloResults.stream().collect(toMap(Henkilo::getOidHenkilo, h -> h));
        Map<String,List<Oikeustulkki>> oikeustulkkis = oikeustulkkiRepository.findAll(where(haku.getOikeustulkkiSpecification())
                    .and(henkiloOidIn(henkilosByOid.keySet()))).stream()
                .sorted(comparing(Oikeustulkki::getAlkaa)).collect(groupingBy(ot -> ot.getTulkki().getHenkiloOid(), mapping(ot->ot, toList())));
        return henkiloResults.stream().flatMap(ot -> ofNullable(oikeustulkkis.get(ot.getOidHenkilo()))
                .map(List::stream).orElseGet(Stream::empty)).map(combiner.apply(henkilosByOid::get)).collect(toList());
    }

    private PaginationObject<Henkilo> listHenkilosByTermi(HenkiloApi api, String termi, Integer count, Integer index) {
        return api.listHenkilos(termi, null, count, index, singletonList(oikeustulkkirekisteriOrganisaatioOid),
                null, null, null, false, true, false, false, null, false);
    }
}
