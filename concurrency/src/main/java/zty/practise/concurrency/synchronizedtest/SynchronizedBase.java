package zty.practise.concurrency.synchronizedtest;

/**
 * 1 synchronized是Java提供的管程实现，用其构筑临界区可以保证互斥 2
 * 基于Java内存模型的happens-before规则，synchronized能够保证可见性（解锁hp加锁） 3
 * sychronized是重量级（阻塞/线程切换）的jdk锁，之后进行了偏向锁，轻量级锁，和JVM逃逸分析等优化 4
 * synchronized的实现是通过在字节码层面插入monitorenter和monitorexit指令
 * 
 * @author zhangtianyi
 */
public class SynchronizedBase {

	/**
	 * 在32位机器上，long/double的读写都是非原子的
	 * int等其他类型的读写是原子的  但是i++不是
	 */
	private static long count = 0L;

	/**
	 * synchroized有以下三种使用方式
	 */
	
	/**
	 * 1 synchronized修饰实例方法，锁对象为this指针
	 */
	public synchronized void get() {
		System.out.println(count);
	}

	/**
	 * 2 synchronized修饰静态方法，锁对象为class对象
	 */
	public synchronized static void set() {
		count++;
	}

	/**
	 * lock使用私有的 不可变的为最佳实践
	 * 由于String/Integer等享元模式（缓存常量池）的存在
	 * 可能不同实例的锁  引用的是同一个对象，所以不适合作为锁
	 */
	private final Object lock = new Object();
    
	/**
	 * 3 synchronized修饰代码块，锁对象为传入的对象
	 */
	public void doWithock() {
		synchronized (lock) {
			count--;
		}
	}

	/**
	 * 测试synchronized的互斥性，因为i++不是原子操作存在竞态条件（cpu缓存+cpu切换）
	 * 所以如果不互斥极大可能结果小于2w
	 * @author zhangtianyi
	 * @return
	 * @throws InterruptedException
	 */
	private static boolean testExclusive() throws InterruptedException {

		Thread a = new Thread(() -> {
			for (int j = 0; j < 10000; j++) {
				SynchronizedBase.set();
			}
		});
		Thread b = new Thread(() -> {
			for (int j = 0; j < 10000; j++) {
				SynchronizedBase.set();
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
