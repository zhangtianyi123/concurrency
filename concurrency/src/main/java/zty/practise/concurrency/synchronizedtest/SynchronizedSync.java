package zty.practise.concurrency.synchronizedtest;

import java.util.List;

import com.google.common.collect.Lists;

/**
 * synchronized除了可以实现互斥以及hp规则保证的可见性以外（见SyncchonizedBase）
 * 还可以保证同步（只有一个条件变量） 
 * synchronized的通过 object的wait  notify  notifyAll方法实现
 * 同步的经典实现方式是等待唤醒机制，而等待唤醒机制最经典的实现是生产者消费者模型
 * 而生产者消费者模型最经典的实现是阻塞队列
 * 理论上来说阻塞队列是需要两个
 * @author zhangtianyi
 */
public class SynchronizedSync {

	List<String> queue = Lists.newArrayList();
	private final Object lock = new Object();

	/**
	 * 生产：进队
	 * @author zhangtianyi
	 * @throws InterruptedException
	 */
	void produce() throws InterruptedException {
		synchronized(lock) {
			System.out.println(Thread.currentThread() + "生产者进入临界区");
			while(queue.size() == 5) {
				//阻塞等待唤醒并释放锁
				lock.wait();
			}
			queue.add("goods");
			System.out.println("queue-add");
			//All唤醒后因为要竞争锁，所以体现的效果也是随机唤醒一个，其它继续wait
			lock.notifyAll();
		}
		
	}
	
	/**
	 * 消费：出队
	 * @author zhangtianyi
	 * @throws InterruptedException
	 */
	void consume() throws InterruptedException {
		synchronized(lock) {
			System.out.println(Thread.currentThread() + "消费者进入临界区");
			while(queue.size() == 0) {
				System.out.println(Thread.currentThread() + "消费者wait");
				lock.wait();
				System.out.println(Thread.currentThread() + "消费者被唤醒");
			}
			Thread.sleep(5000);
			queue.remove(queue.size()-1);
			System.out.println("queue-remove");
			lock.notifyAll();
		}
	}
	
	/**
	 * 三次生产一次消费，消费之后将会唤醒，本意是唤醒生产者，但是此处由于只有一个条件变量
	 * 将会唤醒生产者，但是由于while检验（一定要用while不要用if，确保再检查条件成立），将会再次
	 * 休眠被唤醒的消费者线程，所以使用synchronized可以变相的实现阻塞队列，但是不严谨
	 * @author zhangtianyi
	 * @param args
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws InterruptedException {
		SynchronizedSync sync = new SynchronizedSync();
		Thread A = new Thread(() -> { try {
			sync.consume();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}});
		
		Thread B = new Thread(() -> { try {
			sync.consume();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}});
		
		Thread C = new Thread(() -> { try {
			sync.consume();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}});
		
		Thread D = new Thread(() -> { try {
			sync.produce();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}});
		A.start();
		B.start();
		C.start();
		Thread.sleep(2000);
		D.start();
		Thread.sleep(10000);
		System.out.println("end...");
	}
}
