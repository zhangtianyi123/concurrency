package zty.practise.concurrency.locktest;

import java.util.concurrent.locks.StampedLock;

/**
 * Java8 推出的StamedLock相对于读写锁多了一种乐观读的模式
 * 乐观读这个动作是无锁的
 * StampedLock不支持重入
 * 
 * 悲观锁的临界区为 【读 运算  写】
 * 乐观锁的临界区为 无锁读  无锁运算  【判断 写】
 * 那么乐观读的临界区则是：无锁读 【判断】  运算  （仅仅保证读本身不被打断，读到的数据一致即可）
 *              失败路径： 无锁读 【判断】 （上读锁  运算   释放读锁）        
 *              
 * 类比数据库中的独占锁，共享锁和乐观锁
 * 
 * 意味着在乐观读的时候是可以被写线程修改的
 * 
 * @author zhangtianyi
 *
 */
public class StampedLockAPI {
    
	private int count = 0;
	
	final StampedLock slock = new StampedLock();
	
	//读
	public int getCount() {
		return count;
	}
	
	//写
	public void setCount(int x) {
		long stamp = slock.writeLock();
		System.out.println("修改count = " + x);
		try {
			count = x;
		} finally {
			slock.unlockWrite(stamp);
		}
	}
	
	//乐观读，乐观读期间共享变量被修改则升级为读锁
	public int getAndIncrement() {
		int value = 0;
		
		//乐观读
		long stamp = slock.tryOptimisticRead();
		value = getCount();
		System.out.println("乐观读" + stamp);
		
		//扩大竞态条件触发概率
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//判断
		if(!slock.validate(stamp)) {
			//升级为读锁
			stamp = slock.readLock();
			System.out.println("读锁" + stamp);
			try {
				value = getCount();
			} finally {
				slock.unlockRead(stamp);
			}
		}
		
		count++;
		return count;
	}
	
	public void OpimisticReadWithOutWrite() {
		System.out.println("result= " + getAndIncrement());
	}
	
	public void OptimisticReadWithWrite() {
		new Thread(() -> {     System.out.println("result= " + getAndIncrement());   }).start();
		
		setCount(2);
	}
	
	public static void main(String[] args) {
		StampedLockAPI api = new StampedLockAPI();
		
		//乐观读的时候没有出现写
//		api.OpimisticReadWithOutWrite();
		
		//乐观读的时候发生了写动作,会升级为悲观读，并且会基于写之后的值重新运算
		api.OptimisticReadWithWrite();
	}
}
