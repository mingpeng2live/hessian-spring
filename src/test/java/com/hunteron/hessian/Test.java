package com.hunteron.hessian;

import java.util.Map.Entry;
import java.util.Random;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.druid.sql.dialect.oracle.ast.expr.OracleSizeExpr.Unit;

public class Test {

	private static final Logger logger = LoggerFactory.getLogger(Test.class);
	private static final ConsumConcurrentHashMap<String, String> map = new ConsumConcurrentHashMap<>();
	
	public static void main(String[] args) {
		
		Runnable runnable = new Runnable() {
			
			Random r = new Random(); 

			@Override
			public void run() {
				long s = System.currentTimeMillis();
				long e = 0l;
				while((e = System.currentTimeMillis()) - s < 10 * 60 * 1000) {
					map.put(UUID.randomUUID().toString(), RandomStringUtils.randomAlphanumeric(5));
//					try {
//						long nextLong = r.nextInt(2); // 该地方不能为1，如果为1，就没有睡眠效果
//						Thread.sleep(nextLong);
//					} catch (InterruptedException es) {
//						es.printStackTrace();
//					}
				}
			}
		};
//		Runnable runnable2 = new Runnable() {
//			
//			Random r = new Random(); 
//			
//			@Override
//			public void run() {
//				long s = System.currentTimeMillis();
//				long e = 0l;
//				while((e = System.currentTimeMillis()) - s < 10 * 60 * 1000) {
//					map.put(UUID.randomUUID().toString(), RandomStringUtils.randomAlphanumeric(5));
////					try {
////						long nextLong = r.nextInt(2); // 该地方不能为1，如果为1，就没有睡眠效果
////						Thread.sleep(nextLong);
////					} catch (InterruptedException es) {
////						es.printStackTrace();
////					}
//				}
//			}
//		};
		
		Thread thread = new Thread(runnable);
		thread.start();
//		Thread thread2 = new Thread(runnable2);
//		thread2.start();
		
		
		map.regsiterRunnable(new Runnable() {
			
			private int i = 0;
			
			@Override
			public void run() {
				for (Entry<String, String> entry : map.entrySet()) {
//					logger.info(i + "\t" + entry.getKey() + "\t" + entry.getValue() + "\t" + map.size());
					map.remove(entry.getKey());
				}
			}
		});
		
		map.regsiterRunnable(new Runnable() {
			
			private int i = 0;
			
			@Override
			public void run() {
				for (Entry<String, String> entry : map.entrySet()) {
//					logger.info(i + "\t" + entry.getKey() + "\t" + entry.getValue() + "\t" + map.size());
					map.remove(entry.getKey());
				}
			}
		});
		

		ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
		
		ses.scheduleAtFixedRate(new Runnable() {
			
			@Override
			public void run() {
				logger.info("\t" + map.size());
			}
			
		}, 0, 10, TimeUnit.MILLISECONDS);
		
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		map.shutdown();
	}
	
	
	
}
