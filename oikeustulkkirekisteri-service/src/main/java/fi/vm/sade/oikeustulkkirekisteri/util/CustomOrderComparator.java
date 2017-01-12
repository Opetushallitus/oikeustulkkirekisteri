package fi.vm.sade.oikeustulkkirekisteri.util;

import static java.util.Arrays.asList;
import java.util.Comparator;
import java.util.List;
import static java.util.Objects.requireNonNull;

/**
 * {@link Comparator}-toteutus järjestämään taulukko mielivaltaiseen
 * järjestykseen.
 *
 * <pre>
 * {@code
 * List<String> lista = Arrays.asList("1", "2", "3", "4", "5");
 * Comparator<String> comparator = new CustomOrderComparator<>("3", "1", "4").thenComparing(naturalOrder());
 * Collections.sort(lista, comparator);
 * System.out.println(lista); // tulostaa "[3, 1, 4, 2, 5]"
 * }
 * </pre>
 *
 * @param <T> tyyppi
 */
public class CustomOrderComparator<T> implements Comparator<T> {

    private final List<T> order;

    public CustomOrderComparator(T... order) {
        this(asList(order));
    }

    public CustomOrderComparator(List<T> order) {
        this.order = requireNonNull(order);
    }

    @Override
    public int compare(T o1, T o2) {
        boolean o1included = order.contains(o1);
        boolean o2included = order.contains(o2);
        if (o1included && o2included) {
            return order.indexOf(o1) - order.indexOf(o2);
        }
        if (o1included) {
            return -1;
        }
        if (o2included) {
            return 1;
        }
        return 0;
    }

}
