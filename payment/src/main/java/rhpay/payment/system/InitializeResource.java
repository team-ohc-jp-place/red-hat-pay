package rhpay.payment.system;

import io.quarkus.infinispan.client.Remote;
import io.quarkus.qute.Template;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.opentracing.Traced;
import org.infinispan.client.hotrod.RemoteCache;
import rhpay.monitoring.MonitorRest;
import rhpay.payment.cache.*;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/")
@Traced
@MonitorRest
public class InitializeResource {

    @Inject
    @Remote("payment")
    RemoteCache<TokenKey, PaymentEntity> paymentCache;

    @Inject
    @Remote("user")
    RemoteCache<ShopperKey, ShopperEntity> userCache;

    @Inject
    @Remote("token")
    RemoteCache<TokenKey, TokenEntity> tokenCache;

    @Inject
    @Remote("wallet")
    RemoteCache<ShopperKey, WalletEntity> walletCache;

    @Inject
    Template initialize;


    @GET
    @Path("/init/{userNum}/{amount}/{autoCharge}")
    @Produces(MediaType.TEXT_HTML)
    public Uni<String> initialize(@PathParam("userNum") final int userNum, @PathParam("amount") final int amount, @PathParam("autoCharge") final int autoCharge) {

        // キャッシュのデータを全てクリア
        paymentCache.clear();
        userCache.clear();
        tokenCache.clear();
        walletCache.clear();

        // キャッシュのデータを作成
        for(int i = 0 ; i < userNum ; i++){
            ShopperKey shopperKey = new ShopperKey(i);
            userCache.put(shopperKey, new ShopperEntity("user"+i));
            walletCache.put(shopperKey, new WalletEntity(amount, autoCharge));
        }

        return Uni.createFrom().completionStage(() -> initialize.instance().renderAsync());
    }

}
