package zty.practise.concurrency.threadpool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.springframework.lang.Nullable;

import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.ThreadFactoryBuilder;


/**
 * Guava有提供线程池的工具类
 * 
 * @author zhangtianyi
 *
 */
public class GuavaThreadPool {

	/**
	 * 使用guava的工具类创建的线程可以有自己的名字而不是默认的"pool-x-thread-y"
	 * @param threadNamePrefix
	 * @return
	 */
	public static ThreadFactory buildThreadFactory(String threadNamePrefix) {
		return new ThreadFactoryBuilder().setNameFormat(threadNamePrefix + "-%d").build();
	}
	
	/**
	 * 可设定是否daemon, daemon线程在主线程已执行完毕时, 不会阻塞应用不退出, 而非daemon线程则会阻塞.
	 * 
	 */
	public static ThreadFactory buildThreadFactory(String threadNamePrefix, boolean daemon) {
		return new ThreadFactoryBuilder().setNameFormat(threadNamePrefix + "-%d").setDaemon(daemon).build();
	}
	
	/**
	 * 优雅的关闭线程池
	 * 
	 * （1）先使用shutdown, 停止接收新任务并尝试完成所有已存在任务.
	 * 
	 * （2）如果1/2超时时间后, 则调用shutdownNow,取消在workQueue中Pending的任务,并中断所有阻塞函数.
	 * 
	 * （3）如果1/2超时仍然超时，則强制退出.
	 * @param threadPool
	 * @param shutdownTimeoutMills
	 * @return
	 */
	public static boolean gracefulShutdown(@Nullable ExecutorService threadPool, int shutdownTimeoutMills) {
		return threadPool == null
				|| MoreExecutors.shutdownAndAwaitTermination(threadPool, shutdownTimeoutMills, TimeUnit.MILLISECONDS);
	}

	public static boolean gracefulShutdown(@Nullable ExecutorService threadPool, int shutdownTimeout,
			TimeUnit timeUnit) {
		return threadPool == null || MoreExecutors.shutdownAndAwaitTermination(threadPool, shutdownTimeout, timeUnit);
	}
	
}
