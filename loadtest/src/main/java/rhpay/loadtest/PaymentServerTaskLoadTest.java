package rhpay.loadtest;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.Configuration;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.commons.configuration.XMLStringConfiguration;
import org.infinispan.commons.marshall.ProtoStreamMarshaller;
import rhpay.payment.cache.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class SingleUserPaymentLoadTest {

    private static final String TOKEN_CACHE_NAME = "token";
    private static final String WALLET_CACHE_NAME = "wallet";
    private static final String PAYMENT_CACHE_NAME = "payment";
    private static final String SHOPPER_CACHE_NAME = "user";

    public static void main(String... args) throws Exception {

        if (args.length == 3) {
            System.out.println("");
            System.exit(1);
        }

        final int userNum = Integer.parseInt(args[0]);
        final int paymentNumPerUser = Integer.parseInt(args[1]);
        final int parallelNum = Integer.parseInt(args[2]);
        final int totalPaymentNum = userNum * paymentNumPerUser;

        System.out.println("+++++++++++++++++++++++++++++++++++++++++++++");

        Configuration configuration = createConfiguration();

        RemoteCacheManager manager = new RemoteCacheManager(configuration);

        RemoteCache<TokenKey, TokenEntity> tokenCache = manager.administration().getOrCreateCache(TOKEN_CACHE_NAME,
                new XMLStringConfiguration(String.format(TX_CACHE_CONFIG, TOKEN_CACHE_NAME)));
        RemoteCache<ShopperKey, WalletEntity> walletCache = manager.administration().getOrCreateCache(WALLET_CACHE_NAME,
                new XMLStringConfiguration(String.format(TX_CACHE_CONFIG, WALLET_CACHE_NAME)));
        RemoteCache<ShopperKey, WalletEntity> paymentCache = manager.administration().getOrCreateCache(PAYMENT_CACHE_NAME,
                new XMLStringConfiguration(String.format(TX_CACHE_CONFIG, PAYMENT_CACHE_NAME)));
        RemoteCache<ShopperKey, ShopperEntity> shopperCache = manager.administration().getOrCreateCache(SHOPPER_CACHE_NAME,
                new XMLStringConfiguration(String.format(NON_TX_CACHE_CONFIG, SHOPPER_CACHE_NAME)));

        // データを削除
        walletCache.clear();
        tokenCache.clear();
        paymentCache.clear();
        shopperCache.clear();

        // ユーザに関わるデータを作成
        for (int i = 0; i < userNum; i++) {
            ShopperKey key = new ShopperKey(i);
            shopperCache.put(key, new ShopperEntity("user" + i));
            walletCache.put(key, new WalletEntity(paymentNumPerUser, 0));
        }

        // 各スレッドに渡すトークンのListを作成
        List<TokenKey>[] tokenLists = new List[parallelNum];
        for (int i = 0; i < parallelNum; i++) {
            int previousLastPaymentNo = (i * totalPaymentNum) / parallelNum;
            int currentLastPaymentNo = ((i + 1) * totalPaymentNum) / parallelNum;
            int allocatedPaymentNum = currentLastPaymentNo - previousLastPaymentNo;
            tokenLists[i] = new ArrayList(allocatedPaymentNum);
        }

        // トークンを作成して、各スレッドへ渡す準備
        for (int i = 0; i < userNum; i++) {
            for (int j = 0; j < paymentNumPerUser; j++) {
                int tokenId = i * paymentNumPerUser + j;
                //Tokenをキャッシュする
                TokenKey key = new TokenKey(userNum, String.valueOf(tokenId));
                tokenCache.put(key, new TokenEntity(TokenStatus.UNUSED));
                //Tokenを処理へ渡す
                tokenLists[tokenId / paymentNumPerUser].add(key);
            }
        }

        System.out.println(String.format("Cached Token number is %d", tokenCache.size()));
        System.out.println(String.format("Unused cached Token number is %d", tokenCache.entrySet().stream().filter(e -> (e.getValue().getStatus().equals(TokenStatus.UNUSED))).count()));

        // 負荷をかけるスレッドを作成
        final LinkedBlockingQueue<Runnable> rushTaskQueue = new LinkedBlockingQueue<>(parallelNum);
        final RejectedExecutionHandler rushTaskHandler = new ThreadPoolExecutor.DiscardOldestPolicy();
        final ExecutorService executorService = new ThreadPoolExecutor(parallelNum, parallelNum,
                0L, TimeUnit.MILLISECONDS, rushTaskQueue,
                rushTaskHandler);

        // 処理するタスクを作成
        final AtomicInteger counter = new AtomicInteger(0);
        PaymentLoader[] loaders = new PaymentLoader[parallelNum];
        for (int i = 0; i < parallelNum; i++) {
            loaders[i] = new PaymentLoader(tokenLists[i], manager.getCache(WALLET_CACHE_NAME), counter);
        }

        // 負荷をかける
        long start = System.currentTimeMillis();
        for (PaymentLoader task : loaders) {
            executorService.execute(task);
        }

        // クライアントからの負荷を全部処理し終わるのを待つ
        while (counter.get() != parallelNum) {
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
        System.out.println(String.format("throughput %f process/ms", ((double) totalPaymentNum / (double) (end - start))));

        System.out.println("After rushing, wallet : ");
        for (int i = 0; i < userNum; i++) {
            System.out.println(walletCache.get(new ShopperKey(i)));
        }

        boolean hasError = false;
        System.out.println("エラーが発生したトークン");
        for (PaymentLoader task : loaders) {
            if (task.currentToken != null) {
                hasError = true;
                System.out.println(String.format("Token : [%s], [%s]", task.currentToken, tokenCache.get(task.currentToken)));
            }
        }
        if (hasError == false) {
            System.out.println("なし");
        }

        // 事後処理
        executorService.shutdownNow();
        walletCache.clear();
        tokenCache.clear();
        shopperCache.clear();
        paymentCache.clear();
        manager.close();
    }

    private static final String hostname = "127.0.0.1";
    private static final int port = 11222;
    private static final boolean isAuth = false;
    private static final String user = "admin";
    private static final String password = "password";

    private static final String NON_TX_CACHE_CONFIG =
            "<distributed-cache name=\"%s\">"
                    + " <encoding media-type=\"application/x-protostream\"/>"
                    + " <groups enabled=\"true\"/>"
                    + "</distributed-cache>";

    private static final String TX_CACHE_CONFIG =
            "<distributed-cache name=\"%s\">"
                    + " <encoding media-type=\"application/x-protostream\"/>"
                    + " <groups enabled=\"true\"/>"
                    + "<locking concurrency-level=\"1000\" acquire-timeout=\"15000\" striping=\"false\"/>"
                    + "<transaction mode=\"BATCH\" locking=\"PESSIMISTIC\"/>"
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

    private final AtomicInteger counter;
    private final List<TokenKey> tokenList;
    private final RemoteCache<ShopperKey, WalletEntity> walletCache;
    public final List<TokenKey> completedTokenList;
    public volatile TokenKey currentToken;

    public PaymentLoader(List<TokenKey> tokenList, RemoteCache<ShopperKey, WalletEntity> walletCache, AtomicInteger counter) {
        this.tokenList = tokenList;
        this.walletCache = walletCache;
        this.completedTokenList = new ArrayList<>(tokenList.size());
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

            // 所有しているトークンを連続で使用する
            for (TokenKey t : tokenList) {

                currentToken = t;

                payInfo.put("tokenId", t.getTokenId());
                payInfo.put("shopperId", t.getOwnerId());

                walletCache.execute("PaymentTask", payInfo, new ShopperKey(t.getOwnerId()));
                completedTokenList.add(t);
            }
            // 全部終わったら処理中のトークンをnullにする
            currentToken = null;
        } catch (Exception e) {
            throw e;
        } finally {
            // 負荷かけが終わったら完了タスク数を1増やす
            counter.incrementAndGet();
        }
    }
}