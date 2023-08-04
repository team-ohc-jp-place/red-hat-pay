package rhpay.monolith.rest;

import jdk.jfr.Event;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import rhpay.monolith.monitoring.PaymentEvent;
import rhpay.monolith.monitoring.PointEvent;
import rhpay.payment.domain.*;
import rhpay.payment.repository.*;
import rhpay.payment.usecase.TokenPayInput;
import rhpay.payment.usecase.TokenUsecase;
import rhpay.point.domain.Point;
import rhpay.point.repository.PointRepository;
import rhpay.point.usecase.AddPointInput;
import rhpay.point.usecase.PointUsecase;

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
        TokenUsecase usecase = new TokenUsecase();
        Token token = usecase.createToken(new ShopperId(userId), tokenRepository);
        return new TokenResponse(token.getShopperId().value, token.getTokenId().value, token.getStatus());
    }

    @PostMapping(value = "/pay/{userId}/{tokenId}/{storeId}/{amount}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public PaymentResponse pay(@PathVariable("userId") final int userIdInt, @PathVariable("tokenId") final String tokenIdStr, @PathVariable("storeId") final int storeIdInt, @PathVariable("amount") int amountInt) throws Exception {

        // 入力情報をドメインの情報に変換
        ShopperId shopperId = new ShopperId(userIdInt);
        Money amount = new Money(amountInt);
        TokenId tokenId = new TokenId(tokenIdStr);
        StoreId storeId = new StoreId(storeIdInt);

        // メトリクスの準備
        Event paymentEvent = new PaymentEvent();
        Event pointEvent = null;

        try {
            // 請求処理
            paymentEvent.begin();

            TokenPayInput input = new TokenPayInput(tokenRepository, coffeeStoreRepository, shopperRepository, paymentRepository, walletRepository, shopperId, amount, tokenId, storeId);
            TokenUsecase usecase = new TokenUsecase();
            Payment payment = usecase.pay(input);

            paymentEvent.commit();

            // ポイント加算処理
            pointEvent = new PointEvent();
            pointEvent.begin();

            AddPointInput addPointInput = new AddPointInput(payment, pointRepository);
            PointUsecase pointUsecase = new PointUsecase();
            Point point = pointUsecase.addPoint(addPointInput);

            pointEvent.commit();

            // ドメインの情報を出力情報に変換
            PaymentResponse res = new PaymentResponse(payment.getStoreId().value, payment.getShopperId().value, payment.getTokenId().value, payment.getBillingAmount().value, payment.getBillingDateTime());

            return res;

        } finally {
            // 例外発生時にメトリクスを正常に終了させる
            if (pointEvent == null) {
                if (paymentEvent.shouldCommit()) {
                    paymentEvent.commit();
                }
            } else {
                if (pointEvent.shouldCommit()) {
                    pointEvent.commit();
                }
            }
        }

    }
}
