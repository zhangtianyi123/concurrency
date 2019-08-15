package zty.practise.concurrency.locktest;

import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.google.common.collect.Lists;

/**
 * synchronized利用object的wait  notify  notifyAll能实现同步 但是只有一个条件变量
 * 
 * Lock通过Condition实现同步，支持多个条件变量 有语义类似的await signal signalAll
 * 
 * 以下是实现生产者消费者模式（阻塞队列）的Lock-Condition版本
 * 
 * 类似的synchronized(因为只有一个条件变量的限制——不严谨)：SynchronizedSync
 * 
 * @author zhangtianyi
 *
 */
public class LockSync {

	List<String> queue = Lists.newArrayList();
	private final Lock lock = new ReentrantLock();
	Condition notFull = lock.newCondition();
	Condition notEmpty = lock.newCondition();

	void produce() throws InterruptedException {
		lock.lock();
		try {
			System.out.println("生产者进入临界区");
			while(queue.size() == 5) {
				notFull.await();
			}
			queue.add("goods");
			System.out.println("queue-add");
			notEmpty.signal();
		} finally {
			lock.unlock();
		}
		
	}
	
	void consume() throws InterruptedException {
		lock.lock();
		try {
			System.out.println("消费者进入临界区");
			while(queue.size() == 0) {
				notEmpty.await();
				System.out.println("消费者被唤醒");
			}
			queue.remove(queue.size()-1);
			System.out.println("queue-remove");
			notFull.signal();
		} finally {
			lock.unlock();
		}
	}
}
