package fi.vm.sade.oikeustulkkirekisteri.external.api;

import com.wordnik.swagger.annotations.ApiParam;
import static fi.vm.sade.oikeustulkkirekisteri.external.api.Constants.JSON;
import fi.vm.sade.oikeustulkkirekisteri.external.api.dto.HenkiloRestDto;
import fi.vm.sade.oikeustulkkirekisteri.external.api.dto.PaginationObject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
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

}
