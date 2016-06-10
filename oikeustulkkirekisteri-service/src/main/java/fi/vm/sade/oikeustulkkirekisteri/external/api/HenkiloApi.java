package fi.vm.sade.oikeustulkkirekisteri.external.api;

import com.wordnik.swagger.annotations.ApiParam;
import fi.vm.sade.oikeustulkkirekisteri.external.api.dto.HenkiloCreateDto;
import fi.vm.sade.oikeustulkkirekisteri.external.api.dto.HenkiloRestDto;
import fi.vm.sade.oikeustulkkirekisteri.external.api.dto.PaginationObject;

import javax.ws.rs.*;
import java.util.List;

import static fi.vm.sade.oikeustulkkirekisteri.external.api.Constants.JSON;

/**
 * User: tommiratamaa
 * Date: 30.5.2016
 * Time: 14.50
 */
@Path("/resources/henkilo")
public interface HenkiloApi {
    @GET
    @Produces(JSON)
    PaginationObject<HenkiloRestDto> listHenkilos(
            @ApiParam("Hakuparametri, henkilötunnus, nimi tai henkilö-oid") @QueryParam("q") String queryParam,
            @ApiParam("Henkilötyypin määre") @QueryParam("ht") String henkiloTyyppi,
            @ApiParam("Haun määrärajoite, arvo 0 tarkoittaa hae kaikki") @QueryParam("count") Integer count,
            @ApiParam("Haun aloitusindeksi") @QueryParam("index") Integer index,
            @ApiParam("Rajaus organisaatioiden mukaan") @QueryParam("org") List<String> orgOid,
            @ApiParam("Rajaus palvelun mukaan") @QueryParam("serviceName") String serviceName,
            @ApiParam("Käyttöoikeusryhmärajaus") @QueryParam("groupName") String groupName,
            @ApiParam("Järjestyksen määrääminen") @QueryParam("order") String order,
            @ApiParam(value= "Myös passiivisten henkilöiden haku", defaultValue="false") @QueryParam("p") Boolean passives,
            @ApiParam("Myös aliorganisaatioiden sisällyttäminen") @QueryParam("s") Boolean subOrganizations,
            @ApiParam("Myös organisaatiottomat henkilöt") @QueryParam("no") Boolean noOrganizations,
            @ApiParam("Myös suljetut käyttöoikeudet") @QueryParam("groupClosed") Boolean groupClosed,
            @ApiParam("Rajaus huoltajan hetun tai OID:n mukaan") @QueryParam("guardian") String guardian,
            @ApiParam("Myös duplikaattihenkilöiden haku") @QueryParam("d") Boolean duplicates);

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