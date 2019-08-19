package zty.practise.concurrency.threadpool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;

/**
 * 对于线程池的handler参数 可以定义拒绝策略
 * （1）CallerRunsPolicy 提交任务的线程自己去执行该任务
 * （2）默认的拒绝策略 抛出异常
 * （3）DiscardPolicy直接丢弃任务
 * （4）DiscardOldestPolicy 丢弃最老的任务
 * 
 * 建议使用自定义的拒绝策略——且往往和降级策略搭配使用
 * 
 * @author zhangtianyi
 *
 */
@Slf4j
public class ThreadPoolHandler extends ThreadPoolExecutor.AbortPolicy {

	private final String threadName;
	
    public ThreadPoolHandler(String threadName) {
        this.threadName = threadName;
    }
    
    /**
     * 自定义拒绝策略
     */
    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
    	log.debug("{} rejected", threadName);
    }

}
