public class SimpleLock {
	private boolean isLocked = false;
	private final Object lock = new Object();
	private Thread lockingThread;

	public void lock() {
		synchronized(lock) {
			try {
				while (isLocked) {
					lock.wait();
				}
				System.out.println("Taking a lock " + Thread.currentThread().getName());
				isLocked = true;
				lockingThread = Thread.currentThread();
			} catch (InterruptedException ie) {

			}
		}
	}

	public void unlock() {
		synchronized(lock) {
			if (lockingThread != Thread.currentThread()) {
				throw new RuntimeException("Invalid State transition");
			}
			isLocked = false;
			lockingThread = null;
			System.out.println("Releasing a lock " + Thread.currentThread().getName());
			lock.notify();
		}
	}
}