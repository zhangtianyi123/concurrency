package zty.practise.concurrency.guardedsuspension;

import java.util.List;


import com.google.common.collect.Lists;

import zty.practise.concurrency.synchronizedtest.SynchronizedSync;

/**
 * 保护性暂停——实现等待通知机制
 * 
 * @author zhangtianyi
 *
 */
public class GuardedSync {

	List<String> queue = Lists.newCopyOnWriteArrayList();
	final GuardedObject<List<String>> go = new GuardedObject<>();

	void produce() throws InterruptedException {
		//等待条件满足
		go.get(t -> t.size() < 5, queue);
		queue.add("goods");
		System.out.println("queue-add");
		//唤醒
		go.onChanged(queue);
	}

	void consume() throws InterruptedException {
		//等待条件满足
		go.get(t -> t.size() > 0, queue);
		queue.remove(queue.size()-1);
		System.out.println("queue-remove");
		//唤醒 
		go.onChanged(queue);
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
		Thread.sleep(2000);
		D.start();
		Thread.sleep(10000);
		System.out.println("end...");
	}
}
