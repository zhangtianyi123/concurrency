package zty.practise.concurrency.locktest;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 相对于synchronized lock能响应中断/支持超时/非阻塞获取锁
 * 
 * 并结合tryLock和lockInterrupted 解决死锁/活锁问题
 * 
 * 饥饿问题基于lock的公平锁和非公平锁来解决
 * 
 * @author zhangtianyi
 *
 */
public class LockSpecial {

	/**
	 * 共享变量洗手间
	 */
	public int toilet;

	/**
	 * 共享变量开水房
	 */
	public int waterRoom;

	/**
	 * 分别给洗手间和开水房上锁
	 */
	final Lock waterRoomLock = new ReentrantLock();
	final Lock toiletLock = new ReentrantLock();

	private Lock lock = new ReentrantLock();

	public void goWaterRoomAndToiletInterrupt() throws InterruptedException {
		waterRoomLock.lockInterruptibly();
		// 增加死锁概率
		Thread.sleep(5000);
		try {
			toiletLock.lockInterruptibly();
			try {
				System.out.println("成功获取到两把锁WaterRoomAndToilet");
			} finally {
				toiletLock.unlock();
			}

		} finally {
			waterRoomLock.unlock();
		}

	}
	
	public void goToiletAndWaterRoomInterrupt() throws InterruptedException {
		waterRoomLock.lockInterruptibly();
		// 增加死锁概率
		Thread.sleep(5000);
		try {
			toiletLock.lockInterruptibly();
			try {
				System.out.println("成功获取到两把锁ToiletAndWaterRoom");
			} finally {
				toiletLock.unlock();
			}

		} finally {
			waterRoomLock.unlock();
		}

	}

	/**
	 * 测试lock支持响应中断 给阻塞的线程发送中断信号，能够唤醒它
	 * 
	 * @throws InterruptedException
	 */
	public void testLockWithInterrupt() throws InterruptedException {
		LockSpecial ls = new LockSpecial();
		Thread a = new Thread(() -> {
			try {
				ls.goWaterRoomAndToiletInterrupt();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		a.start();

		ls.goToiletAndWaterRoomInterrupt();
		
		Thread.sleep(5000);
		
		/**
		 * 此时两个线程均获取到底一把锁等待获取第二把锁
		 * 此时两个线程都阻塞
		 * 通过中断唤醒其中一个线程，同时将会释放掉第一个锁，另一个线程能获取到，消除竞争
		 */
		a.interrupt();
		
	}

	public void goWaterRoomAndToilet() throws InterruptedException {
		// 自旋
		while (true) {
			if (waterRoomLock.tryLock()) {
				// 增加活锁概率
				Thread.sleep(5000);
				try {
					if (toiletLock.tryLock()) {
						try {
							System.out.println("成功获取到两把锁WaterRoomAndToilet");
						} finally {
							toiletLock.unlock();
						}
					}
				} finally {
					waterRoomLock.unlock();
				}
			}
			// 休眠一段随机时间可以避免活锁
			Thread.sleep(0);
		}
	}

	public void goToiletAndWaterRoom() throws InterruptedException {
		// 自旋
		while (true) {
			if (toiletLock.tryLock()) {
				// 增加活锁概率
				Thread.sleep(5000);
				try {
					if (waterRoomLock.tryLock()) {
						try {
							System.out.println("成功获取到两把锁ToiletAndWaterRoom");
						} finally {
							waterRoomLock.unlock();
						}
					}
				} finally {
					toiletLock.unlock();
				}
			}
			// 休眠一段随机时间可以避免活锁
			Thread.sleep(0);
		}
	}

	/**
	 * 测试非阻塞的获取锁
	 * 
	 * @throws InterruptedException
	 */
	public void testTryLock() throws InterruptedException {
		LockSpecial ls = new LockSpecial();
		new Thread(() -> {
			try {
				ls.goWaterRoomAndToilet();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}).start();

		ls.goToiletAndWaterRoom();
	}

	/**
	 * 测试超时的非阻塞获取锁 一段时间没有获取到锁，不是阻塞而是返回一个错误
	 * 
	 * @throws InterruptedException
	 */
	public void testTryLockWithTime() throws InterruptedException {
		lock.tryLock(5, TimeUnit.SECONDS);

	}

	public static void main(String[] args) throws InterruptedException {
		LockSpecial ls = new LockSpecial();
		
		//测试trylock避免死锁问题（悲观锁的乐观自旋）
//		ls.testTryLock();
		
		//测试可中断的lock
		ls.testLockWithInterrupt();
		
	}
}
