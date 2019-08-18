package zty.practise.concurrency.threadpool;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.google.common.collect.Lists;


/**
 * 
 * 线程池是一种池化资源模型，为复用昂贵的线程而产生
 * 避免线程频繁的创建和销毁
 * 
 * 线程池本质是一种生产者消费者模型 ，线程池的使用者是生产者提交（生产）任务
 * 线程池本身是消费者，线程处理（消费）任务
 * 
 * 而阻塞队列！是生产者消费者中间的缓冲区，能够有异步化等作用
 * 
 * 如果说生产者是一批不同窗口的厨师，消费者是一批不同桌的食客，那么缓冲区就是推着餐车的服务生
 * 
 * @author zhangtianyi
 *
 */
public class MyThreadPool {

	BlockingQueue<Runnable> cache;
	
	List<MyThread> threads = Lists.newArrayList();
	
	MyThreadPool(int poolSize, BlockingQueue<Runnable> cache) {
		this.cache = cache;
		
		for(int i=0; i<poolSize; i++) {
			MyThread t = new MyThread();
			t.start();
			threads.add(t);
		}
	}
	
	void execute(Runnable task) throws InterruptedException {
		cache.put(task);
	}
	
	class MyThread extends Thread {
		public void run() {
			while(true) {
				Runnable task;
				try {
					//将阻塞转移到此处
					//消费者和生产者解耦，直接同步的从缓冲区取出任务消费
					task = cache.take();
					task.run();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void main(String[] args) throws InterruptedException {
		//建议使用有界队列，避免OOM
		MyThreadPool pool = new MyThreadPool(5, new LinkedBlockingQueue<>(2));
		
		pool.execute(() -> { System.out.println("p-c model"); });
	}
	
}
