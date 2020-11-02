package fi.vm.sade.oikeustulkkirekisteri.domain;

import fi.vm.sade.oikeustulkkirekisteri.domain.embeddable.Kieli;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

import java.io.Serializable;
import java.time.LocalDate;

import static lombok.AccessLevel.PROTECTED;

/**
 * User: tommiratamaa
 * Date: 30.5.2016
 * Time: 12.47
 */
@Entity
@Getter @Setter
@NoArgsConstructor(access = PROTECTED)
@Table(name = "kielipari", schema = "public", uniqueConstraints =
    @UniqueConstraint(columnNames = {"oikeustulkki", "kielesta", "kieleen"}))
public class Kielipari implements Serializable {
    @Id
    @Column(name = "id", nullable = false, updatable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "kielipari_id_seq")
    @SequenceGenerator(name = "kielipari_id_seq", sequenceName = "kielipari_id_seq", allocationSize = 1)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "oikeustulkki", nullable = false)
    private Oikeustulkki oikeustulkki;

    @Column(name = "voimassaolo_alkaa",   nullable = false)
    private LocalDate voimassaoloAlkaa;

    @Column(name = "voimassaolo_paattyy", nullable = false)
    private LocalDate voimassaoloPaattyy;

    @AttributeOverrides(@AttributeOverride(name = "koodi", column = @Column(name = "kielesta", nullable = false, updatable = false)))
    private Kieli kielesta;

    @AttributeOverrides(@AttributeOverride(name = "koodi", column = @Column(name = "kieleen", nullable = false, updatable = false)))
    private Kieli kieleen;

    public Kielipari(Oikeustulkki oikeustulkki, Kieli kielesta, Kieli kieleen, LocalDate voimassaoloAlkaa, LocalDate voimassaoloPaattyy) {
        this.oikeustulkki = oikeustulkki;
        this.kielesta = kielesta;
        this.kieleen = kieleen;
        this.voimassaoloAlkaa = voimassaoloAlkaa;
        this.voimassaoloPaattyy = voimassaoloPaattyy;
    }
}
