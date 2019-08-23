package zty.practise.concurrency.guardedsuspension;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class GuardedObjectWithSignalAllError<T> {

	// 受保护的对象
	T obj;
	final Lock lock = new ReentrantLock();
	final Condition done = lock.newCondition();
	final int timeout = 60;

	// 获取受保护对象
	T get(Predicate<T> p, T obj) {
		lock.lock();
		try {
			this.obj = obj;
			while (!p.test(obj)) {
				done.await(timeout, TimeUnit.SECONDS);
			}
			// System.out.println("--------消费者已经唤醒等待一会儿再消费----------");
			// Thread.sleep(50000);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		} finally {
			lock.unlock();
		}
		return obj;
	}

	// 事件通知方法
	void onChanged(Consumer<T> c, T obj) {
		lock.lock();
		try {
			this.obj = obj;
			c.accept(obj);
			done.signalAll();
		} finally {
			lock.unlock();
		}
	}
}
