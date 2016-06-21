package fi.vm.sade.oikeustulkkirekisteri.service.impl;

import fi.vm.sade.oikeustulkkirekisteri.external.api.dto.HenkiloRestDto;
import lombok.Getter;

import java.util.function.Predicate;

/**
 * User: tommiratamaa
 * Date: 20.6.2016
 * Time: 17.50
 */
public class HenkiloTermPredicate implements Predicate<HenkiloRestDto> {
    @Getter
    private final String term;
    @Getter 
    private boolean matchHetu;

    public HenkiloTermPredicate(String term) {
        this.term = normalize(term);
    }

    private static String normalize(String term) {
        return term == null ? "" : term.trim().toLowerCase();
    }

    public HenkiloTermPredicate matchingHetu() {
        this.matchHetu = true;
        return this;
    }

    @Override
    public boolean test(HenkiloRestDto h) {
        if (term.isEmpty()) {
            return true;
        }
        if (matchHetu && normalize(h.getHetu()).startsWith(term)) {
            return true;
        }
        return normalize(h.getOidHenkilo()).equals(term)
                || (h.getEtunimet() + " " + h.getSukunimi()).startsWith(term)
                || (h.getKutsumanimi() + " " + h.getSukunimi()).startsWith(term)
                || (h.getSukunimi() + " " + h.getEtunimet()).startsWith(term)
                || (h.getSukunimi() + " " + h.getKutsumanimi()).startsWith(term);
    }
}
