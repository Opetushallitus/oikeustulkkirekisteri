package fi.vm.sade.oikeustulkkirekisteri.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

import java.io.Serializable;
import java.time.LocalDateTime;


/**
 * User: tommiratamaa
 * Date: 7.6.2016
 * Time: 12.09
 */
@Entity
@Getter @Setter
@Table(name = "oikeustulkki_muokkaus", schema = "public")
public class OikeustulkkiMuokkaus implements Serializable {
    @Id
    @Column(name = "id", nullable = false, updatable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "oikeustulkki_muokkaus_id_seq")
    @SequenceGenerator(name = "oikeustulkki_muokkaus_id_seq", sequenceName = "oikeustulkki_muokkaus_id_seq", allocationSize = 1)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "oikeustulkki", nullable = false)
    private Oikeustulkki oikeustulkki;

    @Column(name = "muokattu", nullable = false)
    private LocalDateTime muokattu = LocalDateTime.now();
    
    @Column(name = "muokkaaja", nullable = false) // oid
    private String muokkaaja;
    
    @Column(name = "muokkausviesti")
    private String muokkausviesti;
}
