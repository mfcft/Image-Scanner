package server;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

public class DWW extends Thread {

	private Server server;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	private Socket socket;
	private InputStreamReader inReader;


	

	public DWW(Server server, ObjectInputStream in, ObjectOutputStream out, Socket socket) throws IOException {
		this.server = server;
		this.in = in;
		this.out = out;
		this.socket = socket;
	}

	public void send(Object message) throws IOException {
		out.writeObject(message);
	}

	public Object recieve() throws ClassNotFoundException, IOException {
		return in.readObject();
	}

	@Override
	public void run() {

		try {

			TipoPesquisa tipoPesquisa = (TipoPesquisa) recieve();

			while(!interrupted()) {
				
				TaskQueue taskQueue = server.getTaskQueue(tipoPesquisa);
				Task task = taskQueue.removeFirst();
				send(task);
				
				Object respostaObject = recieve();
				if(respostaObject instanceof Task) {
					Task completedTask = (Task) respostaObject;
				
				int dwcId = completedTask.getDwcId();
				DWC dwc = server.getDWC(dwcId);
				dwc.taskCompleted(completedTask);
				}
				
	
			}

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SocketException e) {
			server.setConnected(false);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} 

	}


}
