package zty.practise.concurrency.containers;

import java.util.concurrent.ConcurrentHashMap;

/**
 * ConcurrentHashMap在Java7是采用分段锁机制，对segment数组分段上锁，细粒度上锁的思想
 * 
 * Java8改为（数组+链表+红黑树）+CAS （优化的synchronized）
 * 
 * put操作可能扩容 也可能扩展为红黑树 存在更多的竞态条件
 * 因为多线程环境下，使用Hashmap进行put操作会引起死循环，导致CPU利用率接近100%（1.8-）
 * 链表成环
 * 
 * @author zhangtianyi
 *
 */
public class ConcurrentHashMapTest {
	
	final ConcurrentHashMap cmap = new ConcurrentHashMap();
	
	void test() {
		cmap.get(null);
	}

	/**
	 * TODO
	 * @author zhangtianyi
	 *
	 */
	class MyConcurrentHashMap {
		
	}
}
