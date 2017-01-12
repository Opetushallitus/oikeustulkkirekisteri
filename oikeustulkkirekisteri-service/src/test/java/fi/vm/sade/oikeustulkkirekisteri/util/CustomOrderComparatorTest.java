package fi.vm.sade.oikeustulkkirekisteri.util;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import org.junit.Test;

public class CustomOrderComparatorTest {

    @Test
    public void test() {
        CustomOrderComparator<String> comparator = new CustomOrderComparator<>("3", "2", "1");

        assertSoftly(softly -> {
            softly.assertThat(comparator.compare("1", "4")).isLessThanOrEqualTo(-1);
            softly.assertThat(comparator.compare("5", "1")).isGreaterThanOrEqualTo(1);
            softly.assertThat(comparator.compare("5", "1")).isGreaterThanOrEqualTo(1);
            softly.assertThat(comparator.compare("3", "1")).isLessThanOrEqualTo(-1);
            softly.assertThat(comparator.compare("5", "4")).isEqualTo(0);
        });
    }

}
