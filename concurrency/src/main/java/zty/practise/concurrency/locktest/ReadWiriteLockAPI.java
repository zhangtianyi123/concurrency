package zty.practise.concurrency.locktest;

import java.util.Map;
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
	
	public static void testCache() {
		MyCache<String, String> cache = new MyCache<>();
		//数据未加载，缓存击穿
		System.out.println(cache.get("Mes001"));
		System.out.println(cache.get("Mes002"));
		System.out.println(cache.get("Mes003"));
		
		//直接从缓存读取
		System.out.println(cache.get("Mes001"));
		System.out.println(cache.get("Mes002"));
		System.out.println(cache.get("Mes003"));
		
	}
	
	public static void main(String[] args) {
		testCache();
	}
}
