package com.hunteron.hessian;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 注册一个或多个消费线程的Map. 
 * 当缓存中存在数据就跑已注册的线程
 * <p>
 * 	注册的线程中需要做删除该map中的元素处理
 * </p>
 * 否则就如果为空，就等待.
 *
 * @param <K>
 *            the key type
 * @param <V>
 *            the value type
 * @author ming.peng
 * @date 2013-12-19
 * @since 4.0.0
 */
@SuppressWarnings("serial")
public class ConsumConcurrentHashMap<K, V> extends ConcurrentHashMap<K, V> {

	/** The lock, 锁,向Map中增加数据提醒休眠线程,及Map中无数据时让线程处于等待. */
	private final ReentrantLock lock = new ReentrantLock();
	
	/** Condition for waiting takes */
	private final Condition notEmpty = lock.newCondition();

	/** 默认设置线程池,用于执行任务, 线程执行器. */
	private ExecutorService executor = Executors.newFixedThreadPool(5);

	/** The runnables. */
	private final List<ConsumerRunnable> runnables = new CopyOnWriteArrayList<ConsumerRunnable>();

	/**
	 * Instantiates a new drives concurrent hash map.
	 */
	public ConsumConcurrentHashMap() {
		super();
	}

	/**
	 * Instantiates a new drives concurrent hash map.
	 *
	 * @param initialCapacity
	 *            the initial capacity
	 * @param loadFactor
	 *            the load factor
	 * @param concurrencyLevel
	 *            the concurrency level
	 */
	public ConsumConcurrentHashMap(int initialCapacity, float loadFactor, int concurrencyLevel) {
		super(initialCapacity, loadFactor, concurrencyLevel);
	}

	/**
	 * Instantiates a new drives concurrent hash map.
	 *
	 * @param initialCapacity
	 *            the initial capacity
	 * @param loadFactor
	 *            the load factor
	 */
	public ConsumConcurrentHashMap(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
	}

	/**
	 * Instantiates a new drives concurrent hash map.
	 *
	 * @param initialCapacity
	 *            the initial capacity
	 */
	public ConsumConcurrentHashMap(int initialCapacity) {
		super(initialCapacity);
	}

	/**
	 * Instantiates a new drives concurrent hash map.
	 *
	 * @param m
	 *            the m
	 */
	public ConsumConcurrentHashMap(Map<? extends K, ? extends V> m) {
		super(m);
	}

	/**
	 * Gets the executor.
	 *
	 * @return the executor
	 */
	public ExecutorService getExecutor() {
		return executor;
	}

	/**
	 * Sets the executor.
	 *
	 * @param executor
	 *            the new executor
	 */
	public void setExecutor(ExecutorService executor) {
		this.executor = executor;
	}

	/**
	 * Regsiter runnable.
	 *
	 * @param runnable
	 *            the runnable
	 */
	public void regsiterRunnable(Runnable runnable) {
		ConsumerRunnable run = new ConsumerRunnable(runnable);
		this.runnables.add(run);
		executor.execute(run);
	}

	/**
	 * Regsiter runnable.
	 *
	 * @param runName
	 *            the run name
	 * @param runnable
	 *            the runnable
	 */
	public void regsiterRunnable(String runName, Runnable runnable) {
		ConsumerRunnable run = new ConsumerRunnable(runName, runnable);
		this.runnables.add(run);
		executor.execute(run);
	}

	/**
	 * 删除线程，但不会立马结束线程
	 * Removes the runnable.
	 *
	 * @param runnable
	 *            the runnable
	 */
	public void removeRunnable(String runName) {
		for (ConsumerRunnable run : this.runnables) {
			if (null != run.getRunName() && run.getRunName().equals(runName)) {
				run.setActive(false); // 标记线程退出
				this.runnables.remove(run); // 把线程对象从队列中删除
			}
		}
	}

	/**
	 * 删除线程，但不会立马结束线程
	 * Removes the runnable.
	 *
	 * @param runnable
	 *            the runnable
	 */
	public void removeRunnable(Runnable runnable) {
		for (ConsumerRunnable run : this.runnables) {
			if (run.getRunnable().equals(runnable)) {
				run.setActive(false); // 标记线程退出
				this.runnables.remove(run); // 把线程对象从队列中删除
			}
		}
	}


	/*
	 * (non-Javadoc)
	 *
	 * @see java.util.concurrent.ConcurrentHashMap#put(java.lang.Object,
	 * java.lang.Object)
	 */
	@Override
	public V put(K key, V value) {
		V v = super.put(key, value);
		notifyConsumerRunnables();
		return v;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.util.concurrent.ConcurrentHashMap#putIfAbsent(java.lang.Object,
	 * java.lang.Object)
	 */
	@Override
	public V putIfAbsent(K key, V value) {
		V v = super.putIfAbsent(key, value);
		notifyConsumerRunnables();
		return v;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.util.concurrent.ConcurrentHashMap#putAll(java.util.Map)
	 */
	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		super.putAll(m);
		notifyConsumerRunnables();
	}

	
	/**
	 * notify consumer threads
	 */
	private void notifyConsumerRunnables() {
		lock.lock();
		try{
			if (this.size() > this.runnables.size() * 1000) {
				notEmpty.signalAll();
			} else {
				notEmpty.signal();
			}
		}finally{
			lock.unlock();
		}
	}

	/**
	 * 消费线程
	 * 
	 * @author rocca.peng@hunteron.com
	 * @Description 
	 * @Date  2015年7月17日 下午6:00:46
	 */
	/**
	 * @author pm
	 *
	 */
	/**
	 * @author pm
	 *
	 */
	protected final class ConsumerRunnable implements Runnable {

		/** The run name. */
		private String runName;

		/** The runnable. */
		private Runnable runnable;
		
		/** The isNow. 表示是否继续执行任务, false表示不执行任务，true执行 */
		private boolean isNow = true;

		/** The isActive, 标识线程是否运行, false停止运行， true运行. */
		private boolean isActive = true;

		/** 日志 */
		private Logger logger = LoggerFactory.getLogger(getClass());
		
		/**
		 * Instantiates a new drives runnable.
		 */
		private ConsumerRunnable() {
		}

		/**
		 * Instantiates a new drives runnable.
		 *
		 * @param runnable
		 *            the runnable
		 */
		private ConsumerRunnable(Runnable runnable) {
			super();
			this.runnable = runnable;
		}

		/**
		 * Instantiates a new drives runnable.
		 *
		 * @param runName
		 *            the run name
		 * @param runnable
		 *            the runnable
		 */
		private ConsumerRunnable(String runName, Runnable runnable) {
			super();
			this.runnable = runnable;
			this.runName = runName;
		}

		/** 将注册进来的runnable执行#run()方法,完成后看是否需要有补充机制. */
		public void run() {
			while (this.isActive) {
				lock.lock();
				try {
					if (ConsumConcurrentHashMap.this.isEmpty()) {
						notEmpty.await();
					}
				} catch (Exception e) {
					logger.error("等待异常", e);
				} finally {
					lock.unlock();
				}
				try {
					if (isNow) {
						runnable.run();
					}
				} catch (Exception e) {
					logger.error("", e);
				}
			}
		}
		
		

		/**
		 * @return
		 */
		public boolean isNow() {
			return isNow;
		}

		
		/**
		 * @param isNow
		 */
		public void setNow(boolean isNow) {
			this.isNow = isNow;
		}

		/**
		 * Gets the Active.
		 * 
		 * @return
		 */
		public boolean isActive() {
			return isActive;
		}

		/**
		 * Sets the Active.
		 * 
		 * @param isActive
		 */
		public void setActive(boolean isActive) {
			this.isActive = isActive;
		}

		/**
		 * Gets the run name.
		 *
		 * @return the run name
		 */
		public String getRunName() {
			return runName;
		}

		/**
		 * Sets the run name.
		 *
		 * @param runName
		 *            the new run name
		 */
		public void setRunName(String runName) {
			this.runName = runName;
		}

		/**
		 * Gets the runnable.
		 *
		 * @return the runnable
		 */
		public Runnable getRunnable() {
			return runnable;
		}

		/**
		 * Sets the runnable.
		 *
		 * @param runnable
		 *            the new runnable
		 */
		public void setRunnable(Runnable runnable) {
			this.runnable = runnable;
		}

	}

	/**
	 * 停止所有任务,正在执行的任务会继续执行,关闭线程池
	 */
	public void shutdown() {
		// 将所有线程标记结束
		for (ConsumerRunnable run : runnables) {
			run.setActive(false);
		}
		// 如果存在休眠的线程，就唤醒所有线程，执行完毕，退出run方法
		lock.lock();
		try{
			notEmpty.signalAll();
		}finally{
			lock.unlock();
		}
		// 关闭线程执行器
		executor.shutdown();
	}
	
	/**
	 * 停止所有任务,尝试停止正在执行的线程，关闭线程池
	 */
	public List<Runnable> shutdownNow() {
		// 将所有线程标记结束
		for (ConsumerRunnable run : runnables) {
			run.setActive(false);
			run.setNow(false); // 在结束时是否执行任务
		}
		// 如果存在休眠的线程，就唤醒所有线程，执行完毕，退出run方法
		lock.lock();
		try{
			notEmpty.signalAll();
		}finally{
			lock.unlock();
		}
		// 关闭线程执行器
		return executor.shutdownNow();
	}

}
