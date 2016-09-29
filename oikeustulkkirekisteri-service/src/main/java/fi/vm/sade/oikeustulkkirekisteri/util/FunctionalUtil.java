package fi.vm.sade.oikeustulkkirekisteri.util;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.Optional.ofNullable;

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
    
    public static <F,T> Function<F,T> retrying(Function<F,T> target, int times) {
        return f -> {
            RuntimeException failure;
            int i = 0;
            do try {
                return target.apply(f);
            } catch (RuntimeException e) {
                failure = e;
            } while (i++ < times);
            throw failure;
        };
    }
    
    public static <F,T> Function<F,T> retrying(Function<F,T> target, Function<F,T> with) {
        return f -> {
            try {
                return target.apply(f);
            } catch (RuntimeException e) {
                try {
                    return with.apply(f);
                } catch (RuntimeException e2) {
                    throw e;
                }
            }
        };
    }
    
    public static <T> Supplier<FailureResult<T, RuntimeException>> retrying(Supplier<T> target, int times) {
        return () -> {
            RuntimeException failure;
            int i = 0;
            do try {
                return FailureResult.success(target.get());
            } catch (RuntimeException e) {
                failure = e;
            } while (i++ < times);
            return FailureResult.fail(failure);
        };
    }

    public static <T> Supplier<T> retrying(Supplier<T> target, Supplier<T> with) {
        return () -> {
            try {
                return target.get();
            } catch (RuntimeException e) {
                try {
                    return with.get();
                } catch (RuntimeException e2) {
                    throw e;
                }
            }
        };
    }

    public static class FailureResult<T, Ex extends RuntimeException> {
        private final Ex failure;
        private final T result;

        protected FailureResult(T result, Ex failure) {
            this.result = result;
            this.failure = failure;
        }

        public static<T, Ex extends RuntimeException> FailureResult<T, Ex> success(T result) {
            return new FailureResult<>(result, null);
        }
        
        public static<T, Ex extends RuntimeException> FailureResult<T, Ex> fail(Ex failure) {
            return new FailureResult<>(null, failure);
        }

        public Optional<T> optional() {
            return ofNullable(result);
        }
        
        public T orFail() throws Ex {
            if (this.failure != null) {
                throw failure;
            }
            return result;
        }
        
        public<Ex2 extends Throwable> T orFail(Function<Ex, Ex2> translateException) throws Ex2 {
            if (this.failure != null) {
                throw translateException.apply(this.failure);
            }
            return result;
        }
    }
}
