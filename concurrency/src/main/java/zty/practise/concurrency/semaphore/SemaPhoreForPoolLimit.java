package zty.practise.concurrency.semaphore;

import java.util.List;
import java.util.Vector;
import java.util.concurrent.Semaphore;
import java.util.function.Function;

/**
 * semphore在count=1的时候是互斥语义
 * 在count>1的时候可以起到限流
 * 
 * 
 * 线程池本质上是一种生产者-消费者模式
 * 线程池的生产是异步的，只需要提交给线程池，任务会存放在阻塞队列
 * 
 * 此处不适用阻塞队列，而是用同步器实现的是同步生产，超过线程池大小的任务直接阻塞，有空闲才提交
 * 
 * 
 * @author zhangtianyi
 *
 * @param <T>
 * @param <R>
 */
public class SemaPhoreForPoolLimit<T, R> {

	final List<T> threadPool;
	
	final Semaphore sem;
	
	SemaPhoreForPoolLimit(int maxSize, T t) {
		threadPool = new Vector<T>();
		for(int i=0; i < maxSize; i++) {
			threadPool.add(t);
		}
		
		sem = new Semaphore(maxSize);
	}
	
	//提交任务，线程池消费之
	R exec(Function<T, R> func) throws InterruptedException {
		T t = null;
		//如果线程池的没有空闲线程了，那么提交任务的线程阻塞
		sem.acquire();
		try {
			//取出线程
			t = threadPool.remove(0);
			Thread.sleep(8000);
			//线程执行提交的任务
			return func.apply(t);
		} finally {
			//线程放回线程池
			threadPool.add(t);
			sem.release();
		}
	}
	
	/**
	 * 可以观测到前三个线程同时执行，第四个线程等待有空闲才执行打印
	 * @param args
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws InterruptedException {
		SemaPhoreForPoolLimit<Thread, String> pool = new SemaPhoreForPoolLimit<Thread, String>(3, new Thread());
		new Thread(() -> {
			try {
				pool.exec(t -> {
					System.out.println(t.currentThread());
					return t.currentThread().toString();
				});
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}).start();
		
		new Thread(() -> {
			try {
				pool.exec(t -> {
					System.out.println(t.currentThread());
					return t.currentThread().toString();
				});
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}).start();
		
		new Thread(() -> {
			try {
				pool.exec(t -> {
					System.out.println(t.currentThread());
					return t.currentThread().toString();
				});
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}).start();
		
		new Thread(() -> {
			try {
				pool.exec(t -> {
					System.out.println(t.currentThread());
					return t.currentThread().toString();
				});
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}).start();
	}
}
