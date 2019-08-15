package zty.practise.concurrency.atomictest;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * CAS 即 Compare And Swap 比较和交换
 * 是实现同步的原子指令
 * 
 * Java的CAS操作都是通过Unsafe类的native方法实现的。更底层基于cpu指令实现
 * CAS往往搭配轮询即自旋
 * 是Java的乐观锁实现，是一种无锁方案
 * 
 * 存在ABA问题——解决方案版本号
 * 
 * 悲观锁的临界区为 【读 运算  写】
 * 乐观锁的临界区为 无锁读  无锁运算  【判断 写】
 * 
 * @author zhangtianyi
 *
 */
public class AtomicBase {
     private volatile static int count = 0;
     
     private static AtomicInteger atomiccount = new AtomicInteger(0);
	 
     public static void set() {
    	 count++;
     }
     
     public static void get() {
    	System.out.println(count); 
     }
     
     /**
      * 这类似于cas的使用模板
      * compareAndSet是基础方法需要搭配自旋
      * jdk也有封装了自旋逻辑的方法
      */
     public static void setByCompareAndSet() {
         int oldValue = 0;
    	 int newValue = 0;
    	 
    	 do {
    		  oldValue = atomiccount.get();
    		  newValue = atomiccount.get() + 1;
    	 } while(!atomiccount.compareAndSet(oldValue, newValue));
    	 
    	 
     }
     
     /**
      * 原子类的方法是原子的，但是复合使用却未必了，存在竞态条件
      */
     public static void setByCombinationMethod() {
    	 atomiccount.getAndSet(atomiccount.addAndGet(1));
     }
     
     /**
      * 当然有包装好的单个方法
      */
     public static void setByIncrement() {
    	 atomiccount.getAndIncrement();
     }
     
     /**
      * 测试不安全的多线程计数器
      * @return
      * @throws InterruptedException
      */
     private static boolean testUnSafe() throws InterruptedException {

 		Thread a = new Thread(() -> {
 			for (int j = 0; j < 1000; j++) {
 				AtomicBase.set();
 			}
 		});
 		Thread b = new Thread(() -> {
 			for (int j = 0; j < 1000; j++) {
 				AtomicBase.set();
 			}
 		});
 		a.start();
 		b.start();
 		
 		a.join();
 		b.join();

 		get();
 		return count == 2000;
 	}
     
     /**
      * 测试自旋+compareAndSet实现的线程安全计数器
      * @return
      * @throws InterruptedException
      */
     private static boolean testCombinationMethod() throws InterruptedException {

 		Thread a = new Thread(() -> {
 			for (int j = 0; j < 1000; j++) {
 				AtomicBase.setByCombinationMethod();
 			}
 		});
 		Thread b = new Thread(() -> {
 			for (int j = 0; j < 1000; j++) {
 				AtomicBase.setByCombinationMethod();
 			}
 		});
 		a.start();
 		b.start();
 		
 		a.join();
 		b.join();

 		System.out.println(atomiccount.get());
 		return count == 2000;
 	}
     
     /**
      * 测试原子类组合方法实现的线程不安全计数器
      * @return
      * @throws InterruptedException
      */
     private static boolean testCompareAndSet() throws InterruptedException {

 		Thread a = new Thread(() -> {
 			for (int j = 0; j < 1000; j++) {
 				AtomicBase.setByCompareAndSet();
 			}
 		});
 		Thread b = new Thread(() -> {
 			for (int j = 0; j < 1000; j++) {
 				AtomicBase.setByCompareAndSet();
 			}
 		});
 		a.start();
 		b.start();
 		
 		a.join();
 		b.join();

 		System.out.println(atomiccount.get());
 		return count == 2000;
 	}
     
     /**
      * 测试原子类单个方法实现的线程安全计数器
      * @return
      * @throws InterruptedException
      */
     private static boolean testIncrement() throws InterruptedException {

 		Thread a = new Thread(() -> {
 			for (int j = 0; j < 1000; j++) {
 				AtomicBase.setByIncrement();
 			}
 		});
 		Thread b = new Thread(() -> {
 			for (int j = 0; j < 1000; j++) {
 				AtomicBase.setByIncrement();
 			}
 		});
 		a.start();
 		b.start();
 		
 		a.join();
 		b.join();

 		System.out.println(atomiccount.get());
 		return count == 2000;
 	}
     
     
     /**
      * 模拟的CAS实现，其实是基于原语
      * @param expect
      * @param newValue
      * @return
      */
     private synchronized int myCAS(int expect, int newValue){
         // 读目前 count 的值
         int curValue = count;
         // 比较目前 count 值是否 == 期望值
         if(curValue == expect){
             // 如果是，则更新 count 的值
             count = newValue;
         }
         // 返回写入前的值
         return curValue;
     }
     
     public static void main(String[] args) throws InterruptedException {
    	 //测试（非原子的i++）不安全
    	 //testUnSafe();
    	 
    	 //测试原子类之使用基础方法自实现自旋
    	 //testCompareAndSet();
    	 
    	 //测试复合方法——线程不安全
    	 //testCombinationMethod();
    	 
    	 //测试单个方法——线程安全
    	 testIncrement();
	}
}
