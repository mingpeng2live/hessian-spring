package com.hunteron.hessian;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class TestFutureTask {
	public static void main(String[] args) throws Exception {
		final ExecutorService exec = Executors.newFixedThreadPool(5);

		Runnable runnable = new Runnable() {

			@Override
			public void run() {
				System.out.println("abc");
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.out.println("ddddddd");
			}
		};

		Callable<String> call = new Callable<String>() {
			public String call() throws Exception {
				System.out.println("aa");
				Thread.sleep(1000 * 3);
				return "Other less important but longtime things.";
			}
		};


//		FutureTask<String> task = new FutureTask<String>(runnable, null);

		
//		Future<String> task = exec.submit(call);
		Future<Callable<String>> task = exec.submit(runnable, call);
//		exec.execute(runnable);

//		exec.execute(runnable);
		// 重要的事情
//		Thread.sleep(1000 * 3);
//		System.out.println("Let's do important things.");
		// 不重要的事情
//		String obj = task.get();
//		System.out.println(System.currentTimeMillis());
//		String obj = task.get().call();
//		System.out.println(System.currentTimeMillis() + "\t" + obj);
		// 关闭线程池
		exec.shutdown();
	}
}