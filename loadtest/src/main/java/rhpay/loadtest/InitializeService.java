package rhpay.loadtest;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/")
@RegisterRestClient(configKey="initialize-api")
public interface InitializeService {

    @GET
    @Path("/init/{userNum}/{amount}/{autoCharge}")
    @Consumes(MediaType.APPLICATION_JSON)
    String initialize(@PathParam("userNum") final int userNum, @PathParam("amount") final int amount, @PathParam("autoCharge") final int autoCharge);

}
