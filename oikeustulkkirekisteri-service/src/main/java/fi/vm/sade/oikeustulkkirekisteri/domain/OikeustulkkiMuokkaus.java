package fi.vm.sade.oikeustulkkirekisteri.domain;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.*;

import java.io.Serializable;

import static org.joda.time.DateTime.now;

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
    @GeneratedValue(generator = "oikeustulkki_muokkaus_id_seq")
    @SequenceGenerator(name = "oikeustulkki_muokkaus_id_seq", sequenceName = "oikeustulkki_muokkaus_id_seq")
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "oikeustulkki", nullable = false)
    private Oikeustulkki oikeustulkki;
    
    @Type(type = "dateTime")
    @Column(name = "muokattu", nullable = false)
    private DateTime muokattu = now();
    
    @Column(name = "muokkaaja", nullable = false) // oid
    private String muokkaaja;
    
    @Column(name = "muokkausviesti")
    private String muokkausviesti;
}
