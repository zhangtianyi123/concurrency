package zty.practise.concurrency.semaphore;

import java.util.concurrent.Semaphore;

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


	static int num = 0;

	// 初始化为1 等价于互斥锁
	static final Semaphore mySem = new Semaphore(1);

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
					SemaphoreBase.addNum();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		Thread b = new Thread(() -> {
			for (int j = 0; j < 100; j++) {
				try {
					SemaphoreBase.addNum();
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
