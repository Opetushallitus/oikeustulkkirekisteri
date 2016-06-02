package fi.vm.sade.oikeustulkkirekisteri.domain;

import fi.vm.sade.oikeustulkkirekisteri.domain.feature.Mutable;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.joda.time.LocalDate;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * User: tommiratamaa
 * Date: 30.5.2016
 * Time: 12.41
 */
@Entity
@Getter @Setter
@Table(name = "oikeustulkki", schema = "public")
public class Oikeustulkki extends Mutable {
    @Id
    @Column(name = "id", nullable = false, updatable = false, unique = true)
    @GeneratedValue(generator = "oikeustulkki_id_seq")
    @SequenceGenerator(name = "oikeustulkki_id_seq", sequenceName = "oikeustulkki_id_seq")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tulkki", nullable = false)
    private Tulkki tulkki;
    
    @Type(type = "localDate")
    @Column(name = "alkaa", nullable = false)
    private LocalDate alkaa;
    
    @Type(type = "localDate")
    @Column(name = "paattyy", nullable = false)
    private LocalDate paattyy;
    
    @Column(name = "lisatiedot")
    private String lisatiedot;
    
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true,
            mappedBy = "oikeustulkki")
    private Set<Kielipari> kielet = new HashSet<>(0);
    
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true,
            mappedBy = "oikeustulkki")
    private Set<Sijainti> sijainnit = new HashSet<>(0);
}
