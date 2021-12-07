package rhpay.loadtest;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.concurrent.*;

@QuarkusMain
public class WebSingleUserLoadTest implements QuarkusApplication {

    @RestClient
    PaymentService paymentService;

    @RestClient
    InitializeService initializeService;

    public static final int rushThreadNum = 10;
    public static final int tokenNum = 1_000;


    @Override
    public int run(String... args) throws Exception {

        System.out.println("事前に初期化処理をして下さい");

        // 負荷をかけるスレッドを作成
        final int rushTaskQueueSize = rushThreadNum;
        final LinkedBlockingQueue<Runnable> rushTaskQueue = new LinkedBlockingQueue<>(rushTaskQueueSize);
        final RejectedExecutionHandler rushTaskHandler = new ThreadPoolExecutor.DiscardOldestPolicy();
        final ExecutorService executorService = new ThreadPoolExecutor(rushThreadNum, rushThreadNum,
                0L, TimeUnit.MILLISECONDS, rushTaskQueue,
                rushTaskHandler);

        WebLoader[] loaders = new WebLoader[rushThreadNum];
        for (int i = 0; i < rushThreadNum; i++) {
            loaders[i] = new WebLoader(tokenNum / rushThreadNum, paymentService);
        }

        long start = System.currentTimeMillis();

        for (int i = 0; i < rushThreadNum; i++) {
            executorService.execute(loaders[i]);
        }

        for (WebLoader l : loaders) {
            if (!l.isCompleted()) {
                try {
                    // まだ終わって無かったらスリープして待つ
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        long end = System.currentTimeMillis();

        System.out.println("結果");
        System.out.println("*****************************************");
        System.out.println(String.format("throughput %f process/ms", (double) ((double) tokenNum / (double) (end - start))));

        executorService.shutdownNow();
        return 0;
    }

    public static void main(String[] args) {
        Quarkus.run(WebSingleUserLoadTest.class, args);
    }

    class WebLoader implements Runnable {

        volatile int completedNum = 0;
        private final int taskNum;
        private final PaymentService paymentService;

        public WebLoader(int taskNum, PaymentService paymentService) {
            this.taskNum = taskNum;
            this.paymentService = paymentService;
        }

        @Override
        public void run() {
            for (completedNum = 0; completedNum < taskNum; completedNum++) {
                TokenResponse token = paymentService.getToken(1);
                PaymentResponse payment = paymentService.pay(1, token.getTokenId(), 1, 1);
            }
        }

        public boolean isCompleted() {
            return taskNum == completedNum;
        }
    }

}
