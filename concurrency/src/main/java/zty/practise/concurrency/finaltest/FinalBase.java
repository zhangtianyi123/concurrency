package zty.practise.concurrency.finaltest;


/**
 * 由于存在cpu缓存于乱序执行等引发的可见性和原子性问题
 * 
 * JMM对其做了约束
 * 主要参照VolatileVisibility
 * 
 * 除此之外final对其初始化也限定了规则，只需要保证其安全发布避免逃逸
 * 那么构建对象之后可见其初始化值
 * 
 * @author zhangtianyi
 *
 */
public class FinalBase {
	static class Global {
		static FinalBase base;
	}
	
	final int count;
	private static FinalBase base;
	
	/**
	 * 编译器保证final不被重排序到构造函数之外
	 * this赋予全局变量谓之构造逃逸
	 */
	public FinalBase() {
		Global.base = this;
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		count = 1;
	}

	public static void main(String[] args) {
		//调用构造函数写
		new Thread(() -> {
			base = new FinalBase();
		}).start();
		
		new Thread(() -> {
			System.out.println(Global.base);
			System.out.println(Global.base.count);
		}).start();
		
	}
}
