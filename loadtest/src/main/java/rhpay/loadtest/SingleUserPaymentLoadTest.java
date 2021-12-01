package rhpay.loadtest;

import rhpay.payment.domain.ShopperId;
import rhpay.payment.domain.Token;
import rhpay.payment.domain.TokenId;
import rhpay.payment.domain.TokenStatus;
import rhpay.payment.cache.ShopperKey;
import rhpay.payment.cache.TokenEntity;
import rhpay.payment.cache.TokenKey;
import rhpay.payment.cache.WalletEntity;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.Configuration;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.commons.configuration.XMLStringConfiguration;
import org.infinispan.commons.marshall.ProtoStreamMarshaller;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class SingleUserPaymentLoadTest {

    public static final int rushThreadNum = 10;
    public static final int tokenNum = 100_000;

    private static final String TOKEN_CACHE_NAME = "token";
    private static final String WALLET_CACHE_NAME = "wallet";
    private static final String PAYMENT_CACHE_NAME = "payment";

    public static final List<Token>[] tokenLists = new List[rushThreadNum];

    public static void main(String... args) throws Exception {
        System.out.println("+++++++++++++++++++++++++++++++++++++++++++++");

        Configuration configuration = createConfiguration();

        RemoteCacheManager manager = new RemoteCacheManager(configuration);

        RemoteCache<TokenKey, TokenEntity> tokenCache = manager.administration().getOrCreateCache(TOKEN_CACHE_NAME,
                new XMLStringConfiguration(String.format(CACHE_CONFIG, TOKEN_CACHE_NAME)));
        RemoteCache<ShopperKey, WalletEntity> walletCache = manager.administration().getOrCreateCache(WALLET_CACHE_NAME,
                new XMLStringConfiguration(String.format(CACHE_CONFIG, WALLET_CACHE_NAME)));
        RemoteCache<ShopperKey, WalletEntity> paymentCache = manager.administration().getOrCreateCache(PAYMENT_CACHE_NAME,
                new XMLStringConfiguration(String.format(CACHE_CONFIG, PAYMENT_CACHE_NAME)));
        walletCache.clear();
        tokenCache.clear();
        paymentCache.clear();

        final ShopperId shopperId = new ShopperId(1);

        walletCache.put(new ShopperKey(shopperId.value), new WalletEntity(tokenNum, 0));
        System.out.println(walletCache.get(new ShopperKey(shopperId.value)));

        // 各スレッドに渡すトークンのListを作成
        List<Token>[] tokenLists = new List[rushThreadNum];
        for (int i = 0; i < rushThreadNum; i++) {
            tokenLists[i] = new ArrayList(tokenNum / rushThreadNum);
        }

        // トークンを作成して、各スレッドへ渡す準備
        for (int i = 0; i < tokenNum; i++) {
            //Tokenを作る
            Token token = new Token(shopperId, new TokenId(String.valueOf(i)), TokenStatus.UNUSED);
            ;
            //Tokenをキャッシュする
            TokenKey key = new TokenKey(token.getShopperId().value, token.getTokenId().value);
            tokenCache.put(key, new TokenEntity(rhpay.payment.cache.TokenStatus.UNUSED));
            //Tokenを処理へ渡す
            tokenLists[i % rushThreadNum].add(token);
        }

        System.out.println(String.format("Cached Token number is %d", tokenCache.size()));
        System.out.println(String.format("Unused cached Token number is %d", tokenCache.entrySet().stream().filter(e -> (e.getValue().getStatus().equals(rhpay.payment.cache.TokenStatus.UNUSED))).count()));

        // 負荷をかけるスレッドを作成
        final int rushTaskQueueSize = rushThreadNum;
        final LinkedBlockingQueue<Runnable> rushTaskQueue = new LinkedBlockingQueue<>(rushTaskQueueSize);
        final RejectedExecutionHandler rushTaskHandler = new ThreadPoolExecutor.DiscardOldestPolicy();
        final ExecutorService executorService = new ThreadPoolExecutor(rushThreadNum, rushThreadNum,
                0L, TimeUnit.MILLISECONDS, rushTaskQueue,
                rushTaskHandler);

        // 処理するタスクを作成
        final Counter counter = new Counter();
        PaymentLoader[] loaders = new PaymentLoader[rushThreadNum];
        for (int i = 0; i < rushThreadNum; i++) {
            loaders[i] = new PaymentLoader(tokenLists[i], manager.getCache(TOKEN_CACHE_NAME), counter);
        }

        // 負荷をかける
        long start = System.currentTimeMillis();
        for (PaymentLoader task : loaders) {
            executorService.execute(task);
        }

        // クライアントからの負荷を全部処理し終わるのを待つ
        while (counter.get() != rushThreadNum) {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        long end = System.currentTimeMillis();

        System.out.println("結果");
        System.out.println("*****************************************");

        // 終了したトークン数を数えて出力
        int completedTokenNum = 0;
        for (PaymentLoader task : loaders) {
            completedTokenNum += task.completedTokenList.size();
        }
        System.out.println(String.format("終了したトークン数 %d", completedTokenNum));

        System.out.println("After rushing, wallet : " + walletCache.get(new ShopperKey(shopperId.value)));
        System.out.println(String.format("throughput %f process/ms", (double) ((double) tokenNum / (double) (end - start))));

        boolean hasError = false;
        System.out.println("エラーが発生したトークン");
        for (PaymentLoader task : loaders) {
            if (task.currentToken != null) {
                hasError = true;
                TokenKey key = new TokenKey(task.currentToken.getShopperId().value, task.currentToken.getTokenId().value);
                System.out.println(String.format("Token : [%s], [%s]", key, tokenCache.get(key)));
            }
        }
        if (hasError == false) {
            System.out.println("なし");
        }

        // 事後処理
        executorService.shutdownNow();
        walletCache.clear();
        tokenCache.clear();
        manager.close();
    }

    private static final String hostname = "127.0.0.1";
    private static final int port = 11222;
    private static final boolean isAuth = false;
    private static final String user = "admin";
    private static final String password = "password";

    private static final String CACHE_CONFIG =
            "<distributed-cache name=\"%s\" mode=\"SYNC\" remote-timeout=\"17500\">"
                    + " <encoding media-type=\"application/x-protostream\"/>"
                    + " <groups enabled=\"true\"/>"
//                    + "<locking/>"
//                    + "<locking concurrency-level=\"32\" acquire-timeout=\"1000000\" striping=\"false\"/>"
//                    + "<state-transfer timeout=\"1000000\"/>"
//                    + "<transaction auto-commit=\"false\" mode=\"BATCH\" notifications=\"false\" transaction-manager-lookup=\"org.infinispan.transaction.lookup.EmbeddedTransactionManagerLookup\"/>"
                    + "</distributed-cache>";

    private static Configuration createConfiguration() throws Exception {

        ConfigurationBuilder cb
                = new ConfigurationBuilder();
        cb.marshaller(new ProtoStreamMarshaller())
                .statistics()
                .enable()
                .jmxDomain("org.infinispan")
                .addServer()
                .host(hostname)
                .port(port)
                .addContextInitializer("rhpay.loadtest.system.PaymentSchemaImpl")
                // デフォルトは10
                .asyncExecutorFactory().addExecutorProperty("infinispan.client.hotrod.default_executor_factory.pool_size", "10")
                //デフォルトは100,000
                .addExecutorProperty("infinispan.client.hotrod.default_executor_factory.queue_size", "100000");

        if (isAuth) {
            cb.security()
                    .authentication()
                    .realm("default")
                    .saslMechanism("DIGEST-MD5")
                    .username(user)
                    .password(password);
        }

        return cb.build();
    }


}

class PaymentLoader implements Runnable {

    private final Counter counter;
    private final List<Token> tokenList;
    private final RemoteCache<TokenKey, TokenEntity> tokenCache;
    public final List<Token> completedTokenList;
    public volatile Token currentToken;

    public PaymentLoader(List<Token> tokenList, RemoteCache<TokenKey, TokenEntity> tokenCache, Counter counter) {
        this.tokenList = tokenList;
        this.tokenCache = tokenCache;
        completedTokenList = new ArrayList<>(tokenList.size());
        this.counter = counter;
    }

    @Override
    public void run() {
        try {
            //処理のパラメータを作成
            Map<String, Object> payInfo = new HashMap<>(5);
            payInfo.put("amount", 1);
            payInfo.put("storeId", 1);
            payInfo.put("storeName", "");

            for (Token t : tokenList) {

                currentToken = t;

                payInfo.put("tokenId", t.getTokenId().value);
                payInfo.put("shopperId", t.getShopperId().value);

                tokenCache.execute("PaymentTask", payInfo, new TokenKey(t.getShopperId().value, t.getTokenId().value));
                completedTokenList.add(t);
            }
            // 全部終わったら処理中のトークンをnullにする
            currentToken = null;
        } catch (Exception e) {
            throw e;
        } finally {
            // 負荷かけが終わったら完了タスク数を1増やす
            counter.increment();
        }
    }
}

class Counter {
    private final AtomicInteger counter = new AtomicInteger(0);

    public void increment() {
        counter.incrementAndGet();
    }

    public int get() {
        return counter.get();
    }
}