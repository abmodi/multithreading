import java.util.*;

public class ReadWriteLock {
	private Object lock = new Object();
	boolean readLocked = false;
	boolean writeLocked = false;
	Map<Thread, Integer> threadLockMap = new HashMap<>();
	int pendingWriteRequests = 0;
	Thread writeLockedThread = null;
	int writeLockedCount = 0;

	public void readLock() {
		synchronized(lock) {
			try {
				Thread currentThread = Thread.currentThread();
				while(shouldReadWait(currentThread)) {
					lock.wait();
				}
				System.out.println("Taking read lock: " + currentThread.getName());
				readLocked = true;
				int lockCount = 0;
				if (threadLockMap.get(currentThread) != null) {
					lockCount = threadLockMap.get(currentThread);
				}
				threadLockMap.put(currentThread, lockCount + 1);
			} catch (InterruptedException e) {

			}
		}
	}

	private boolean shouldReadWait(Thread currentThread) {
		if (writeLockedThread == currentThread) {
			return false;
		}

		if (writeLocked) {
			return true;
		}

		if (threadLockMap.get(currentThread) != null) {
			return false;
		}

		if (pendingWriteRequests > 0) {
			return true;
		}
		return false;
	}

	private boolean shouldWriteWait(Thread currentThread) {
		if (writeLocked && writeLockedThread != currentThread) {
			return true;
		}

		if (readLocked && threadLockMap.size() == 1 && threadLockMap.get(currentThread) != null) {
			return false;
		}

		if (readLocked) {
			return true;
		}

		return false;
	}

	public void readUnlock() {
		synchronized(lock) {
			Thread currentThread = Thread.currentThread();
			if (threadLockMap.get(currentThread) != null) {
				int lockCount = threadLockMap.get(currentThread);
				--lockCount;

				if (lockCount == 0) {
					threadLockMap.remove(currentThread);
				}
				
				if (threadLockMap.size() == 0) {
					readLocked = false;
					lock.notifyAll();
				}
			} else {
				throw new RuntimeException("IllegalStateTransition");
			}
		}
	}

	public void writeLock() {
		synchronized(lock) {
			try {
				++pendingWriteRequests;
				while(shouldWriteWait(Thread.currentThread())) {
					lock.wait();
				}
				--pendingWriteRequests;
				System.out.println("Taking write lock: " + Thread.currentThread().getName());
				writeLocked = true;
				writeLockedThread = Thread.currentThread();
				++writeLockedCount;
			} catch(InterruptedException e) {

			}
		}
	}

	public void writeUnlock() {
		synchronized(lock) {
			if (Thread.currentThread() != writeLockedThread) {
				throw new RuntimeException("IllegalStateTransition");
			}

			--writeLockedCount;
			if (writeLockedCount == 0) {
				writeLocked = false;
				writeLockedThread = null;
				lock.notifyAll();
			}
		}
	}
}