package fi.vm.sade.oikeustulkkirekisteri.domain.feature;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.joda.time.DateTime;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;
import java.io.Serializable;

import static org.joda.time.DateTime.now;

/**
 * User: tommiratamaa
 * Date: 30.5.2016
 * Time: 12.42
 */
@MappedSuperclass
@TypeDefs({
    @TypeDef(name = "localTime", typeClass = org.jadira.usertype.dateandtime.joda.PersistentLocalTime.class),
    @TypeDef(name = "localDate", typeClass = org.jadira.usertype.dateandtime.joda.PersistentLocalDate.class),
    @TypeDef(name = "dateTime", typeClass = org.jadira.usertype.dateandtime.joda.PersistentDateTime.class,
            parameters = {@Parameter(name = "databaseZone", value = "jvm")})
})
public abstract class Mutable implements Creatable, Modifyable, Serializable {
    @Type(type = "dateTime") @Getter
    @Column(name = "luotu", nullable = false, updatable = false)
    private DateTime luotu = now();
    
    @Getter @Setter
    @Column(name = "luoja", nullable = false, updatable = false)
    private String luoja;

    @Getter @Setter
    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    @Getter@Setter
    @Type(type = "dateTime")
    @Column(name = "muokattu")
    private DateTime muokattu;
    
    @Getter@Setter
    @Column(name = "muokkaaja")
    private String muokkaaja;

    @Getter
    @Column(name = "poistettu", nullable = false)
    private boolean poistettu;
    
    @Getter
    @Column(name = "poistaja")
    private String poistaja;
    
    @Getter
    @Column(name = "poistohetki")
    private DateTime poistohetki;
    
    public void markPoistettu(String poistaja) {
        this.poistohetki = now();
        this.poistettu = true;
        this.poistaja = poistaja;
    }

    protected void setLuotu(DateTime luotu) {
        this.luotu = luotu;
    }

    protected void setPoistettu(boolean poistettu) {
        this.poistettu = poistettu;
    }

    protected void setPoistaja(String poistaja) {
        this.poistaja = poistaja;
    }

    protected void setPoistohetki(DateTime poistohetki) {
        this.poistohetki = poistohetki;
    }
}
