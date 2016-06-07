package fi.vm.sade.oikeustulkkirekisteri.resource;

import com.wordnik.swagger.annotations.Api;
import org.joda.time.DateTime;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * User: tommiratamaa
 * Date: 31.5.2016
 * Time: 12.42
 */
@Api("Oikeustulkki")
@RestController
@RequestMapping("/oikeustulkki")
public class OikeustulkkiResource {
    
    @RequestMapping(value = "/hello", method = RequestMethod.GET)
    public DateTime helloWorld() {
        return DateTime.now();
    }
}
