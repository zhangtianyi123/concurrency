package zty.practise.concurrency.synchronizedtest;

/**
 * new 在程序中是一条语句却对应着三条CPU指令
 * 1 分配一块内存（字节码new 申请一块Eden区内存 由于TLAB会申请线程自有的一段 避免申请时候加锁）
 * 2 在内存上初始化对象（new触发了类加载 分为（加载-链接-初始化））
 * 3 将M地址给instance对象（JVM栈->堆）
 * 
 * 编译优化可能将执行顺序调整为132 如果执行到3之后发生了线程切换，那么另一个线程执行instance==null失败
 * 但是实际上并没有进行性初始化，将引发NPE
 * 
 * 双重检查是为了避免进入临界区
 * 
 * 本质上是因为第一个if(instance == null) 不在临界区，出现了竞态条件
 * 
 * 解决1：final修饰 使得new安全发布(发布可见性语义)
 * 解决2：volatile保证可见性
 * 
 * @author zhangtianyi
 *
 */
public class SynchronizedForSingleton {
    static SynchronizedForSingleton instance;
    
    private SynchronizedForSingleton () {
    	
    }
    
    static SynchronizedForSingleton getInstance() {
    	SynchronizedForSingleton instance = null;
    	if(instance == null) {
    		synchronized(SynchronizedForSingleton.class) {
    			if(instance == null) {
    				//可能发生指令重排序（cpu...）
    				instance = new SynchronizedForSingleton();
    			}
    		}
    	}
    	return instance;
    }
}
