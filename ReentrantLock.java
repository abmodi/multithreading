public class ReentrantLock {
	private boolean locked = false;
	private int count = 0;
	private Thread lockingThread = null;

	public synchronized void lock() {
		try {
			while (locked && lockingThread != Thread.currentThread()) {
				wait();
			}
			locked = true;
			lockingThread = Thread.currentThread();
			++count;
		} catch(InterruptedException e) {

		}
	}

	public synchronized void unlock() {
		if (lockingThread != Thread.currentThread()) {
			throw new RuntimeException("IllegalStateTransition");
		}

		--count;

		if (count == 0) {
			lockingThread = null;
			locked = false;
			notify();
		}
	}
}