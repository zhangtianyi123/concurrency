package zty.practise.concurrency.threadpool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * ThreadPoolWxecutor有多个参数
 * corePoolSize    代表最少线程数
 * maximumPoolSize 代表最大线程数
 * keepAliveTime & unit 代表最大矿工时间
 * workQueue 代表工作队列
 * threadFactory 可以自定义如何创建线程 比如指定一个有意义的名字
 * handler代表拒绝策略 见ThreadPoolHandler
 * 
 * （1）不建议使用默认的无解队列容易造成OOM
 * （2）不建议使用默认的拒绝策略，搭配降级使用
 * （3）不建议使用默认的线程名字（优先级低）
 * 
 * @author zhangtianyi
 *
 */
public class ThreadPoolBase {
	
	/**
	 * 当提交任务超过max和queue之和的时候触发拒绝
	 */
	void testHandler() {
    	ExecutorService executors = new ThreadPoolExecutor(2, 2, 0L, TimeUnit.MILLISECONDS,
    			new LinkedBlockingQueue(1), GuavaThreadPool.buildThreadFactory("ztyPool"), new ThreadPoolHandler("ztysPool"));
    	
    	executors.execute(() -> { System.out.println("1" + Thread.currentThread()); try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} });
    	
    	executors.execute(() -> { System.out.println("2" + Thread.currentThread()); try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} });
    	
    	executors.execute(() -> { System.out.println("3" + Thread.currentThread()); try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} });
    	
      	executors.execute(() -> { System.out.println("4" + Thread.currentThread()); try {
    			Thread.sleep(5000);
    		} catch (InterruptedException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		} });
      	
      	executors.execute(() -> { System.out.println("5" + Thread.currentThread()); try {
    			Thread.sleep(5000);
    		} catch (InterruptedException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		} });
    }
    
    public static void main(String[] args) {
    	ThreadPoolBase base = new ThreadPoolBase();
    	base.testHandler();
	}

}
