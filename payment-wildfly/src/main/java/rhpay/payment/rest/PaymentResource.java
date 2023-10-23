package rhpay.payment.rest;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import rhpay.payment.domain.*;
import rhpay.payment.usecase.TokenPayInput;
import rhpay.payment.usecase.TokenUsecase;

@Path("/")
public class PaymentResource {

    @Inject
    private TokenUsecase usecase;

    @POST
    @Path("/pay/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    public TokenResponse createTokenAPI(@PathParam("userId") final int userId) {

        Token token = usecase.createToken(new ShopperId(userId));

        TokenResponse res = new TokenResponse(token.getShopperId().value, token.getTokenId().value, token.getStatus());
        return res;
    }

    @POST
    @Path("/pay/{userId}/{tokenId}/{storeId}/{amount}")
    @Produces(MediaType.APPLICATION_JSON)
    public PaymentResponse pay(@PathParam("userId") final int userIdInt, @PathParam("tokenId") final String tokenIdStr, @PathParam("storeId") final int storeIdInt, @PathParam("amount") int amountInt) throws PaymentException {

        // 入力情報をドメインの情報に変換
        ShopperId shopperId = new ShopperId(userIdInt);
        Money amount = new Money(amountInt);
        TokenId tokenId = new TokenId(tokenIdStr);
        StoreId storeId = new StoreId(storeIdInt);

        TokenPayInput input = new TokenPayInput(shopperId, amount, tokenId, storeId);
        Payment payment = usecase.pay(input);

        //ポイント加算処理
//        AddPointInput addPointInput = new AddPointInput(payment, pointRepository);
//        PointUsecase pointUsecase = new PointUsecase();
//        Point point = pointUsecase.addPoint(addPointInput);

        // ドメインの情報を出力情報に変換
        PaymentResponse res = new PaymentResponse(payment.getStoreId().value, payment.getShopperId().value, payment.getTokenId().value, payment.getBillingAmount().value, payment.getBillingDateTime());

        return res;
    }

//    @GET
//    @Path("/user/{userId}")
//    @Produces(MediaType.TEXT_PLAIN)
//    public String userInfo(@PathParam("userId") int userId) {
//        ShopperService shopperService = new ShopperService(shopperRepository);
//        WalletService walletService = new WalletService(walletRepository);
//
//        Shopper shopper = shopperService.load(new ShopperId(userId));
//        Wallet wallet = walletService.load(shopper);
//        return String.format("%s さんの残高は %d 円です。オートチャージは %d 円に設定されています", shopper.getUserName().value, wallet.getChargedMoney().value, wallet.getAutoChargeMoney().value);
//    }
}