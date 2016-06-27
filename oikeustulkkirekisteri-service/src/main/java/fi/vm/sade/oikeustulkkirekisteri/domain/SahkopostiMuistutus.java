package fi.vm.sade.oikeustulkkirekisteri.domain;

import fi.vm.sade.oikeustulkkirekisteri.domain.embeddable.Kieli;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.*;
import java.io.Serializable;

import static org.joda.time.DateTime.now;

/**
 * User: tommiratamaa
 * Date: 21.6.2016
 * Time: 14.07
 */
@Entity
@Getter @Setter
@Table(name = "sahkoposti_muistutus", schema = "public")
public class SahkopostiMuistutus implements Serializable {
    @Id
    @Column(name = "id", nullable = false, updatable = false, unique = true)
    @GeneratedValue(generator = "sahkoposti_muistutus_id_seq")
    @SequenceGenerator(name = "sahkoposti_muistutus_id_seq", sequenceName = "sahkoposti_muistutus_id_seq")
    private Long id;
    @Type(type = "dateTime")
    @Column(name = "luotu")
    private DateTime luotu = now();
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "oikeustulkki", nullable = false)
    private Oikeustulkki oikeustulkki;
    @Type(type = "dateTime") 
    @Column(name = "lahetetty")
    private DateTime lahetetty;
    @Column(name = "lahettaja", nullable = false)
    private String lahettaja;
    @Column(name = "vastaanottaja", nullable = false)
    private String vastaanottaja;
    @Column(name = "template_name", nullable = false)
    private String templateName;
    @Column(name = "sahkoposti_id")
    private Long sahkopostiId;
    @AttributeOverrides(@AttributeOverride(name = "koodi", column = @Column(name = "kieli", nullable = false, updatable = false)))
    private Kieli kieli;
    @Column(name = "virhe")
    private String virhe;
}
