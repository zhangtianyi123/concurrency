package zty.practise.concurrency.cyclicBarrier;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * CyclicBarrier可以实现线程的同步等待
 * 
 * 可以重置计数器
 * 可以传入回调函数（同步执行->异步执行）
 * 
 * CountDownLatch主要用来解决一个线程等待多个线程的场景
 * Cyclic是一组线程的互相等待
 * 
 * 回调函数和重置计数器给了更多的灵活性
 * 
 * @author zhangtianyi
 *
 */
public class CyclicBarrierBase {
   
	int a = 1;
	int b = 1;
	int c = 1;
	
	final Executor executor = Executors.newFixedThreadPool(1);
	final Executor executorAdd = Executors.newFixedThreadPool(2);
	
	//同步
	final CyclicBarrier barrierSync = new CyclicBarrier(2, () -> {
		c = a * b; 
		a = c;
		b = c;
		System.out.println("回调c = " + c);
	});
	
	//异步
	final CyclicBarrier barrierAsync = new CyclicBarrier(2, () -> {
		executor.execute(() -> {
			c = a * b; 
			a = c;
			b = c;
			System.out.println("回调c = " + c);
		});
	});
	
	/**
	 * 同步执行回调函数
	 */
	void testBarrierForSync() {
		for(int i=0; i<3; i++) {
			executorAdd.execute(() -> {
				a++;
				try {
					barrierSync.await();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (BrokenBarrierException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			});
			
			executorAdd.execute(() -> {
				b++;
				try {
					barrierSync.await();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (BrokenBarrierException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			});
		}
	}
	
	/**
	 * 异步执行回调函数
	 */
	void testBarrierForAsync() {
		for(int i=0; i<3; i++) {
			executorAdd.execute(() -> {
				a++;
				try {
					barrierAsync.await();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (BrokenBarrierException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			});
			
			executorAdd.execute(() -> {
				b++;
				try {
					barrierAsync.await();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (BrokenBarrierException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			});
		}
	}
	
	public static void main(String[] args) {
		CyclicBarrierBase base = new CyclicBarrierBase();
		
		//同步的结果是对的
		base.testBarrierForSync();
		
		//同步转异步(通过提交给线程池新线程)之后乱序，因为AB依赖于c  
		//如果要异步执行回调函数  除非再异步转同步（比如）回调函数的值存在阻塞队列中，生产者阻塞获取
		base.testBarrierForAsync();
	}
}
