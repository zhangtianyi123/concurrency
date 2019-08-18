package zty.practise.concurrency.containers;

import java.util.List;
import java.util.Vector;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.function.Function;


/**
 * 对比SemaPhoreForPoolLimit 通过同步器同步的阻塞大于模拟线程池 的任务提交被阻塞
 * 
 * 而阻塞队列本身是生产者消费者的实现，可以同步转异步 （所有的生产动作都转为异步 同步转移到消费端）
 * 
 * 常常被用作一个中间缓冲区，避免了生产者消费者之间不平衡和同步等待等问题
 * 
 * @author zhangtianyi
 *
 */
public class BlockingQueueAsync<T, R> {

	final List<T> threadPool;

	//异步存放任务的阻塞队列，将接受任务和消费任务解耦
	//将一对一消费变成了一对多接收
	final BlockingQueue<Function<T, R>> queue;

	BlockingQueueAsync(int maxSize, T t) {
		threadPool = new Vector<T>();
		for (int i = 0; i < maxSize; i++) {
			threadPool.add(t);
		}

		queue = new ArrayBlockingQueue<Function<T, R>>(1000);
	}

	// 提交任务，线程池消费之
	MyFuture submit(Function<T, R> func) throws InterruptedException {
		queue.add(func);
		return new MyFuture();
	}
	
	@SuppressWarnings("static-access")
	void test() {
		BlockingQueueAsync<Thread, String> pool = new BlockingQueueAsync<Thread, String>(3, new Thread());
		new Thread(() -> {
			try {
				@SuppressWarnings("unchecked")
				MyFuture future = (BlockingQueueAsync<T, R>.MyFuture) pool.submit(t -> {
					System.out.println(t.currentThread());
					return t.currentThread().toString();
				});
				System.out.println("get:" + future.get());
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}).start();

	}
		
	
	
	public static void main(String[] args) throws InterruptedException {
		BlockingQueueAsync<Thread, String> pool = new BlockingQueueAsync<Thread, String>(3, new Thread());
		pool.test();
		pool.test();
		pool.test();
		pool.test();
	}
	
	class MyFuture {
		synchronized R get() throws InterruptedException {
			//此处采用主动轮询而非被动阻塞的方式
			while(threadPool.size() == 0) {
				//Thread.sleep(2000);
			}
			T t = null;
			try {
				t = threadPool.remove(0);
				Function<T, R> func = queue.take();
				Thread.sleep(5000);
				return func.apply(t);
			} finally {
				threadPool.add(t);
			}
		}
	}
}
