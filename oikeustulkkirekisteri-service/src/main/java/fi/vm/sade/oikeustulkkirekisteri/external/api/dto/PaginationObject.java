package fi.vm.sade.oikeustulkkirekisteri.external.api.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * User: tommiratamaa
 * Date: 2.6.2016
 * Time: 12.49
 */
@Getter @Setter
public class PaginationObject<T> {
    private Long totalCount;
    private List<T> results;
}
