package zty.practise.concurrency.synchronizedtest;

/**
 * 多线程的活跃性问题往往分为饥饿，活锁，死锁
 * 由于synchronized是互斥的，阻塞的，所以容易曹成死锁
 * 
 * 死锁指的是：竞争资源相互等待时永久阻塞
 * 1 互斥：无法避免
 * 2 占有（部分资源）且等待（另部分资源）——一次申请所有资源
 * 3 不可抢占——超时lock等
 * 4 循环等待——按照顺序申请申请
 * 
 * 以SynchronizedAPIOpimization中开水房和洗手间为例
 * @author zhangtianyi
 *
 */
public class SynchronizedDeadLock {


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

	public void goToiletSingle() {
		synchronized (toiletLock) {
			toilet++;
		}
	}
	
	public void goWaterRoomSingle() {
		synchronized(waterRoomLock) {
			waterRoom++;
		}
	}

	
	/**
	 * 如果需要用细粒度的锁构筑统一的管程临界区的时候
	 * （同时管理开水房和洗手间两个临界资源（共享变量））
	 * @throws InterruptedException 
	 */
	public void goWaterRoomAndToilet() throws InterruptedException {
		synchronized(waterRoomLock) {
			Thread.sleep(5000);
			synchronized(toiletLock) {
				
			}
		}
	}
	
	public void goToiletAndWaterRoom() throws InterruptedException {
		synchronized(toiletLock) {
			Thread.sleep(5000);
			synchronized(waterRoomLock) {
				
			}
		}
	}
	
	/**
	 * 使用jps查看java进程 再用jstack打印可以看到死锁情况
	 * 当然使用JMX工具也可以
	 * @param args
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws InterruptedException {
		SynchronizedDeadLock sd = new SynchronizedDeadLock();
		
		new Thread(() -> {
			try {
				sd.goToiletAndWaterRoom();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}).start();
		
		sd.goWaterRoomAndToilet();
	}
}
