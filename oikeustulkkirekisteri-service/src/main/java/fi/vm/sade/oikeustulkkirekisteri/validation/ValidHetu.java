package fi.vm.sade.oikeustulkkirekisteri.validation;

import fi.vm.sade.oikeustulkkirekisteri.validation.validator.HetuValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * User: tommiratamaa
 * Date: 7.6.2016
 * Time: 12.39
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Constraint(validatedBy = HetuValidator.class)
@Documented
public @interface ValidHetu {
    String message() default "{fi.vm.sade.oikeustulkkirekisteri.hetu}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
