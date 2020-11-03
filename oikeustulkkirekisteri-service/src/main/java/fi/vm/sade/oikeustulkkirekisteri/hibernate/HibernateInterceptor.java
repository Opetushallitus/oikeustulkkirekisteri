package fi.vm.sade.oikeustulkkirekisteri.hibernate;

import fi.vm.sade.oikeustulkkirekisteri.domain.feature.Creatable;
import fi.vm.sade.oikeustulkkirekisteri.domain.feature.Modifyable;
import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.function.Supplier;

import static java.util.Optional.ofNullable;

/**
 * Hibernate interceptor for auto-setting create/modification oids/timestamps:
 * @see Creatable
 * @see Modifyable
 * 
 * User: tommiratamaa
 * Date: 30.5.2016
 * Time: 13.41
 */
public class HibernateInterceptor extends EmptyInterceptor {
    private static final Supplier<String> LOGGED_IN_USER = ()
        -> ofNullable(SecurityContextHolder.getContext().getAuthentication()).map(Authentication::getName).orElse(null);
    
    @Override
    public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState, String[] propertyNames, Type[] types) {
        boolean changed = false;
        if (entity instanceof Modifyable) {
            changed = set("muokattu", propertyNames, currentState, LocalDateTime::now);
            changed |= set("muokkaaja", propertyNames, currentState, LOGGED_IN_USER);
        }
        return changed;
    }

    @Override
    public boolean onSave(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
        boolean changed = false;
        if (entity instanceof Creatable) {
            changed = set("luotu", propertyNames, state, LocalDateTime::now);
            changed |= set("luoja", propertyNames, state, LOGGED_IN_USER);
        }
        return changed;
    }
    
    private boolean set(String property, String[] propertyNames, Object[] currentState, Supplier<?> supplier) {
        int i = 0;
        for (String p : propertyNames) {
            if (p.equals(property) && currentState[i] == null) {
                Object v = supplier.get();
                if (v != null) {
                    currentState[i] =  v;
                    return true;
                }
            }
            ++i;
        }
        return false;
    }
}
