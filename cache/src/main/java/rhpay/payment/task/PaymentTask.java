package rhpay.payment.task;

import rhpay.monitoring.TaskEvent;
import rhpay.payment.repository.BillingRepository;
import rhpay.payment.domain.*;
import rhpay.payment.repository.CacheBillingRepository;
import rhpay.payment.repository.CacheShopperRepository;
import rhpay.payment.repository.ShopperRepository;
import rhpay.payment.service.BillingService;
import jdk.jfr.Event;
import org.infinispan.Cache;
import org.infinispan.tasks.ServerTask;
import org.infinispan.tasks.TaskContext;
import rhpay.payment.cache.*;
import rhpay.payment.service.ShopperService;

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

        BillingRepository billingRepository = new CacheBillingRepository(tokenCache, walletCache, paymentCache);
        BillingService billingService = new BillingService(billingRepository);
        ShopperRepository shopperRepository = new CacheShopperRepository(shopperCache);
        ShopperService shopperService = new ShopperService(shopperRepository);

        // このクラスはスレッドセーフでは無いため、パラメータをスレッドローカルへ保持する
        PaymentTaskParameter parameter = new PaymentTaskParameter(shopperId, tokenId, amount, store, billingService, shopperService);
        parameterThreadLocal.set(parameter);
    }

    @Override
    public PaymentResponse call() throws Exception {

        // パラメータを取得する
        PaymentTaskParameter parameter = parameterThreadLocal.get();

        // JFRのイベントを開始する
        Event taskEvent = new TaskEvent("PaymentTask", parameter.shopperId.value, parameter.tokenId.value);
        taskEvent.begin();

        try {
            // ドメインのオブジェクトを取得
            final ShopperId shopperId = parameter.shopperId;
            final TokenId tokenId = parameter.tokenId;
            final Money amount = parameter.amount;
            final CoffeeStore store = parameter.store;
            final Billing bill = store.createBill(amount);

            Shopper shopper = parameter.shopperService.load(shopperId);

            // 請求処理
            BillingService billingService = parameter.billingService;
            final Payment payment = billingService.bill(shopper, tokenId, bill);

            // レスポンスを返す
            return new PaymentResponse(payment.getStoreId().value, payment.getShopperId().value, payment.getTokenId().value, payment.getBillingAmount().value, payment.getBillingDateTime().toEpochSecond(ZoneOffset.of("+09:00")));
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
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

    public PaymentTaskParameter(ShopperId shopperId, TokenId tokenId, Money amount, CoffeeStore store, BillingService billingService, ShopperService shopperService) {
        this.shopperId = shopperId;
        this.tokenId = tokenId;
        this.amount = amount;
        this.store = store;
        this.billingService = billingService;
        this.shopperService = shopperService;
    }
}