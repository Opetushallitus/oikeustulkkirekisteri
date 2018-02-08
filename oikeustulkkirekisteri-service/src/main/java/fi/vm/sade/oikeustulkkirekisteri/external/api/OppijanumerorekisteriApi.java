package fi.vm.sade.oikeustulkkirekisteri.external.api;

import com.wordnik.swagger.annotations.ApiParam;
import static fi.vm.sade.oikeustulkkirekisteri.external.api.Constants.JSON;
import fi.vm.sade.oikeustulkkirekisteri.external.api.dto.HenkiloCreateDto;
import fi.vm.sade.oikeustulkkirekisteri.external.api.dto.HenkiloRestDto;
import fi.vm.sade.oikeustulkkirekisteri.external.api.dto.PaginationObject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

public interface OppijanumerorekisteriApi {

    @GET
    @Produces(JSON)
    @Path("/henkilo")
    PaginationObject<HenkiloRestDto> list(
            @ApiParam("Haettavan henkilön hetu") @QueryParam("hetu") String hetu,
            @ApiParam("Passiiviset") @QueryParam("passivoitu") Boolean passivoitu,
            @ApiParam("Duplikaatit") @QueryParam("duplikaatti") Boolean duplikaatti
    );

    @GET
    @Produces(JSON)
    @Path("/henkilo/{oid}")
    HenkiloRestDto findByOid(@ApiParam("Haettavan henkilön OID") @PathParam("oid") String oid);

    @POST
    @Produces("application/json")
    @Consumes(JSON)
    @Path("/henkilo")
    String createHenkilo(HenkiloCreateDto henkilo);

    @PUT
    @Produces("application/json")
    @Consumes(JSON)
    @Path("/henkilo")
    String updateHenkilo(HenkiloRestDto henkilo);

}
