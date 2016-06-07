package fi.vm.sade.oikeustulkkirekisteri.util;

import com.google.common.base.Optional;
import fi.vm.sade.oikeustulkkirekisteri.service.exception.NotFoundException;

/**
 * User: tommiratamaa
 * Date: 7.6.2016
 * Time: 19.05
 */
public class FoundUtil {
    
    public static <T> T found(T nullable) throws NotFoundException {
        if (nullable == null) {
            throw new NotFoundException();
        }
        return nullable;
    }
    
    public static <T> T found(Optional<T> t) throws NotFoundException {
        if (!t.isPresent()) {
            throw new NotFoundException();
        }
        return t.get();
    }

    public static <T> T found(java.util.Optional<T> t) throws NotFoundException {
        if (!t.isPresent()) {
            throw new NotFoundException();
        }
        return t.get();
    }
}
