package server;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import messages.PedidoPesquisa;
import messages.RespostaPesquisa;

public class DWC extends Thread {

	private Server server;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	private Socket socket;
	private int dwcId;
	
	public DWC(Server server, ObjectInputStream in, ObjectOutputStream out, Socket socket, int dwcId) {
		this.server = server;
		this.in = in;
		this.out = out;
		this.socket = socket;
		this.dwcId = dwcId;
	}

	public void send(Object message) throws IOException {
		out.writeObject(message);
	}

	public Object recieve() throws ClassNotFoundException, IOException {
		return in.readObject();
	}

	private RespostaPesquisa respostaPesquisa;
	
	@Override
	public void run() {
		try {
			while(!interrupted()) {
			
				Object recievedMessage = recieve();

				if(recievedMessage instanceof PedidoPesquisa) {
					PedidoPesquisa pedidoPesquisa = (PedidoPesquisa) recievedMessage;

					respostaPesquisa = new RespostaPesquisa(pedidoPesquisa.getTasks().size());
					
					for(Task task : pedidoPesquisa.getTasks()) {
						task.setDwcId(dwcId);
						
						TaskQueue taskQueue = server.getTaskQueue(task.getTipoPesquisa());
						if(taskQueue != null) {
							taskQueue.addLast(task);
						}
					}
					
					
					respostaPesquisa.waitForTaskResults();
					send(respostaPesquisa);
					}
				
				
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	
	public int getDwcId() {
		return dwcId;
	}
	
	public synchronized void taskCompleted(Task task) {
		respostaPesquisa.addTask(task);
	}

}