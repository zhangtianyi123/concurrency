package zty.practise.concurrency.synchronizedtest;

/**
 * synchronized被称为重量级锁是因为每次进入临界区之后其他试图进入临界区
 * 的线程都会阻塞（意味着用户态-内核态的切换）
 * jdk对其做了偏向锁，轻量级锁等应对不同并发场景的划分
 * 
 * 对象头的标记字段中最后两位表示锁状态：
 * 00代表轻量级锁，01代表无锁（偏向锁），10代表重量级锁，11和GC算法有关
 * 
 * 由于优化在底层完成，api层面难以验证，所以本测试更多通过注释来做说明
 * @author zhangtianyi
 *
 */
public class SynchronizedOpimization {
	
	int count = 0;
	
	boolean v = false;
	/**
	 * JVM优化——逃逸分析
	 * 因为synchronized将线程ID写入对象头，而new Object是一个每次都会变的对象
	 * 那么将会经过逃逸分析之后，直接在字节码层面干掉synchronized 相当于无锁
	 */
	public void set() {
		synchronized(new Object()) {
			count++;
			v = true;
		}
	}
		
	public void get() {
	    synchronized(new Object()) {
	    	System.out.println(count);	
	    }
	}
	
	/**
	 * 偏向锁针对只有一个线程的场景，第一次CAS写入对象头
	 * 之后每次只需要判断即可 ，只有一次CAS相当于无锁
	 */
	public synchronized void doWitBiasLock() {
	   count++;	
	}
	
	/**
	 * 轻量级锁针对多个线程的场景，但是交替进入临界区没有竞争的场景
	 * 每次加锁解锁都需要CAS操作
	 */
	public synchronized void doWithLightWeightLock() {
	   count++;	
	}

	/**
	 * 重量级锁针对竞争场景，将会阻塞线程
	 */
	public synchronized void doWithHeavyWeightLock() {
	   count++;	
	}
	
	/**
	 * 验证synchronized锁锁对象可变时对互斥的影响
	 * @param args
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws InterruptedException {
		SynchronizedOpimization op = new SynchronizedOpimization();
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
}
