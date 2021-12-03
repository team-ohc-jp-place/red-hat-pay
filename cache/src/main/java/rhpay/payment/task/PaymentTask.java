package rhpay.payment.task;

import rhpay.monitoring.TaskEvent;
import rhpay.payment.repository.*;
import rhpay.payment.domain.*;
import rhpay.payment.service.BillingService;
import jdk.jfr.Event;
import org.infinispan.Cache;
import org.infinispan.tasks.ServerTask;
import org.infinispan.tasks.TaskContext;
import rhpay.payment.cache.*;
import rhpay.payment.service.PaymentService;
import rhpay.payment.service.ShopperService;
import rhpay.payment.service.TokenService;

import java.time.ZoneOffset;
import java.util.Map;

/**
 * 支払時のサーバタスク
 */
public class PaymentTask implements ServerTask<PaymentResponse> {

    private final ThreadLocal<PaymentTaskParameter> parameterThreadLocal = new ThreadLocal<>();

    @Override
    public void setTaskContext(TaskContext taskContext) {

        // パラメータからドメインのオブジェクトを作成
        Map<String, ?> param = taskContext.getParameters().get();
        final ShopperId shopperId = new ShopperId((Integer) param.get("shopperId"));
        final TokenId tokenId = new TokenId((String) param.get("tokenId"));
        final Money amount = new Money((Integer) param.get("amount"));
        final StoreId storeId = new StoreId((Integer) param.get("storeId"));
        final StoreName storeName = new StoreName((String) param.get("storeName"));
        final CoffeeStore store = new CoffeeStore(storeId, storeName);

        // コンテキストから取得したキャッシュからレポジトリとサービスを作成
        Cache<TokenKey, TokenEntity> tokenCache = (Cache<TokenKey, TokenEntity>) taskContext.getCache().get();
        Cache<ShopperKey, WalletEntity> walletCache = taskContext.getCacheManager().getCache("wallet");
        Cache<TokenKey, PaymentEntity> paymentCache = taskContext.getCacheManager().getCache("payment");
        Cache<ShopperKey, ShopperEntity> shopperCache = taskContext.getCacheManager().getCache("user");

        BillingRepository billingRepository = new CacheBillingRepository(walletCache);
        BillingService billingService = new BillingService(billingRepository);
        ShopperRepository shopperRepository = new CacheShopperRepository(shopperCache);
        ShopperService shopperService = new ShopperService(shopperRepository);
        TokenRepository tokenRepository = new CacheTokenRepository(tokenCache);
        TokenService tokenService = new TokenService(tokenRepository);
        PaymentRepository paymentRepository = new CachePaymentRepository(paymentCache);
        PaymentService paymentService = new PaymentService(paymentRepository);

                // このクラスはスレッドセーフでは無いため、パラメータをスレッドローカルへ保持する
        PaymentTaskParameter parameter = new PaymentTaskParameter(shopperId, tokenId, amount, store, billingService, shopperService, tokenService, paymentService);
        parameterThreadLocal.set(parameter);
    }

    @Override
    public PaymentResponse call() throws Exception {

        // パラメータを取得する
        PaymentTaskParameter parameter = parameterThreadLocal.get();

        // JFRのイベントを開始する
        Event taskEvent = new TaskEvent("PaymentTask", parameter.shopperId.value, parameter.tokenId.value);
        taskEvent.begin();
        Token token = null;

        // ドメインのオブジェクトを取得
        final ShopperId shopperId = parameter.shopperId;
        final TokenId tokenId = parameter.tokenId;
        final Money amount = parameter.amount;
        final CoffeeStore store = parameter.store;
        final Billing bill = store.createBill(amount);
        final BillingService billingService = parameter.billingService;
        final TokenService tokenService = parameter.tokenService;
        final PaymentService paymentService = parameter.paymentService;

        try {

            // 買い物客の情報を読み込む
            Shopper shopper = parameter.shopperService.load(shopperId);

            // トークンを読み込んで処理中にする
            token = tokenService.load(shopperId, tokenId);
            token = tokenService.processing(token);

            // 請求処理
            final Payment payment = billingService.bill(shopper, tokenId, bill);

            // トークンを使用済みにする
            token = tokenService.used(token);

            // 支払い結果を格納する
            paymentService.store(payment);

            // アプリケーションへレスポンスを返す
            return new PaymentResponse(payment.getStoreId().value, payment.getShopperId().value, payment.getTokenId().value, payment.getBillingAmount().value, payment.getBillingDateTime().toEpochSecond(ZoneOffset.of("+09:00")));

        } catch (Exception e) {
            PaymentException thrw = new PaymentException("Fail to pay");
            if(token == null){
                thrw = new PaymentException(String.format("This token is not exist : [%s, %s]", shopperId, tokenId));
            } else {
                tokenService.failed(token);
            }
            thrw.addSuppressed(e);
            thrw.printStackTrace();
            throw thrw;
        } finally {
            taskEvent.commit();
            parameterThreadLocal.remove();
        }
    }

    @Override
    public String getName() {
        return "PaymentTask";
    }

}

/**
 * クライアントから送られてきたパラメータをスレッドローカルに一括して格納する為のクラス
 */
class PaymentTaskParameter {

    public final ShopperId shopperId;
    public final TokenId tokenId;
    public final Money amount;
    public final CoffeeStore store;
    public final BillingService billingService;
    public final ShopperService shopperService;
    public final TokenService tokenService;
    public final PaymentService paymentService;

    public PaymentTaskParameter(ShopperId shopperId, TokenId tokenId, Money amount, CoffeeStore store, BillingService billingService, ShopperService shopperService, TokenService tokenService, PaymentService paymentService) {
        this.shopperId = shopperId;
        this.tokenId = tokenId;
        this.amount = amount;
        this.store = store;
        this.billingService = billingService;
        this.shopperService = shopperService;
        this.tokenService = tokenService;
        this.paymentService = paymentService;
    }
}