import java.util.*;

public class MyBlockingQueue {
	Queue<Integer> queue = new LinkedList<Integer>();
	int size;

	public MyBlockingQueue(int size) {
		this.size = size;	
	}

	public synchronized void push(int x) {
		try {
			while (queue.size() == size) {
				wait();
			}
			queue.add(x);
			if (queue.size() == 1) {
				notify();
			}
		} catch(InterruptedException e) {

		}
	}

	public synchronized int pop(int x) {
		try {
			while(queue.size() == 0) {
				wait();
			}
			if (queue.size() == size) {
				notify();
			}

			return queue.remove();
		} catch(InterruptedException e) {

		}
		return -1;
	}
}