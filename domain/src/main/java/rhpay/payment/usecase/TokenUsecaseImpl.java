package rhpay.payment.usecase;

import rhpay.payment.domain.*;
import rhpay.payment.repository.*;
import rhpay.payment.service.*;

public class TokenUsecaseImpl implements TokenUsecase {

    TokenRepository tokenRepository;

    CoffeeStoreRepository coffeeStoreRepository;

    ShopperRepository shopperRepository;

    PaymentRepository paymentRepository;

    WalletRepository walletRepository;

    public TokenUsecaseImpl(TokenRepository tokenRepository, CoffeeStoreRepository coffeeStoreRepository, ShopperRepository shopperRepository, PaymentRepository paymentRepository, WalletRepository walletRepository) {
        this.tokenRepository = tokenRepository;
        this.coffeeStoreRepository = coffeeStoreRepository;
        this.shopperRepository = shopperRepository;
        this.paymentRepository = paymentRepository;
        this.walletRepository = walletRepository;
    }

    public Token createToken(ShopperId shopperId) {
        TokenService tokenService = new TokenService(this.tokenRepository);
        Token token = tokenService.create(shopperId);
        return token;
    }

    public Payment pay(TokenPayInput input) throws PaymentException {

        TokenService tokenService = new TokenService(this.tokenRepository);
        CoffeeStoreService coffeeStoreService = new CoffeeStoreService(this.coffeeStoreRepository);
        ShopperService shopperService = new ShopperService(this.shopperRepository);
        PaymentService paymentService = new PaymentService(this.paymentRepository);
        WalletService walletService = new WalletService(this.walletRepository);

        Token token = null;

        try {
            // 買い物客と店舗の情報を読み込む
            Shopper shopper = shopperService.load(input.shopperId);
            CoffeeStore store = coffeeStoreService.load(input.storeId);
            Wallet wallet = walletService.load(shopper);

            // 請求する内容を作成
            Billing bill = store.createBill(input.amount);

            // トークンを読み込む
            token = tokenService.load(input.shopperId, input.tokenId);
            if (token == null) {
                throw new RuntimeException(String.format("This token is not exist : [%s, %s]", input.shopperId, input.tokenId));
            }

            // トークンを処理中にする
            token = token.processing();
            tokenService.store(token);

            // 請求処理
            Payment payment = wallet.pay(bill);

            // 財布を更新
            walletService.store(wallet);

            // トークンを使用済みにする
            token = token.used(payment);
            tokenService.store(token);

            Payment relatedPayment = token.getRelatedPayment();
            // 支払い結果を格納する
            paymentService.store(relatedPayment);

            return relatedPayment;
        } catch (Exception e) {
            PaymentException thrw = new PaymentException("Fail to pay");
            if (token != null) {
                try {
                    token.failed();
                    tokenService.store(token);
                } catch (Exception e1) {
                    thrw.addSuppressed(e1);
                }
            }
            thrw.addSuppressed(e);
            thrw.printStackTrace();
            throw thrw;
        }
    }
}
