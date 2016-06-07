package fi.vm.sade.oikeustulkkirekisteri.domain;

import lombok.*;
import org.hibernate.annotations.Immutable;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

import static lombok.AccessLevel.PROTECTED;

/**
 * User: tommiratamaa
 * Date: 30.5.2016
 * Time: 12.53
 */
@Entity
@Immutable
@Getter @Setter
@NoArgsConstructor(access = PROTECTED)
@Table(name = "sijainti", schema = "public", uniqueConstraints =
    @UniqueConstraint(columnNames = {"oikeustulkki", "tyyppi", "koodi"}))
public class Sijainti implements Serializable {
    public enum Tyyppi {
        MAAKUNTA,
        KOKO_SUOMI
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
    private Tyyppi tyyppi = Tyyppi.MAAKUNTA;
    
    @Column(name = "koodi")
    private String koodi;

    public Sijainti(Oikeustulkki oikeustulkki, Tyyppi tyyppi) {
        this.oikeustulkki = oikeustulkki;
        this.tyyppi = tyyppi;
    }

    public Sijainti(Oikeustulkki oikeustulkki, Tyyppi tyyppi, String koodi) {
        this.oikeustulkki = oikeustulkki;
        this.tyyppi = tyyppi;
        this.koodi = koodi;
    }
}
