package zty.practise.concurrency.guardedsuspension;

import java.util.List;


import com.google.common.collect.Lists;

import zty.practise.concurrency.synchronizedtest.SynchronizedSync;

/**
 * 保护性暂停——实现等待通知机制
 * 
 * 等待通知机制本质是两个线程之间的异步转同步 （因为某种业务需要）
 * if(异步条件满足) {
 *     同步执行
 * }
 * 
 * 对于同步问题一定要用广义的生产-消费的模式去分析
 * 比如如下其实生产者和消费者互为生产者，消费者：生产者在消费消费者创造的”空间“
 * 
 * signalAll会有问题 必须signal
 * 因为get 和 onchange临界区分开了  all中第一个获取锁的线程在临界区内没有onchange
 * 一旦释放锁，在onchange之前 ，其它唤醒的线程就会直接通过条件判断，造成错误
 * 
 * @author zhangtianyi
 *
 */
public class GuardedSync {

	List<String> queue = Lists.newArrayList();
	
	final GuardedObjectWithSignalAllError<List<String>> go = new GuardedObjectWithSignalAllError<>();
	
	void produce() throws InterruptedException {
		go.get(t -> t.size() < 5, queue);
		go.onChanged(c -> c.add("goods"),queue);
	}

	/**
	 * signalAll会唤醒所有等待线程，其中一个进入临界区 其他等待互斥锁
	 * 所以需要在第一个在临界区内再次改变条件，才能再次满足while条件判断 wait
	 * （其它进入临界区不是从代码开端开始进入 而是从wait代码处开始进入）
	 * 
	 * @throws InterruptedException
	 */
	void consume() throws InterruptedException {
		go.get(t -> t.size() > 0, queue);
		go.onChanged(c -> c.remove(c.size() - 1), queue);
	}
	
	public static void main(String[] args) throws InterruptedException {
		GuardedSync sync = new GuardedSync();
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
		Thread.sleep(5000);
		D.start();
		Thread.sleep(10000);
		System.out.println("end...");
	}
}
