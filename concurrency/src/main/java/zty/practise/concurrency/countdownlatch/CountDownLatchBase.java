package zty.practise.concurrency.countdownlatch;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * CountDownLatch用以实现线程等待
 * 通常的场景是主线程等待依赖的子线程执行完毕
 * 
 * 类似于join 的线程池版本用法
 * 
 * @author zhangtianyi
 *
 */
public class CountDownLatchBase {

	int count = 1;
	
	void testOneDependencyMoreByJoin() throws InterruptedException {
		
		Thread a = new Thread(() -> { count++;});
		Thread b = new Thread(() -> { count++;});
		
		a.start();
		b.start();
		
		a.join();
		b.join();
		
		count = count * 2;
		System.out.println(count);
	}
	
	void testOneDependencyMoreByLatch() throws InterruptedException {
		Executor executor = Executors.newFixedThreadPool(2);
		
        CountDownLatch latch = new CountDownLatch(2);
        executor.execute(()-> {
        	count++;
            latch.countDown();
        });
        executor.execute(()-> {
        	count++;
            latch.countDown();
        });
        // 等待两个查询操作结束
        latch.await();
        
		count = count * 2;
		System.out.println(count);
		
	}
	
	public static void main(String[] args) throws InterruptedException {
		CountDownLatchBase base = new CountDownLatchBase();
		
		//通过join来实现汇聚关系的依赖等待
		base.testOneDependencyMoreByJoin();
		
		//通过countDownLatch来实现汇聚关系的依赖等待
//		base.testOneDependencyMoreByLatch();
	}
}
