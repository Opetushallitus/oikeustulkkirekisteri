package fi.vm.sade.oikeustulkkirekisteri.external.api;

import com.wordnik.swagger.annotations.ApiParam;
import fi.vm.sade.oikeustulkkirekisteri.external.api.dto.HenkiloCreateDto;
import fi.vm.sade.oikeustulkkirekisteri.external.api.dto.HenkiloRestDto;

import javax.ws.rs.*;

import static fi.vm.sade.oikeustulkkirekisteri.external.api.Constants.JSON;

/**
 * User: tommiratamaa
 * Date: 30.5.2016
 * Time: 14.50
 *
 * @deprecated {@link OppijanumerorekisteriApi}
 */
@Path("/resources/henkilo")
@Deprecated
public interface HenkiloApi {

    @GET
    @Produces(JSON)
    @Path("/{oid}")
    HenkiloRestDto findByOid(@ApiParam("Haettavan henkilön OID") @PathParam("oid") String oid);

    @POST
    @Produces("application/json")
    @Consumes(JSON)
    String createHenkilo(HenkiloCreateDto henkilo);
    
    @PUT
    @Path("/{oid}")
    @Produces("application/json")
    @Consumes(JSON)
    String updateHenkilo(@PathParam("oid") String oid, HenkiloRestDto henkilo);
}