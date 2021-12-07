package rhpay.monolith.rest;

import jdk.jfr.Event;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import rhpay.monolith.monitoring.PaymentEvent;
import rhpay.monolith.monitoring.PointEvent;
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
    @Transactional
    public TokenResponse createTokenAPI(@PathVariable("userId") final int userId) {
        TokenService tokenService = new TokenService(tokenRepository);
        Token token = tokenService.create(new ShopperId(userId));
        TokenResponse res = new TokenResponse(token.getShopperId().value, token.getTokenId().value, token.getStatus());
        return res;
    }

    @PostMapping(value = "/pay/{userId}/{tokenId}/{storeId}/{amount}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public PaymentResponse pay(@PathVariable("userId") final int userIdInt, @PathVariable("tokenId") final String tokenIdStr, @PathVariable("storeId") final int storeIdInt, @PathVariable("amount") int amountInt) throws Exception {
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
        Event paymentEvent = new PaymentEvent();
        Event pointEvent = null;

        try {
            paymentEvent.begin();
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

            // 財布を更新
            walletService.store(wallet);

            // トークンを使用済みにする
            token = tokenService.used(token);

            // 支払い結果を格納する
            paymentService.store(payment);

            paymentEvent.commit();
            pointEvent = new PointEvent();
            pointEvent.begin();

            // ポイントを加算する
            Point point = pointService.load(shopperId);
            point.addPoint(payment.getBillingAmount());
            pointService.store(point);

            pointEvent.commit();

            PaymentResponse res = new PaymentResponse(payment.getStoreId().value, payment.getShopperId().value, payment.getTokenId().value, payment.getBillingAmount().value, payment.getBillingDateTime());

            return res;
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
        } finally {
            if (pointEvent == null) {
                paymentEvent.commit();
            } else {
                pointEvent.commit();
            }
        }
    }
}
