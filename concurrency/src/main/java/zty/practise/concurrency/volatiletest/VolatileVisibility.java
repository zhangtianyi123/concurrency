package zty.practise.concurrency.volatiletest;

/**
 * 由于CPU缓存（可以理解为Java线程的私有的工作缓存）
 * 某一个线程对共享变量的写动作对于别的线程来说是未必可见的
 * 
 * 针对这个问题，JMM提供了Happens-Before规则对CPU缓存以及重排序
 * 做出一定的限制
 * 
 * volatile不能互斥性，但是可以通过在读写命令后插入内存屏障
 * 在cpu层面强心刷新缓存，从而保证可见性
 * 
 * Happens-before规则
 * 1 单线程顺序性
 * 2 volatile写对于读可见
 * 3 synchronized解锁对于加锁可见
 * 4 传递性
 * 5 线程开始 hp 线程中的读写动作 hp 线程结束（体现为thread.join）
 * # 中断可见（interrupt）
 * # finalize可见 （GC）
 * 
 * @author zhangtianyi
 *
 */
public class VolatileVisibility {

	static class A {
		private int count = 0;
		boolean v = false;
	   
		public void set() {
			count++;
			v = true;
		}
		
		public void get() {
			if(v) {
				System.out.println(count);
			}
		}
	}
	
	static class B {
		private int count = 0;
		volatile boolean v = false;
	   
		public void set() {
			System.out.println("写");
			count++;
			v = true;
		}
		
		public void get() {
			System.out.println("读");
			if(v) {
				System.out.println(count);
			}
		}
	}
	
	public static void main(String[] args) {
		//什么都不打印，因为v=true写动作对if(v)不可见
		//未必能复现
		A a = new A();
		new Thread(() -> { a.get();} ).start();
		a.set();
		
		//一定会打印1
		//虽然只有v修饰了volatile  但是由于单线程顺序性和传递性count的读也能看见写
		B b = new B();
		new Thread(() -> { b.get(); } ).start();
		b.set();
	}
}
