package zty.practise.concurrency.locktest;

import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.google.common.collect.Maps;

/**
 * 读写锁针对读多写少的场景
 * 
 * 读读不互斥 读写互斥 写写互斥
 * 
 * @author zhangtianyi
 *
 */
public class ReadWiriteLockAPI {

	static class MyCache<K, V> {
		static long count = 0;
		
		final Map<K, V> m = Maps.newHashMap();
		final ReadWriteLock rwl = new ReentrantReadWriteLock();
		
		final Lock rlock = rwl.readLock();
		final Lock wlock = rwl.writeLock();
		
		V get(K key) {
			V v = null;
			rlock.lock();
			try {
				v = m.get(key);
			} finally {
				rlock.unlock();
			}
			
			//缓存命中
			if(v != null) {
				return v;
			}
			
			//等待以放大竞态条件发生概率
			if(count < 3) {
			  try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
			}
			
			//缓存未命中
			wlock.lock();
			try {
				//查询数据库
				v = getValueByDB(key);
				//查询结果放入缓存
				m.put(key, v);
			} finally {
				wlock.unlock();
			}
			
			return v;
		}
		
		V put(K key, V value) {
			wlock.lock();
			try {
				return m.put(key, value);
			} finally {
				wlock.unlock();
			}
		}
		
		private V getValueByDB(K key) {
			System.out.println("从数据库读取" + key + "的值");
			return (V)(key + "'s value");
		}
		
	}
	
	/**
	 * 第一组判断缓存中没有数据后等待
	 * 第二组判断缓存中没有数据，从数据库取出数据填入缓存
	 * 第一组继续执行，将会重复的从数据库中取出数据
	 * 第三组数据发现缓存命中直接返回
	 * 
	 * 改进方式：在临界区中（写锁中）再次判断缓存命中，可以消除竞态条件
	 */
	public static void testCache() {
		MyCache<String, String> cache = new MyCache<>();
		Executor executor = Executors.newFixedThreadPool(6);
		//数据未加载，缓存击穿
		executor.execute(() -> { System.out.println(cache.get("Mes001")); cache.count++;});
		executor.execute(() -> { System.out.println(cache.get("Mes002")); cache.count++;});
		executor.execute(() -> { System.out.println(cache.get("Mes003")); cache.count++;});
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//直接从缓存读取
		executor.execute(() -> { System.out.println(cache.get("Mes001")); cache.count++;});
		executor.execute(() -> { System.out.println(cache.get("Mes002")); cache.count++;});
		executor.execute(() -> { System.out.println(cache.get("Mes003")); cache.count++;});
		
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//直接从缓存读取
		//但是由于判断缓存命中不在临界区中
		//所以if(缓存不存在)则从数据库读——存在竞态条件
		executor.execute(() -> { System.out.println(cache.get("Mes001")); });
		executor.execute(() -> { System.out.println(cache.get("Mes002")); });
		executor.execute(() -> { System.out.println(cache.get("Mes003")); });
		
	}
	
	public static void main(String[] args) {
		testCache();
	}
}
