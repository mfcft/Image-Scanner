package messages;

import java.io.Serializable;
import java.util.List;

import server.Task;

public class PedidoPesquisa implements Serializable {

	
	private List<Task> tasks;
	
	public PedidoPesquisa(List<Task> tasks) {
		this.tasks = tasks;
	}
	
	public List<Task> getTasks() {
		return tasks;
	}

}
