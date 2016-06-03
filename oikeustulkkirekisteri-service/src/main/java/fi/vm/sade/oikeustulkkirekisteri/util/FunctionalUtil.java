package fi.vm.sade.oikeustulkkirekisteri.util;

import java.util.function.Function;

/**
 * User: tommiratamaa
 * Date: 2.6.2016
 * Time: 13.52
 */
public class FunctionalUtil {
    private FunctionalUtil() {
    }
    
    public static <F,T> Function<F,T> or(Function<F,T> a, Function<F,T> b) {
        return f -> {
            T t = a.apply(f);
            return t != null ? t : b.apply(f);
        };
    }
}
