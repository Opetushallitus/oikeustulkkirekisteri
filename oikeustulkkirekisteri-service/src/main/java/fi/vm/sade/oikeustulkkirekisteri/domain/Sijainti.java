package fi.vm.sade.oikeustulkkirekisteri.domain;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Immutable;

import javax.persistence.*;
import java.io.Serializable;

/**
 * User: tommiratamaa
 * Date: 30.5.2016
 * Time: 12.53
 */
@Entity
@Immutable
@Getter @Setter
@Table(name = "sijainti", schema = "public", uniqueConstraints =
    @UniqueConstraint(columnNames = {"oikeustulkki", "tyyppi", "koodi"}))
public class Sijainti implements Serializable {
    public enum Tyyppi {
        MAAKUNTA,
        KUNTA
    }

    @Id
    @Column(name = "id", nullable = false, updatable = false, unique = true)
    @GeneratedValue(generator = "sijainti_id_seq")
    @SequenceGenerator(name = "sijainti_id_seq", sequenceName = "sijainti_id_seq")
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "oikeustulkki", nullable = false)
    private Oikeustulkki oikeustulkki;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "tyyppi", nullable = false)
    private Tyyppi tyyppi;
    
    @Column(name = "koodi", nullable = false)
    private String koodi;
}
