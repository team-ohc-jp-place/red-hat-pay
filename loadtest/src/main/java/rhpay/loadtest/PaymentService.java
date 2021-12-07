package rhpay.loadtest;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import rhpay.payment.domain.Payment;
import rhpay.payment.domain.Token;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/")
@RegisterRestClient(configKey="payment-api")
public interface PaymentService {

    @POST
    @Path("/pay/{userId}")
    @Consumes(MediaType.APPLICATION_JSON)
    TokenResponse getToken(@PathParam("userId") final int userId);

    @POST
    @Path("/pay/{userId}/{tokenId}/{storeId}/{amount}")
    @Consumes(MediaType.APPLICATION_JSON)
    PaymentResponse pay(@PathParam("userId") final int userId, @PathParam("tokenId") final String tokenId, @PathParam("storeId") final int storeId, @PathParam("amount") int amount);
}
