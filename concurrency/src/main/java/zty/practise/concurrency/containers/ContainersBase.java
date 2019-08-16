package zty.practise.concurrency.containers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 许多Java容器不是并发安全的
 * 比如HashMap 因为存在扩容操作，所以存在更多的竞态条件...
 * 
 * Java提供并发容器保证单操作的并发安全（并不保证复合操作的并发安全）
 * 比如迭代器 hasNext()  next() 之间存在竞态条件
 * 
 * 
 * @author zhangtianyi
 *
 */
public class ContainersBase {

	/**
	 * 进入看源码可知 所有的实例方法都被synchronized（同一个锁）包起来执行了
	 */
	void wrapWithSynchronized() {
		List list = Collections.synchronizedList(new ArrayList());
		Set set = Collections.synchronizedSet(new HashSet());
		Map map = Collections.synchronizedMap(new HashMap());
	}
	
	/**
	 * 复合操作存在竞态条件
	 * 必须加锁
	 * 
	 */
	void safeIterator() {
		List list = Collections.synchronizedList(new ArrayList());
		synchronized(list) {
			Iterator i = list.iterator();
			while(i.hasNext()) {
				i.next();
			}
		}
	}
}
