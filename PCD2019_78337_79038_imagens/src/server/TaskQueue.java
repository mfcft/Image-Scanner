package server;

import java.util.LinkedList;
import java.util.Queue;

public class TaskQueue {

	private Queue<Task> queue;
	
	public TaskQueue() {
		queue = new LinkedList<Task>();
	}
	
	
	public synchronized void addLast(Task task) {
		queue.offer(task);
		notifyAll();
	}
	
	public synchronized Task removeFirst() throws InterruptedException {
	while(queue.isEmpty()) {
			wait();
		}
		
		return queue.poll();
		
	}
	
}
