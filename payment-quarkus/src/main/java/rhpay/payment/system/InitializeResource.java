package rhpay.payment.system;

import io.quarkus.infinispan.client.Remote;
import io.quarkus.qute.Template;
import io.smallrye.mutiny.Uni;
import org.infinispan.client.hotrod.RemoteCache;
import rhpay.payment.cache.*;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;

@Path("/")
//@Traced
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

    private final int BATCH_ENTRY_NUM = 5000;

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
        Map<ShopperKey, ShopperEntity> userMap = new HashMap<>(BATCH_ENTRY_NUM);
        Map<ShopperKey, WalletEntity> walletMap = new HashMap<>(BATCH_ENTRY_NUM);
        int batchCount = 0;

        for(int i = 0 ; i < userNum ; i++){
            batchCount++;
            ShopperKey shopperKey = new ShopperKey(i);
            userMap.put(shopperKey, new ShopperEntity("user"+i));
            walletMap.put(shopperKey, new WalletEntity(amount, autoCharge));

            if(batchCount == BATCH_ENTRY_NUM){
                userCache.putAllAsync(userMap);
                walletCache.putAllAsync(walletMap);

                batchCount = 0;
                userMap.clear();
                walletMap.clear();
            }
        }

        if(batchCount != 0){
            userCache.putAllAsync(userMap);
            walletCache.putAllAsync(walletMap);
        }

        return Uni.createFrom().completionStage(() -> initialize.instance().renderAsync());
    }

}
