package zty.practise.concurrency.semaphore;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.common.collect.Lists;

import zty.practise.concurrency.synchronizedtest.SynchronizedBase;

/**
 * 管程和信号量是可以互相实现的 一下用管程实现信号量
 * 
 * 信号量的核心是共享变量计数器，通过它来协调同步线程
 * 
 * @author zhangtianyi
 *
 */
public class SemaphoreModel {

	// 共享计数器
	private AtomicInteger count = new AtomicInteger(0);

	// 阻塞队列
	List<Thread> queue = Lists.newArrayList();

	private final Object lock = new Object();

	SemaphoreModel(int count) {
		this.count = new AtomicInteger(count);
	}

	/**
	 * 申请资源
	 * 
	 * @throws InterruptedException
	 */
	public void acquire() throws InterruptedException {
		count.getAndDecrement();
		if (count.get() < 0) {
			synchronized (lock) {
				if (count.get() < 0) {
					queue.add(Thread.currentThread());
					lock.wait();
				}
			}
		}

	}

	/**
	 * 释放资源
	 */
	public void release() {
		count.getAndIncrement();
		if (count.get() <= 0) {
			synchronized (lock) {
				if (count.get() <= 0) {
					queue.remove(0);
					lock.notify();
				}
			}
		}
	}

	static int num = 0;

	// 初始化为1 等价于互斥锁
	static final SemaphoreModel mySem = new SemaphoreModel(1);

	static void addNum() throws InterruptedException {
		mySem.acquire();
		try {
			num++;
		} finally {
			mySem.release();
		}
	}

	private static boolean testExclusive() throws InterruptedException {

		Thread a = new Thread(() -> {
			for (int j = 0; j < 100; j++) {
				try {
					SemaphoreModel.addNum();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		Thread b = new Thread(() -> {
			for (int j = 0; j < 100; j++) {
				try {
					SemaphoreModel.addNum();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		a.start();
		b.start();

		a.join();
		b.join();

		System.out.println(num);
		return num == 200;
	}

	public static void main(String[] args) throws InterruptedException {
		// 测试用信号量实现互斥
		testExclusive();
	}
}
