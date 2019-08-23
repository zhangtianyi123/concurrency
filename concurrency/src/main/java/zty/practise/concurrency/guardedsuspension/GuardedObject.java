package zty.practise.concurrency.guardedsuspension;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.function.Predicate;

import com.google.common.collect.Maps;

/**
 * 
 * TODO
 * 解决并发onchange时候搭配signalAll的错误
 * 
 * @author zhangtianyi
 *
 * @param <T>
 */
class GuardedObject<T> {
	// 受保护的对象
	T obj;
	final Lock lock = new ReentrantLock();
	final Condition done = lock.newCondition();
	final int timeout = 60;
	final static Map<Object, GuardedObject> gos = Maps.newConcurrentMap();

	GuardedObject() {
	}
	
	<K> GuardedObject(K key) {
		GuardedObject go = new GuardedObject();
		gos.put(key, go);
	}
	
	// 获取受保护对象
	T get(Predicate<T> p, T obj) {
		lock.lock();
		try {
			this.obj = obj;
			while (!p.test(obj)) {
				done.await(timeout, TimeUnit.SECONDS);
			}
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
			done.signal();
		} finally {
			lock.unlock();
		}
	}
}
