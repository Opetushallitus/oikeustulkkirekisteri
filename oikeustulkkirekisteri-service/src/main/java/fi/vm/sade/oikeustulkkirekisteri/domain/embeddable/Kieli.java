package fi.vm.sade.oikeustulkkirekisteri.domain.embeddable;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * User: tommiratamaa
 * Date: 30.5.2016
 * Time: 12.57
 */
@Embeddable
@Getter @Setter
public class Kieli {
    @Column(name = "koodi", nullable = false)
    private String koodi;
}
