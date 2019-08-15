package zty.practise.concurrency.synchronizedtest;

import org.openjdk.jol.info.ClassLayout;

/**
 * 本类是测试Java对synchronized的优化
 * 
 * synchronized被称为重量级锁是因为每次进入临界区之后其他试图进入临界区 的线程都会阻塞（意味着用户态-内核态的切换）
 * jdk对其做了偏向锁，轻量级锁等应对不同并发场景的划分
 * 
 * 对象头的标记字段中最后两位表示锁状态： 00代表轻量级锁，01代表无锁（偏向锁），10代表重量级锁，11和GC算法有关
 * 
 * @author zhangtianyi
 *
 */
public class SynchronizedJavaOpimization {

	int count = 0;

	boolean v = false;

	/**
	 * JVM优化——逃逸分析 因为synchronized将线程ID写入对象头，而new Object是一个每次都会变的对象
	 * 那么将会经过逃逸分析之后，直接在字节码层面干掉synchronized 相当于无锁
	 */
	public void set() {
		synchronized (new Object()) {
			count++;
			v = true;
			System.out.println(ClassLayout.parseInstance(this).toPrintable());
		}
	}

	public void get() {
		synchronized (new Object()) {
			System.out.println(count);
		}
	}

	/**
	 * 偏向锁针对只有一个线程的场景，第一次CAS写入对象头 之后每次只需要判断即可 ，只有一次CAS相当于无锁
	 * 
	 * @throws InterruptedException
	 */
	public void doWitBiasLock() throws InterruptedException {
		Thread.sleep(5000);
		Object lock = new Object();
		synchronized (lock) {
			System.out.println(ClassLayout.parseInstance(lock).toPrintable());
		}
	}

	/**
	 * 轻量级锁针对多个线程的场景，但是交替进入临界区没有竞争的场景 每次加锁解锁都需要CAS操作
	 * @throws InterruptedException 
	 */
	public synchronized void doWithLightWeightLock() throws InterruptedException {
		Thread.sleep(5000);
		Object lock = new Object();
		Thread a = new Thread(() -> {
			synchronized (lock) {
				System.out.println(ClassLayout.parseInstance(lock).toPrintable());
			}
		});
		
		a.start();
		a.join();
		synchronized (lock) {
			System.out.println(ClassLayout.parseInstance(lock).toPrintable());
		}
	}

	/**
	 * 重量级锁针对竞争场景，将会阻塞线程
	 * @throws InterruptedException 
	 */
	public synchronized void doWithHeavyWeightLock() throws InterruptedException {
		Thread.sleep(5000);
		Object lock = new Object();
		Thread a = new Thread(() -> {
			synchronized (lock) {
				System.out.println(ClassLayout.parseInstance(lock).toPrintable());
			}
		});
		
		a.start();
		synchronized (lock) {
			System.out.println(ClassLayout.parseInstance(lock).toPrintable());
		}
	}

	/**
	 * 验证可变对象锁被逃逸分析，互斥失效
	 * 
	 * @throws InterruptedException
	 */
	public void testRemoveLock() throws InterruptedException {
		SynchronizedJavaOpimization op = new SynchronizedJavaOpimization();
		Thread a = new Thread(() -> {
			for (int j = 0; j < 10000; j++) {
				op.set();
			}
		});
		Thread b = new Thread(() -> {
			for (int j = 0; j < 10000; j++) {
				op.set();
			}
		});
		a.start();
		b.start();

		a.join();
		b.join();

		System.out.println(op.count != 20000);
	}

	public void testDoWitBiasLock() {
		new Thread(() -> {
			try {
				doWitBiasLock();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}).start();
	}

	/**
	 * 验证synchronized锁锁对象可变时对互斥的影响
	 * 
	 * @param args
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws InterruptedException {
		SynchronizedJavaOpimization op = new SynchronizedJavaOpimization();

		// 测试无锁，首行64位 00000001 对象头的markword 最后三位001 表示无锁
		// 这个无锁不是指在程序层面无锁，是被JVM优化掉了
		// op.set();
		
		//测试偏向锁，首行00000101 最后三位101表示偏向锁
		//op.doWitBiasLock();
		
		//测试轻量级锁
		//第一个临界区打印00000101，第二个临界区00001000 证明从偏向锁升级为轻量级锁
		//op.doWithLightWeightLock();
    
		//测试重量级锁 11001010 010证明为重量级锁
//		op.doWithHeavyWeightLock();
	}
}
