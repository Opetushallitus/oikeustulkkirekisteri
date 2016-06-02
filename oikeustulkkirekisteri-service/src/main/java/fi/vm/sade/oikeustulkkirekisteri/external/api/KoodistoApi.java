package fi.vm.sade.oikeustulkkirekisteri.external.api;

import fi.vm.sade.oikeustulkkirekisteri.external.api.dto.KoodistoKoodiDto;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import java.util.List;

import static fi.vm.sade.oikeustulkkirekisteri.external.api.Constants.JSON;

/**
 * User: tommiratamaa
 * Date: 1.6.2016
 * Time: 11.42
 */
@Path("/rest/json/")
public interface KoodistoApi {

    @GET
    @Path("{koodisto}/koodi")
    @Produces(JSON)
    List<KoodistoKoodiDto> listKoodis(@PathParam("koodisto") String koodisto);
    
    @GET
    @Path("relaatio/sisaltyy-ylakoodit/{koodi}")
    @Produces(JSON)
    List<KoodistoKoodiDto> listAlakoodit(@PathParam("koodi") String koodi);
}
