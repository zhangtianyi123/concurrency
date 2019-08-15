package zty.practise.concurrency.synchronizedtest;

/**
 * 本类是测试API使用对synchronized的优化
 * 
 * 主要分为两个层面
 * 1 减小synchoronized的临界区大小，锁住共享变量即可
 * 2 减小synchoronized锁的粒度，锁对象尽可能小(比如static将会锁住所有共享变量，但是this只会锁住当前实例)
 * 
 * @author zhangtianyi
 * 
 *  */
public class SynchronizedAPIOpimization {

	/**
	 * 共享变量洗手间
	 */
	public int toilet;
	
	/**
	 * 共享变量开水房
	 */
	public int waterRoom;
	
	/**
	 * 同时锁住洗手间和开水房
	 * 程序员们离开工位一趟，往往又要接水，又要上厕所
	 * 但是同时构筑大的临界区，只要进入厕所，别人开水房也不能进人。只要进入开水房，别人厕所也不能进入
	 */
	/**
	 * 接水
	 */
	public synchronized void goToilet() {
		waterRoom++;
	}
	/**
	 * 上厕所
	 */
	public synchronized void goWaterRoom() {
		toilet++;
	}
	
	/**
	 * 分别给洗手间和开水房上锁
	 */
	final Object waterRoomLock = new Object();
	final Object toiletLock = new Object();
	public synchronized void goToiletAndWaterRoom() {
		goToilet();
		goWaterRoom();
	}
	
}
