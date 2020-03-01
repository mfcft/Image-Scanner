package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Server  {   //Server Socket

	public static void main(String[] args) {
		try {
			int port = Integer.parseInt(args[0]);
			new Server().startServing(port);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private Map<TipoPesquisa, TaskQueue> taskQueueMap;
	private List<DWC> dwcList = new ArrayList<DWC>();
	private int contadorClientes = 0;
	private boolean isConnected = true;
	
	public Server() {
		taskQueueMap = new HashMap<TipoPesquisa, TaskQueue>();
		taskQueueMap.put(TipoPesquisa.PESQUISA_SIMPLES, new TaskQueue());
		taskQueueMap.put(TipoPesquisa.PESQUISA_90, new TaskQueue());
		taskQueueMap.put(TipoPesquisa.PESQUISA_180, new TaskQueue());
	}
	
	public TaskQueue getTaskQueue(TipoPesquisa tipoPesquisa) {
		return taskQueueMap.get(tipoPesquisa);
	}
	
	public synchronized DWC getDWC(int dwcId) {
		for (DWC dwc : dwcList) {
			if(dwc.getDwcId() == dwcId) {
				return dwc;
			}
		}
		
		return null;
	}
	public void printit() {
		System.out.println("conexao fechada");
	}

	public boolean isConnected() {
		return isConnected;
	}

	public void setConnected(boolean isConnected) {
		this.isConnected = isConnected;
	}
	
	public void startServing(int port) throws IOException {
		ServerSocket s = new ServerSocket(port);
		System.out.println("Lançou ServerSocket: " + s);
		try {
			while (true) {
				try {
				Socket socket = s.accept();
				System.out.println("Conexão aceite: " + socket);
				
				ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
				ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
				
					Object firstMessage = in.readObject();

					if(firstMessage.equals("Worker")) {
						DWW dww = new DWW(this, in, out, socket);
						dww.start();
						System.out.println("Novo worker ligado");
						
					} else if(firstMessage.equals("Client")) {
						contadorClientes++;
						DWC dwc = new DWC(this, in, out, socket, contadorClientes);
						dwc.start();
						dwcList.add(dwc);
						System.out.println("Novo client ligado");
					}
					
				
					
					
				
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
				
				
			}
		} finally {
			s.close();
		}
	}

}