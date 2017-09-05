public class ThreadTest {
	static class Thread1 implements Runnable {
		int cnt = 1;
		ReadWriteLock lock = new ReadWriteLock();
		
		public void run() {
			lock.readLock();
			cnt += 1;
			try {
				Thread.sleep(100);
			} catch(InterruptedException ie) {

			}
			lock.readUnlock();
			printValue();
		}

		public void printValue() {
			lock.writeLock();
			System.out.println("Count = " + cnt);
			lock.writeUnlock();
		}
	}

	public static void main(String[] args) throws Exception {
		Thread1 t = new Thread1();
		Thread t1 = new Thread(t, "T1");
		Thread t2 = new Thread(t, "T2");

		t1.start();
		t2.start();

		t1.join();
		t2.join();
	}
}