package zty.practise.concurrency.containers;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

import com.google.common.collect.Lists;

/**
 * 
 * 写时复制是一宗在读多写少场景下保证并发安全的模式
 * 
 * 在写的时候将共享变量新复制一份出来，读的时候无锁
 * 
 * 只适用于写操作非常少的场景，并且能够容忍读写暂时的不一致 并且迭代器不能增删改（只读）因为只是一个快照
 * 
 * 写的时候复制出来写，写完了再将引用指向新的数组
 * 
 * @author zhangtianyi
 *
 */
public class CopyOnWriteListTest {

	class MySubCopyOnWriteArrayList<E> extends MyCopyOnWriteArrayList<E> {

		/**
		 * 抽取出来在本类展示改动
		 * @param e
		 * @return
		 */
		public boolean myAdd(E e) {
			final ReentrantLock lock = this.lock;
			lock.lock();
			try {
				Object[] elements = getArray();
				int len = elements.length;
				Object[] newElements = Arrays.copyOf(elements, len + 1);
				newElements[len] = e;
				
				//制造不一致性
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				setArray(newElements);
				return true;
			} finally {
				lock.unlock();
			}
		}
	}
	
	List<String> list = new MySubCopyOnWriteArrayList();
	
	Executor executor = Executors.newFixedThreadPool(2);
	
	private void addOneElement() {
		list.add("d");
	}
	
	private void iterativeList() {
		Iterator it = list.iterator();
		while(it.hasNext()) {
			System.out.println(it.next());
		}
		System.out.println("--------------------");
	}

	/**
	 * 测试写时复制可能出现的不一致
	 * 第一次迭代无法展示出add的数据
	 */
	void testInconsistent() {
		list.add("a");
		list.add("b");
		list.add("c");
		
		executor.execute(() -> { addOneElement(); });
		executor.execute(() -> { iterativeList(); });
		
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		executor.execute(() -> { iterativeList(); });
	}
	
	public static void main(String[] args) {
		CopyOnWriteListTest test = new CopyOnWriteListTest();
		test.testInconsistent();
	}

}
