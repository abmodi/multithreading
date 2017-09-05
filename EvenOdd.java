public class EvenOdd {
	static Object lock = new Object();
	static int cnt = 0;

	static class EvenRunnable implements Runnable {
		public void run() {
			synchronized(lock) {
				while (cnt <= 100) {
					try {
						while(cnt % 2 == 1) {
							lock.wait();
						}
						System.out.println(cnt);
						++cnt;
						lock.notify();
					} catch (InterruptedException ie) {

					}
				}
			}
		}
	}

	static class OddRunnable implements Runnable {
		public void run() {
			synchronized(lock) {
				while (cnt <= 100) {
					try {
						while(cnt % 2 == 0) {
							lock.wait();
						}
						System.out.println(cnt);
						++cnt;
						lock.notify();
					} catch (InterruptedException ie) {

					}
				}
			}
		}
	}

	public static void main(String[] args) throws Exception {
		Thread evenThread = new Thread(new EvenRunnable(), "Even");
		Thread oddThread = new Thread(new OddRunnable(), "Odd");

		evenThread.start();
		oddThread.start();

		oddThread.join();
		evenThread.join();
	}
}