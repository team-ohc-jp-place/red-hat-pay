package rhpay.loadtest;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.Configuration;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.commons.configuration.XMLStringConfiguration;
import org.infinispan.commons.marshall.ProtoStreamMarshaller;
import rhpay.payment.cache.*;

import java.io.FileInputStream;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class PaymentServerTaskLoadTest {

    private static final String TOKEN_CACHE_NAME = "token";
    private static final String WALLET_CACHE_NAME = "wallet";
    private static final String PAYMENT_CACHE_NAME = "payment";
    private static final String SHOPPER_CACHE_NAME = "user";

    public static void main(String... args) throws Exception {

        if (args.length != 3) {
            System.out.println("アプリケーションの引数に <ユーザ数> <ユーザ毎の決済数> <並列度> を指定して下さい");
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
                new XMLStringConfiguration(String.format(TRANSACTIONAL_EXPIRED_CACHE_CONFIG, TOKEN_CACHE_NAME)));
        RemoteCache<ShopperKey, WalletEntity> walletCache = manager.administration().getOrCreateCache(WALLET_CACHE_NAME,
                new XMLStringConfiguration(String.format(TRANSACTIONAL_LOCKING_CACHE_CONFIG, WALLET_CACHE_NAME)));
        RemoteCache<ShopperKey, WalletEntity> paymentCache = manager.administration().getOrCreateCache(PAYMENT_CACHE_NAME,
                new XMLStringConfiguration(String.format(TRANSACTIONAL_EXPIRED_CACHE_CONFIG, PAYMENT_CACHE_NAME)));
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
                TokenKey key = new TokenKey(i, String.valueOf(tokenId));
                tokenCache.put(key, new TokenEntity(TokenStatus.UNUSED));
                //Tokenを処理へ渡す
                int assignedThreadNo = (tokenId * parallelNum) / totalPaymentNum;
                tokenLists[assignedThreadNo].add(key);
            }
        }

        System.out.println(String.format("Cached Token number is %d", tokenCache.size()));
        System.out.println(String.format("Unused cached Token number is %d", tokenCache.entrySet().stream().filter(e -> (e.getValue().getStatus().equals(TokenStatus.UNUSED))).count()));
        System.out.println("Number of tokens each thread has :");
        int threadNo = 0;
        for (List<TokenKey> t : tokenLists) {
            System.out.println(String.format("No %d thread has %d tokens", threadNo, t.size()));
            threadNo++;
        }

        // 負荷をかけるスレッドを作成
        final LinkedBlockingQueue<Runnable> rushTaskQueue = new LinkedBlockingQueue<>(parallelNum);
        final RejectedExecutionHandler rushTaskHandler = new ThreadPoolExecutor.DiscardOldestPolicy();
        final ExecutorService executorService = new ThreadPoolExecutor(parallelNum, parallelNum,
                0L, TimeUnit.MILLISECONDS, rushTaskQueue,
                rushTaskHandler);

        // 処理するタスクを作成
        final AtomicInteger readyCounter = new AtomicInteger(0);
        final AtomicInteger completeCounter = new AtomicInteger(0);
        PaymentLoader[] loaders = new PaymentLoader[parallelNum];
        for (int i = 0; i < parallelNum; i++) {
            loaders[i] = new PaymentLoader(readyCounter, tokenLists[i], manager.getCache(WALLET_CACHE_NAME), completeCounter);
        }

        // 負荷をかける
        for (PaymentLoader task : loaders) {
            executorService.execute(task);
        }

        // クライアントからの負荷を欠ける準備が終わるのを待つ
        while (readyCounter.get() != parallelNum) {
            try {
                TimeUnit.MILLISECONDS.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // 開始時間を測定
        long start = System.currentTimeMillis();

        // 負荷掛けを開始する
        for (PaymentLoader task : loaders) {
            task.startLoad();
        }

        // クライアントからの負荷を全部処理し終わるのを待つ
        while (completeCounter.get() != parallelNum) {
            try {
                TimeUnit.MILLISECONDS.sleep(10);
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
    private static final String port = "11222";
    private static final String isAuth = "false";
    private static final String user = "admin";
    private static final String password = "password";

    private static final String NON_TX_CACHE_CONFIG =
            "<distributed-cache name=\"%s\">"
                    + " <encoding media-type=\"application/x-protostream\"/>"
                    + " <groups enabled=\"true\"/>"
                    + "</distributed-cache>";

    private static final String TRANSACTIONAL_EXPIRED_CACHE_CONFIG =
            "<distributed-cache name=\"%s\">"
                    + " <encoding media-type=\"application/x-protostream\"/>"
                    + " <groups enabled=\"true\"/>"
                    + " <transaction mode=\"BATCH\" locking=\"PESSIMISTIC\"/>"
                    + " <memory max-count=\"100000\" when-full=\"REMOVE\"/>"
                    + "</distributed-cache>";

    private static final String TRANSACTIONAL_LOCKING_CACHE_CONFIG =
            "<distributed-cache name=\"%s\">"
                    + " <encoding media-type=\"application/x-protostream\"/>"
                    + " <groups enabled=\"true\"/>"
                    + " <locking acquire-timeout=\"100\" striping=\"false\"/>"
                    + " <transaction mode=\"BATCH\" locking=\"PESSIMISTIC\"/>"
                    + "</distributed-cache>";

    private static Configuration createConfiguration() throws Exception {

        Properties prop = new Properties();
        try {
            prop.load(new FileInputStream("./infinispan.properties"));
            System.out.println("infinispan.propertiesを読み込みました");
        }catch(Exception e){
            System.err.println("infinispan.propertiesが読み込まれませんでした");
        }

        ConfigurationBuilder cb
                = new ConfigurationBuilder();
        cb.marshaller(new ProtoStreamMarshaller())
                .statistics()
                .enable()
                .jmxDomain("org.infinispan")
                .addServer()
                .host(prop.getProperty("hostname", hostname))
                .port(Integer.parseInt(prop.getProperty("port", port)))
                .addContextInitializer("rhpay.loadtest.system.PaymentSchemaImpl");

        if (Boolean.parseBoolean(prop.getProperty("auth", isAuth))) {
            cb.security()
                    .authentication()
                    .realm("default")
                    .saslMechanism("DIGEST-MD5")
                    .username(prop.getProperty("user", user))
                    .password(prop.getProperty("password", password));
        }

        return cb.build();
    }
}

class PaymentLoader implements Runnable {

    private final AtomicInteger readyCounter;
    private final AtomicInteger completeCounter;
    private final List<TokenKey> tokenList;
    private final RemoteCache<ShopperKey, WalletEntity> walletCache;
    public final List<TokenKey> completedTokenList;
    public volatile boolean isStart = false;

    public volatile TokenKey currentToken;

    public PaymentLoader(AtomicInteger readyCounter, List<TokenKey> tokenList, RemoteCache<ShopperKey, WalletEntity> walletCache, AtomicInteger completeCounter) {
        this.readyCounter = readyCounter;
        this.tokenList = tokenList;
        this.walletCache = walletCache;
        this.completedTokenList = new ArrayList<>(tokenList.size());
        this.completeCounter = completeCounter;
    }

    public void startLoad() {
        this.isStart = true;
    }

    @Override
    public void run() {
        try {
            //処理のパラメータを作成
            Map<String, Object> payInfo = new HashMap<>(5);
            payInfo.put("amount", 1);
            payInfo.put("storeId", 1);
            payInfo.put("storeName", "");

            // 準備が出来たことを知らせる
            readyCounter.incrementAndGet();

            // 負荷をかける号令を待つ
            while (!this.isStart) {
                try {
                    TimeUnit.MILLISECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

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
            e.printStackTrace();
            throw e;
        } finally {
            // 負荷かけが終わったら完了タスク数を1増やす
            completeCounter.incrementAndGet();
        }
    }
}