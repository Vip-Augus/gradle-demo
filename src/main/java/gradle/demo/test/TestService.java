package gradle.demo.test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;

/**
 * @author by JingQ on 2018/3/17
 */
public class TestService {

    private Semaphore semaphore = new Semaphore(1);

    private CountDownLatch countDownLatch;


    public TestService(CountDownLatch countDownLatch) {
        this.countDownLatch = countDownLatch;
    }

    public void testMethod() {
        try {
            semaphore.acquire();
            System.out.println("当前线程是: " + Thread.currentThread().getName() +
                    " 时间是： " + System.currentTimeMillis());
            //等待5秒
            Thread.sleep(1000);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            countDownLatch.countDown();
            semaphore.release();
        }
    }
}
