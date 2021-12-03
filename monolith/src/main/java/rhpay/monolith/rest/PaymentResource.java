package rhpay.monolith.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import rhpay.payment.domain.*;
import rhpay.payment.repository.*;
import rhpay.payment.service.*;
import rhpay.point.domain.Point;
import rhpay.point.repository.PointRepository;
import rhpay.point.service.PointService;

@RestController
public class PaymentResource {

    private TokenRepository tokenRepository;
    private CoffeeStoreRepository coffeeStoreRepository;
    private PointRepository pointRepository;
    private ShopperRepository shopperRepository;
    private PaymentRepository paymentRepository;
    private WalletRepository walletRepository;

    public PaymentResource(TokenRepository tokenRepository, CoffeeStoreRepository coffeeStoreRepository, PointRepository pointRepository, ShopperRepository shopperRepository, PaymentRepository paymentRepository, WalletRepository walletRepository) {
        this.tokenRepository = tokenRepository;
        this.coffeeStoreRepository = coffeeStoreRepository;
        this.pointRepository = pointRepository;
        this.shopperRepository = shopperRepository;
        this.paymentRepository = paymentRepository;
        this.walletRepository = walletRepository;
    }

    @PostMapping(value = "/pay/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Token createTokenAPI(@PathVariable("userId") final int userId) {
        TokenService tokenService = new TokenService(tokenRepository);
        Token token = tokenService.create(new ShopperId(userId));
        return token;
    }

    @PostMapping(value = "/pay/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Payment pay(@PathVariable("userId") final int userIdInt, @PathVariable("tokenId") final String tokenIdStr, @PathVariable("storeId") final int storeIdInt, @PathVariable("amount") int amountInt) throws Exception {
        TokenService tokenService = new TokenService(tokenRepository);
        CoffeeStoreService coffeeStoreService = new CoffeeStoreService(coffeeStoreRepository);
        PointService pointService = new PointService(pointRepository);
        ShopperService shopperService = new ShopperService(shopperRepository);
        PaymentService paymentService = new PaymentService(paymentRepository);
        WalletService walletService = new WalletService(walletRepository);

        ShopperId shopperId = new ShopperId(userIdInt);
        Money amount = new Money(amountInt);
        TokenId tokenId = new TokenId(tokenIdStr);
        StoreId storeId = new StoreId(storeIdInt);

        Token token = null;

        try {

            // 買い物客と店舗の情報を読み込む
            Shopper shopper = shopperService.load(shopperId);
            CoffeeStore store = coffeeStoreService.load(storeId);
            Wallet wallet = walletService.load(shopper);

            // 請求する内容を作成
            Billing bill = store.createBill(amount);

            // トークンを読み込んで処理中にする
            token = tokenService.load(shopperId, tokenId);
            token = tokenService.processing(token);

            // 請求処理
            Payment payment = wallet.pay(bill, tokenId);

            // トークンを使用済みにする
            token = tokenService.used(token);

            // 支払い結果を格納する
            paymentService.store(payment);

            // ポイントを加算する
            Point point = pointService.load(shopperId);
            point.addPoint(payment.getBillingAmount());
            pointService.store(point);

            return payment;
        } catch (Exception e) {
            PaymentException thrw = new PaymentException("Fail to pay");
            if (token == null) {
                thrw = new PaymentException(String.format("This token is not exist : [%s, %s]", shopperId, tokenId));
            } else {
                tokenService.failed(token);
            }
            thrw.addSuppressed(e);
            thrw.printStackTrace();
            throw thrw;
        }
    }
}
