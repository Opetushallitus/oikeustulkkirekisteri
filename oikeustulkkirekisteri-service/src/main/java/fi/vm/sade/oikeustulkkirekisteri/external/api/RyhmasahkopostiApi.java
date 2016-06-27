package fi.vm.sade.oikeustulkkirekisteri.external.api;

import fi.vm.sade.ryhmasahkoposti.api.dto.EmailData;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import static fi.vm.sade.oikeustulkkirekisteri.external.api.Constants.JSON;

/**
 * User: tommiratamaa
 * Date: 27.6.2016
 * Time: 12.14
 */
@Path("email")
public interface RyhmasahkopostiApi {
    /**
     * @param data email to be sent
     * @return the id of the email send operation
     */
    @POST
    @Consumes(JSON)
    @Produces({"application/json"})
    String sendEmail(EmailData data);
}
