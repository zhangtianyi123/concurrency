package zty.practise.concurrency.guardedsuspension;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Predicate;

class GuardedObject<T> {
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
			// 超时等待提条件满足
			while (!p.test(obj)) {
				done.await(timeout, TimeUnit.SECONDS);
			}
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		} finally {
			lock.unlock();
		}
		// 返回非空的受保护对象
		return obj;
	}

	// 事件通知方法
	void onChanged(T obj) {
		lock.lock();
		try {
			this.obj = obj;
			done.signalAll();
		} finally {
			lock.unlock();
		}
	}
}
