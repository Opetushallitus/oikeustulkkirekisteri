package fi.vm.sade.oikeustulkkirekisteri.validation.validator;

import fi.vm.sade.generic.common.HetuUtils;
import fi.vm.sade.oikeustulkkirekisteri.validation.ValidHetu;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * User: tommiratamaa
 * Date: 7.6.2016
 * Time: 12.43
 */
public class HetuValidator implements ConstraintValidator<ValidHetu, String> {
   public void initialize(ValidHetu constraint) {
   }

   public boolean isValid(String hetu, ConstraintValidatorContext context) {
      return hetu == null || HetuUtils.isHetuValid(hetu);
   }
}
