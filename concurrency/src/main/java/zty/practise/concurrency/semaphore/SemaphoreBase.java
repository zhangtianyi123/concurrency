package zty.practise.concurrency.semaphore;

/**
 * 
 * 管程和信号量是两大解决并发问题的模型
 * 
 * 信号量模型通过一个计数器来统计已经进入临界区的线程数量
 * 如果已经到达上限则限制阻塞，否则不阻塞
 * 即信号量的n=1的时候类似于互斥锁 但是n>1的时候允许多个线程进入临界区，在临界区内可能线程不安全
 * 
 * @author zhangtianyi
 *
 */
public class SemaphoreBase {

	
}
