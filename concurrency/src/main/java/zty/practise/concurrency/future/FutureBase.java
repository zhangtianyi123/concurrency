package zty.practise.concurrency.future;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * ThreadPoolExecutor的execute方法是直接执行没有返回值的 通过submit方法和FutureTask工具类获取执行结果
 * 
 * Future:取消任务，判断任务是否已取消，判断任务是否已结束，（超时的）获取任务结束 使用模型往往类似于join 也类似于countdownlatch
 * 参见CountDownLatchBase
 * 
 * FutureTask实现了Runnable Future接口
 * 
 * @author zhangtianyi
 *
 */
public class FutureBase {

	/**
	 * 使用 futureTask实现依赖等待的线程同步模型
	 * 
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	void testOneDependencyMoreByFuture() throws InterruptedException, ExecutionException {

		FutureTask<Integer> futureTaskTwo = new FutureTask<>(() -> {
			return 2;
		});
		FutureTask<Integer> futureTaskThree = new FutureTask<>(() -> {
			return 3;
		});

		ExecutorService es = Executors.newCachedThreadPool();

		es.submit(futureTaskTwo);
		es.submit(futureTaskThree);

		Integer two = futureTaskTwo.get();
		Integer three = futureTaskThree.get();

		System.out.println(two * three);
	}

	/**
	 * future同比对比使用阻塞队列实现 当然也可以参照CountDownLatchBase.java中对此模型的两个实现
	 * 
	 * @throws InterruptedException
	 */
	void testOneDependencyMoreByBlockingQueue() throws InterruptedException {
		BlockingQueue cache = new LinkedBlockingQueue(2);
		ExecutorService es = Executors.newCachedThreadPool();
		int res = 1;
		
		es.execute(() -> {
			cache.add(2);
		});
		es.execute(() -> {
			cache.add(3);
		});

		for (int i = 0; i < 2; i++) {
			int tmp = (int) cache.take();
			tmp = tmp * res;
		}

		System.out.println(res);
	}

	public static void main(String[] args) throws InterruptedException, ExecutionException {
		FutureBase base = new FutureBase();

		// futureTask实现依赖等待的线程同步模型
		// base.testOneDependencyMoreByFuture();

		// blockingQueue实现依赖等待的线程同步模型
		base.testOneDependencyMoreByBlockingQueue();
	}
}
