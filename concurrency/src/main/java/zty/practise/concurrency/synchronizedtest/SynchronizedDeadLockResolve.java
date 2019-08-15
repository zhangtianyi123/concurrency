package zty.practise.concurrency.synchronizedtest;

import java.util.ArrayList;
import java.util.List;

/**
 * 对应SynchronizedDeadLock 针对死锁的形成必要条件，提供解决方案
 * 
 */
public class SynchronizedDeadLockResolve {

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
	final Object waterRoomLock = new Object();
	final Object toiletLock = new Object();

	/**
	 * 破坏占有且等待
	 * 
	 * @author zhangtianyi
	 */
	class DoubleLockContainer {
		private List<Object> locks = new ArrayList<>();

		/**
		 * 一次性申请所有资源
		 * 
		 * @param lockOne
		 * @param lockTwo
		 * @return
		 */
		synchronized boolean applyAllLock(Object lockOne, Object lockTwo) {
			if (locks.contains(lockOne) || locks.contains(lockTwo)) {
				return false;
			} else {
				locks.add(lockOne);
				locks.add(lockTwo);
			}
			return true;
		}

		/**
		 * 一次性釋放所有資源
		 * 
		 * @param lockOne
		 * @param lockOne
		 */
		synchronized void freeAllLocks(Object lockOne, Object lockTwo) {
			locks.remove(lockOne);
			locks.remove(lockTwo);
		}
	}

	public void goWaterRoomAndToilet(DoubleLockContainer container) throws InterruptedException {
		while (!container.applyAllLock(waterRoomLock, toiletLock));
		
		try {
			System.out.println("goWaterRoomAndToilet获取到全部资源");
			synchronized (waterRoomLock) {
				Thread.sleep(1000);
				synchronized (toiletLock) {

				}
			}
		} finally {
			container.freeAllLocks(waterRoomLock, toiletLock);
		}
	}

	public void goToiletAndWaterRoom(DoubleLockContainer container) throws InterruptedException {
		while (!container.applyAllLock(waterRoomLock, toiletLock));
		

		try {
			System.out.println("goToiletAndWaterRoom获取到全部资源");
			synchronized (toiletLock) {
				Thread.sleep(1000);
				synchronized (waterRoomLock) {

				}
			}
		} finally {
			container.freeAllLocks(waterRoomLock, toiletLock);
		}
	}
	
	/**
	 * 一次性申请资源破坏死锁
	 * @throws InterruptedException 
	 * 
	 */
	public void testDeadLockWithApplyAllLocks() throws InterruptedException {
		DoubleLockContainer container = new DoubleLockContainer();
		SynchronizedDeadLockResolve sd = new SynchronizedDeadLockResolve();

		new Thread(() -> {
			try {
				sd.goToiletAndWaterRoom(container);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}).start();

		sd.goWaterRoomAndToilet(container);
	}
	
	public void goWaterRoomAndToilet() throws InterruptedException {
		synchronized(waterRoomLock) {
			Thread.sleep(5000);
			synchronized(toiletLock) {
				
			}
		}
	}
	
	/**
	 * 相同顺序申请资源破坏死锁
	 * @throws InterruptedException 
	 * 
	 */
	public void testDeadLockWithSameOrder() throws InterruptedException {
		SynchronizedDeadLockResolve sd = new SynchronizedDeadLockResolve();

		new Thread(() -> {
			try {
				sd.goWaterRoomAndToilet();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}).start();

		sd.goWaterRoomAndToilet();
	} 

	/**
	 * 使用jps查看java进程 再用jstack打印可以看到沒有發生死锁情况 当然使用JMX工具也可以
	 * 线程会顺利执行完成
	 * @param args
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws InterruptedException {
		SynchronizedDeadLockResolve sd = new SynchronizedDeadLockResolve();
		
		//一次性申请资源破坏死锁测试
//		sd.testDeadLockWithApplyAllLocks();
		
		//按顺序申请资源破坏死锁测试
		sd.testDeadLockWithSameOrder();
	}
}
