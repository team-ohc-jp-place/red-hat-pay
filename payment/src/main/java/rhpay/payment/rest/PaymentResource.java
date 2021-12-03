package rhpay.payment.rest;

import rhpay.monitoring.MonitorRest;
import io.quarkus.qute.Template;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.opentracing.Traced;
import rhpay.payment.domain.*;
import rhpay.payment.repository.*;
import rhpay.payment.service.*;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/")
@Traced
@MonitorRest
public class PaymentResource {

    @Inject
    ShopperRepository shopperRepository;

    @Inject
    WalletRepository walletRepository;

    @Inject
    NotifyRepository notifyRepository;

    @Inject
    TokenRepository tokenRepository;

    @Inject
    CoffeeStoreRepository coffeeStoreRepository;

    @Inject
    PaymentRepository paymentRepository;

    @Inject
    DelegateRepository delegateRepository;

    @POST
    @Path("/pay/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Token> createTokenAPI(@PathParam("userId") final int userId) {
        TokenService tokenService = new TokenService(tokenRepository);
        Token token = tokenService.create(new ShopperId(userId));
        return Uni.createFrom().item(token);
    }

    @POST
    @Path("/pay/{userId}/{tokenId}/{storeId}/{amount}")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Payment> pay(@PathParam("userId") final int userId, @PathParam("tokenId") final String tokenId, @PathParam("storeId") final int storeId, @PathParam("amount") int amount) {
        DelegateService delegateService = new DelegateService(delegateRepository);
        NotifyService paymentService = new NotifyService(notifyRepository);
        CoffeeStoreService coffeeStoreService = new CoffeeStoreService(coffeeStoreRepository);

        //TODO: いつかはDBから取るようにしたい
        CoffeeStore store = coffeeStoreService.load(new StoreId(storeId));

        try {
            Payment payment = delegateService.invoke(new ShopperId(userId), new TokenId(tokenId), store, new Money(amount));
            paymentService.notify(payment);
            return Uni.createFrom().item(payment);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @GET
    @Path("/user/{userId}")
    @Produces(MediaType.TEXT_PLAIN)
    public Uni<String> userInfo(@PathParam("userId") int userId) {
        ShopperService shopperService = new ShopperService(shopperRepository);
        WalletService walletService = new WalletService(walletRepository);

        Shopper shopper = shopperService.load(new ShopperId(userId));
        Wallet wallet = walletService.load(shopper);
        return Uni.createFrom().item(String.format("%s さんの残高は %d 円です。オートチャージは %d 円に設定されています", shopper.getUserName().value, wallet.getChargedMoney().value, wallet.getAutoChargeMoney().value));
    }

    @Inject
    Template top;

    @Inject
    Template createToken;

    @Inject
    Template cashRegister;

    @Inject
    Template billingCompleted;

    @Inject
    Template viewToken;

    @Inject
    Template error;

    @GET
    @Path("/index")
    @Produces(MediaType.TEXT_HTML)
    public Uni<String> top() {
        return Uni.createFrom().completionStage(() -> top.instance().renderAsync());
    }

    /**
     * UI用のトークン作成
     *
     * @param userId
     * @return
     */
    @POST
    @Path("/createToken/{userId}")
    @Produces(MediaType.TEXT_HTML)
    public Uni<String> createTokenUI(@PathParam("userId") @DefaultValue("1") final int userId) {
        try {
            TokenService tokenService = new TokenService(tokenRepository);
            Token token = tokenService.create(new ShopperId(userId));
            return Uni.createFrom().completionStage(() -> viewToken.data("token", token).data("payment", null).data("reload", true).renderAsync());
        } catch (Exception e) {
            e.printStackTrace();
            return Uni.createFrom().completionStage(() -> error.data("message", "トークンの表示に失敗しました").data("detail", e.getMessage()).renderAsync());
        }
    }

    @GET
    @Path("/viewToken/{shopperId}/{tokenId}")
    @Produces(MediaType.TEXT_HTML)
    public Uni<String> viewTokenUI(@PathParam("shopperId") final int shopperId, @PathParam("tokenId") final String tokenId) {
        ShopperId sId = new ShopperId(shopperId);
        TokenId tId = new TokenId(tokenId);
        try {
            TokenService tokenService = new TokenService(tokenRepository);
            Token token = tokenService.load(sId, tId);

            PaymentService paymentService = new PaymentService(paymentRepository);
            Payment payment = paymentService.load(sId, tId);
            return Uni.createFrom().completionStage(() -> viewToken.data("token", token).data("payment", payment).data("reload", false).renderAsync());
        } catch (TokenException e) {
            e.printStackTrace();
            return Uni.createFrom().completionStage(() -> error.data("message", "トークンの読み込みに失敗しました").data("detail", e.getMessage()).renderAsync());
        }
    }

    /*
     * レジ側の画面
     */
    @GET
    @Path("/cashRegister")
    @Produces(MediaType.TEXT_HTML)
    public Uni<String> viewCashRegister() {
        try {
            return Uni.createFrom().completionStage(() -> cashRegister.instance().renderAsync());
        } catch (Exception e) {
            e.printStackTrace();
            return Uni.createFrom().completionStage(() -> error.data("message", "レジの表示に失敗しました").data("detail", e.getMessage()).renderAsync());
        }
    }

    @POST
    @Path("/bill")
    @Produces(MediaType.TEXT_HTML)
    public Uni<String> bill(@FormParam("shopperId") final int userId, @FormParam("tokenId") final String tokenId, @FormParam("storeId") @DefaultValue("1") final int storeId, @FormParam("amount") int amount) {
        try {
            DelegateService delegateService = new DelegateService(delegateRepository);
            NotifyService paymentService = new NotifyService(notifyRepository);
            CoffeeStoreService coffeeStoreService = new CoffeeStoreService(coffeeStoreRepository);
            CoffeeStore store = coffeeStoreService.load(new StoreId(storeId));

            Payment payment = delegateService.invoke(new ShopperId(userId), new TokenId(tokenId), store, new Money(amount));
            if (payment == null) {
                return Uni.createFrom().completionStage(() -> error.data("message", "決済処理に失敗しました").data("detail", "").renderAsync());
            } else {
                paymentService.notify(payment);
                return Uni.createFrom().completionStage(() -> billingCompleted.data("tokenId", payment.getTokenId().value).data("amount", payment.getBillingAmount().value).data("date", payment.getBillingDateTime()).renderAsync());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Uni.createFrom().completionStage(() -> error.data("message", "決済処理に失敗しました").data("detail", e.getMessage()).renderAsync());
        }

    }


}