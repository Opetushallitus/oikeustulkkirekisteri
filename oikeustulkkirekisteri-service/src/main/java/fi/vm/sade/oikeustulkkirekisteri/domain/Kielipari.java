package fi.vm.sade.oikeustulkkirekisteri.domain;

import fi.vm.sade.oikeustulkkirekisteri.domain.embeddable.Kieli;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

/**
 * User: tommiratamaa
 * Date: 30.5.2016
 * Time: 12.47
 */
@Entity
@Getter @Setter
@Table(name = "kielipari", schema = "public", uniqueConstraints =
    @UniqueConstraint(columnNames = {"oikeustulkki", "kielesta", "kieleen"}))
public class Kielipari implements Serializable {
    @Id
    @Column(name = "id", nullable = false, updatable = false, unique = true)
    @GeneratedValue(generator = "kielipari_id_seq")
    @SequenceGenerator(name = "kielipari_id_seq", sequenceName = "kielipari_id_seq")
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "oikeustulkki", nullable = false)
    private Oikeustulkki oikeustulkki;
    
    @AttributeOverrides(@AttributeOverride(name = "koodi", column = @Column(name = "kielesta", nullable = false, updatable = false)))
    private Kieli kielesta;
    
    @AttributeOverrides(@AttributeOverride(name = "koodi", column = @Column(name = "kieleen", nullable = false, updatable = false)))
    private Kieli kieleen;
}
