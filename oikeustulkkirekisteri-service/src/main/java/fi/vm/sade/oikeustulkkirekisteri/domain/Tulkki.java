package fi.vm.sade.oikeustulkkirekisteri.domain;

import fi.vm.sade.oikeustulkkirekisteri.domain.feature.Mutable;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Immutable;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * User: tommiratamaa
 * Date: 30.5.2016
 * Time: 11.08
 */
@Entity
@Immutable
@Getter @Setter
@Table(name = "tulkki", schema = "public")
public class Tulkki extends Mutable {
    @Id
    @Column(name = "id", nullable = false, updatable = false, unique = true)
    @GeneratedValue(generator = "tulkki_id_seq")
    @SequenceGenerator(name = "tulkki_id_seq", sequenceName = "tulkki_id_seq")
    private Long id;
    
    @Column(name = "henkilo_oid", nullable = false, unique = true, updatable = false)
    private String henkiloOid;
    
    @Column(name = "julklaisulupa", nullable = false)
    private boolean julklaisulupa;
    
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "tulkki", cascade = CascadeType.ALL)
    private Set<Oikeustulkki> oikeustulkit = new HashSet<>(0);
}
