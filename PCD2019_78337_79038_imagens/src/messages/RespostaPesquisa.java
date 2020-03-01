package messages;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import server.Task;

public class RespostaPesquisa implements Serializable {
	
	private List<Task> tasksResults = new ArrayList<Task>();
	private int createdTasks;
	
	
	public RespostaPesquisa(int createdTasks) {
		this.createdTasks = createdTasks;
	}
	
	public List<Task> getTasksResults() {
		return tasksResults;
	}
	
	public synchronized void waitForTaskResults() throws InterruptedException {
		while(createdTasks != tasksResults.size()) {
			wait();
		}
	}
	
	
	public synchronized void addTask(Task task) {
		tasksResults.add(task);
		notifyAll();
	}
	
	
}
