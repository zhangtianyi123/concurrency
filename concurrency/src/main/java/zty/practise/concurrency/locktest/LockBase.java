package zty.practise.concurrency.locktest;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import zty.practise.concurrency.synchronizedtest.SynchronizedBase;

/**
 * lock是Java SDK提供的可替代synchronized的管程实现
 * 
 * lock的可见性是由volatile关键字间接实现的
 * 
 * @author zhangtianyi
 *
 */
public class LockBase {

	private static long count = 0;
	
	//可重入锁（当然synchronized也是可重入的）
	private final static Lock rlock = new ReentrantLock();
	
	/**
	 * 以下是使用lock的经典范式
	 * lock
	 * try{}
	 * finally{ unlock }
	 */
	public static void set() {
		rlock.lock();
		try {
			count++;
		} finally {
			rlock.unlock();
		}
	}
	
	/**
	 * 读方法
	 */
	public static void get() {
		rlock.lock();
		try {
			System.out.println(count);
		} finally {
			rlock.unlock();
		}
	}
	
	/**
	 * 以lock的基本用法 验证lock的互斥性
	 * @return
	 * @throws InterruptedException
	 */
	private static boolean testExclusive() throws InterruptedException {

		Thread a = new Thread(() -> {
			for (int j = 0; j < 10000; j++) {
				LockBase.set();
			}
		});
		Thread b = new Thread(() -> {
			for (int j = 0; j < 10000; j++) {
				LockBase.set();
			}
		});
		a.start();
		b.start();
		
		a.join();
		b.join();

		return count == 20000;
	}
	
	public static void main(String[] args) throws InterruptedException {
		System.out.println(testExclusive());
	}
}
