package fi.vm.sade.oikeustulkkirekisteri.external.api;

import fi.vm.sade.authentication.model.Henkilo;
import fi.vm.sade.oikeustulkkirekisteri.external.api.dto.HenkiloCreateDTO;

import javax.ws.rs.*;

/**
 * User: tommiratamaa
 * Date: 30.5.2016
 * Time: 14.50
 */
@Path("resources/henkilo")
public interface HenkiloApi {
    public static enum ExternalPermissionService {
        HAKU_APP, SURE
    }
    
    @POST
    String createHenkilo(HenkiloCreateDTO henkilo);

    @PUT
    @Path("/{oid}")
    String updateHenkilo(@PathParam("oid") String oid, Henkilo henkilo,
         @HeaderParam("External-Permission-Service") ExternalPermissionService permissionService);
}