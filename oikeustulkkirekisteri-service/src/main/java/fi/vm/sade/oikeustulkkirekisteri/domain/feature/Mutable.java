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
    @Getter@Setter
    @Type(type = "dateTime")
    private DateTime muokattu;
    @Getter@Setter
    private String muokkaaja;
}
