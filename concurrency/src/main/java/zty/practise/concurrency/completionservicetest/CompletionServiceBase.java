package zty.practise.concurrency.completionservicetest;

import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import zty.practise.concurrency.threadpool.GuavaThreadPool;
import zty.practise.concurrency.threadpool.ThreadPoolHandler;

/**
 * 批量执行异步任务(因为内置了缓冲区——阻塞队列)
 * BlockingQueue+Executor
 * 
 * CompletionService相当于线程池（线程池的Future是同步模型的解决方案）的再包装
 * 
 * 线程池本质上是一个生产者-消费者模型  线程池使用者生产任务 线程池的线程消费任务 其中有任务的阻塞队列作为缓冲区，让多路提交异步
 * 
 * 但是——线程池结合future的时候，也是一个生产者-消费者模型：线程池生产结果，主线程消费结果，而CompletionServiceBase包装的阻塞队列将多路消费异步化
 * 
 *                         【                   线程池                                  】
 * 任务提交方(比如主线程) —— 任务阻塞队列 —— 消费者线程，同时生产结果  —— 结果阻塞队列 —— 结果消费方（比如主线程）
 *                         【                                      CompletionService             】
 * 
 * @author zhangtianyi
 *
 */
public class CompletionServiceBase {

	
	void testSyncSubmitAndAsyncConsume() throws InterruptedException, ExecutionException {
		ThreadPoolExecutor pool = new ThreadPoolExecutor(2, 4, 0L, TimeUnit.MILLISECONDS,
    			new LinkedBlockingQueue());
    	
		CompletionService<Integer> completionService = new ExecutorCompletionService<>(pool);
		completionService.submit(() -> { return 2; });
		completionService.submit(() -> { return 3; });
		completionService.submit(() -> { return 4; });
		
		int res = 1;
		for(int i=0; i<3; i++) {
			int tmp = completionService.take().get();
			res = res * tmp;
		}
		
		System.out.println(res);
	}
	
	public static void main(String[] args) throws InterruptedException, ExecutionException {
		CompletionServiceBase base = new CompletionServiceBase();
		
		base.testSyncSubmitAndAsyncConsume();
	}
}
