package fi.vm.sade.oikeustulkkirekisteri.external.api;

import fi.vm.sade.authentication.model.Henkilo;
import fi.vm.sade.oikeustulkkirekisteri.external.api.dto.HenkiloCreateDto;

import javax.ws.rs.*;

import static fi.vm.sade.oikeustulkkirekisteri.external.api.Constants.JSON;

/**
 * User: tommiratamaa
 * Date: 30.5.2016
 * Time: 14.50
 */
@Path("/resources/henkilo")
public interface HenkiloApi {
    enum ExternalPermissionService {
        HAKU_APP, SURE
    }
    
    @POST
    @Produces("text/plain")
    @Consumes(JSON)
    String createHenkilo(HenkiloCreateDto henkilo);

    @PUT
    @Path("/{oid}")
    @Produces("text/plain")
    @Consumes(JSON)
    String updateHenkilo(@PathParam("oid") String oid, Henkilo henkilo,
         @HeaderParam("External-Permission-Service") ExternalPermissionService permissionService);
}