import java.util.*;
import java.util.concurrent.*;

public class ThreadPool {
	BlockingQueue<Runnable> tasks;
	int threadCount;
	List<Thread> threads = new LinkedList<Thread>();
	boolean isShuttingDown = false;

	public ThreadPool(int size, int capacity) {
		threadCount = size;
		tasks = new ArrayBlockingQueue<>(capacity);
		for (int i = 0; i < size; ++i) {
			threads.add(new Thread(new TaskRunner()));
		}
	}

	public synchronized void shutdown() {
		isShuttingDown = true;
		for (Thread thread : threads) {
			thread.interrupt();
		}
	}

	public synchronized void submit(Runnable task) throws InterruptedException {
		if (!isShuttingDown) {
			tasks.put(task);
		} else {
			throw new IllegalStateException("Threadpool has been stopped");
		}
	}

	class TaskRunner implements Runnable {
		public void run() {
			try {
				while (!Thread.currentThread().isInterrupted()) {
					Runnable task = tasks.take();
					task.run();
				}
			} catch(InterruptedException e) {

			}
		}
	}
}