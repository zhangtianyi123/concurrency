package zty.practise.concurrency.countdownlatch;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * JDK8提供CompletableFuture支持异步编程 其API能够方便的描述复杂的同步关系模型
 * 并且结合Java8的Lambda还能达成语法层面的简洁易用（关注异常处理）
 * 
 * 可描述串行关系、并行关系、AND汇聚关系和OR汇聚关系等
 * 
 * 默认情况下CompletableFuture会使用公共的ForkJoinPool线程池
 * 
 * CompletableFuture实现了Future接口 实现了CompleionState接口
 * 
 * @author zhangtianyi
 *
 */
public class CompletableFutureBase {

	//thenApply是串行的
	private CompletableFuture<Integer> futureTwo = CompletableFuture.supplyAsync(() -> {
		return 2;
	}).thenApply(r -> r+1);
	
	private CompletableFuture<Integer> futureThree = CompletableFuture.supplyAsync(() -> {
		return 3;
	}).thenApply(r -> r+1);;
	
	//and汇聚关系
	private CompletableFuture<Integer> andResult = futureTwo.thenCombine(futureThree, (u, res) -> {
		try {
			System.out.println(futureTwo.get() * futureThree.get());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	});

	public static void main(String[] args) {
		CompletableFutureBase base = new CompletableFutureBase();
		base.andResult.join();
	}

}
