package zty.practise.concurrency.locktest;

import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.google.common.collect.Lists;

import zty.practise.concurrency.guardedsuspension.GuardedSync;

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
			notEmpty.signalAll();
		} finally {
			System.out.println("生产者释放锁");
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
			System.out.println("--------消费者已经唤醒等待一会儿再消费----------");
			Thread.sleep(50000);
			queue.remove(queue.size()-1);
			System.out.println("queue-remove");
			notFull.signalAll();
		} finally {
			lock.unlock();
		}
	}
	
	public static void main(String[] args) throws InterruptedException {
		LockSync sync = new LockSync();
		Thread A = new Thread(() -> { try {
			sync.consume();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}});
		
		Thread B = new Thread(() -> { try {
			sync.consume();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}});
		
		Thread C = new Thread(() -> { try {
			sync.consume();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}});
		
		Thread D = new Thread(() -> { try {
			sync.produce();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}});
		A.start();
		B.start();
		C.start();
		Thread.sleep(5000);
		D.start();
		Thread.sleep(10000);
		System.out.println("end...");
	}
}
