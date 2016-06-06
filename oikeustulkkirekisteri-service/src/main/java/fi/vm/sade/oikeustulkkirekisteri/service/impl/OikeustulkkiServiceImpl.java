package fi.vm.sade.oikeustulkkirekisteri.service.impl;

import fi.vm.sade.authentication.model.Henkilo;
import fi.vm.sade.oikeustulkkirekisteri.domain.Oikeustulkki;
import fi.vm.sade.oikeustulkkirekisteri.external.api.HenkiloApi;
import fi.vm.sade.oikeustulkkirekisteri.external.api.dto.PaginationObject;
import fi.vm.sade.oikeustulkkirekisteri.repository.OikeustulkkiRepository;
import fi.vm.sade.oikeustulkkirekisteri.service.OikeustulkkiService;
import fi.vm.sade.oikeustulkkirekisteri.service.dto.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static fi.vm.sade.oikeustulkkirekisteri.service.dto.OikeustulkkiHakuSpecBuilder.empty;
import static fi.vm.sade.oikeustulkkirekisteri.service.dto.OikeustulkkiHakuSpecBuilder.*;
import static java.util.Collections.singletonList;
import static java.util.Comparator.comparing;
import static java.util.Optional.empty;
import static java.util.Optional.*;
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
    
    @Autowired
    private OikeustulkkiRepository oikeustulkkiRepository;
    
    @Resource
    private HenkiloApi henkiloResourceClient;
    
    @Resource
    private HenkiloApi henkiloResourceReadClient;
    
    @Value("${oikeustulkkirekisteri.organisaatio.oid}")
    private String oikeustulkkirekisteriOrganisaatioOid;
    
    @Override
    @Transactional(readOnly = true)
    public List<OikeustulkkiVirkailijaListDto> haeVirkailija(OikeustulkkiVirkailijaHakuDto hakuDto) {
        return doHaku(henkiloResourceClient,
                new Haku<>(hakuDto, mostRestrictiveCondition(hakuDto).orElse(null), henkiloPredicate(hakuDto), spec(hakuDto)),
                this::combineHenkiloVirkailija);
    }
    
    private Optional<String> mostRestrictiveCondition(OikeustulkkiVirkailijaHakuDto hakuDto) {
        if (hakuDto.getOid() != null) {
            return of(hakuDto.getOid());
        }
        if (hakuDto.getHetu() != null) {
            return of(hakuDto.getHetu());
        }
        if (hakuDto.getNimi() != null) {
            return of(hakuDto.getNimi());
        }
        return empty();
    }
    
    private Predicate<Henkilo> henkiloPredicate(OikeustulkkiVirkailijaHakuDto hakuDto) {
        Predicate<Henkilo> p = h -> true;
        if (hakuDto.getHetu() != null) {
            p = p.and(h -> h.getHetu().startsWith(hakuDto.getHetu().trim().toLowerCase()));
        }
        if (hakuDto.getOid() != null) {
            p = p.and(h -> h.getOidHenkilo().startsWith(hakuDto.getOid().trim().toLowerCase()));
        }
        if (hakuDto.getNimi() != null) {
            p = p.and(nimiHaku(hakuDto.getNimi()));
        }
        return p;
    }
    
    private static Predicate<Henkilo> nimiHaku(String nimi) {
        return Stream.of(nimi.trim().toLowerCase().split("\\s+"))
                .reduce(h -> true, (Predicate<Henkilo> p1, String s) ->
                        (h -> h.getEtunimet().trim().toLowerCase().contains(s) || h.getSukunimi().trim().toLowerCase().contains(s)), Predicate::and);
    }
    
    private static Specifications<Oikeustulkki> spec(OikeustulkkiVirkailijaHakuDto dto) {
        Specifications<Oikeustulkki> where =  empty.and(voimassaoloRajausAlku(dto.getVoimassaAlku()))
                .and(voimassaoloRajausLoppu(dto.getVoimassaLoppu()))
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
            dto.setPaattyy(ot.getPaattyy());
            dto.setHenkiloOid(henkilo.getOidHenkilo());
            dto.setEtunimi(henkilo.getEtunimet());
            dto.setSukunimi(henkilo.getSukunimi());
            return dto;
        };
    }

    @Override
    @Transactional(readOnly = true)
    public List<OikeustulkkiPublicListDto> haeJulkinen(OikeustulkkiJulkinenHakuDto hakuDto) {
        return doHaku(henkiloResourceReadClient,
                new Haku<>(hakuDto, hakuDto.getNimi(), henkiloPredicate(hakuDto), spec(hakuDto)),
                this::combineHenkilo);
    }
    
    private Predicate<Henkilo> henkiloPredicate(OikeustulkkiJulkinenHakuDto hakuDto) {
        Predicate<Henkilo> p = h -> true;
        if (hakuDto.getNimi() != null) {
            p = p.and(nimiHaku(hakuDto.getNimi()));
        }
        return p;
    }

    private static Specifications<Oikeustulkki> spec(OikeustulkkiJulkinenHakuDto dto) {
        Specifications<Oikeustulkki> where = where(voimassaoloRajausLoppu(now()))
                .and(kieliparit(dto.getKieliparit()));
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
        private final Predicate<Henkilo> henkiloPredicate;
        private final Specification<Oikeustulkki> oikeustulkkiSpecification;
    }

    private <DtoType,HakuType extends OikeustulkkiHakuehto> List<DtoType> doHaku(HenkiloApi api, Haku<HakuType> haku,
                             Function<Function<String,Henkilo>, Function<Oikeustulkki, DtoType>> combiner) {
        HakuType hakuDto = haku.getHaku();
        Integer count = ofNullable(hakuDto.getCount()).orElse(DEFAULT_COUNT),
                page = ofNullable(hakuDto.getPage()).orElse(1),
                index = (page-1)*count;
        List<Henkilo> henkiloResults = listHenkilosByTermi(api, haku.getMostRestrictiveHenkiloCondition(), count, index)
                .getResults().stream().filter(haku.getHenkiloPredicate()).collect(toList());
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
