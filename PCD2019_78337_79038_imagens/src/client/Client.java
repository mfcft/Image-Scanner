package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import messages.RespostaPesquisa;

public class Client extends Thread {

	private String serverIp;
	private int serverPort;

	private Socket socket;
	private ObjectInputStream in;
	private ObjectOutputStream out;

	private ClientInterface ci;

	public Client(String serverIp, int serverPort) {
		this.serverIp = serverIp;
		this.serverPort = serverPort;

		try {
			this.socket = new Socket(serverIp, serverPort);

			this.out = new ObjectOutputStream(socket.getOutputStream());
			this.in = new ObjectInputStream(socket.getInputStream());

			ci = new ClientInterface(this);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

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
			while(!interrupted()) {
				RespostaPesquisa respostaPesquisa = (RespostaPesquisa) recieve();
				ci.update(respostaPesquisa);
				
					
	
				

			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	} 

	//extends Image{
	//
	//
	//	//Método que vai ler os nomes dos ficheiros duma pasta e guardar numa arraylist
	//	public ArrayList <String> getImageListing(String directory) {
	//		ArrayList <String> imagenames  = new ArrayList<String>();
	//		File images = new File(directory);
	//		File[] ficheiros = images.listFiles();
	//		for(int i = 0; i <  ficheiros.length; i++) {
	//			if(ficheiros[i].isFile() && ficheiros[i].getName().endsWith(".png")){
	//				imagenames.add(ficheiros[i].getName());
	//				System.out.println(ficheiros[i]);
	//			}else if (ficheiros[i].isDirectory()){
	//				System.out.println("Diretoria encontrada");
	//			}
	//		}
	//		return imagenames;
	//	}	
	//
	//	//Método que altera o tamanho de imagens [Não está a ser utilizado]
	//	public BufferedImage resizeImage(String filename) {
	//		BufferedImage beforeImg = getBufferedImage(filename);
	//		int w = beforeImg.getWidth();
	//		int h = beforeImg.getHeight();
	//		BufferedImage afterImg = new BufferedImage(1199, 599, BufferedImage.TYPE_INT_ARGB);
	//		Graphics2D g = afterImg.createGraphics();
	//		g.drawImage(beforeImg, 0, 0, 800, 500, null);
	//		g.dispose();
	//		/*AffineTransform at = new AffineTransform();
	//		at.scale(d, d);
	//		AffineTransformOp scaleOp = 
	//		   new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
	//		afterImg = scaleOp.filter(beforeImg, afterImg);*/
	//		return afterImg;
	//	}
	//


	public static void main(String[] args) throws IOException {
		
		String ip = args[0];
		int port = Integer.parseInt(args[1]);
		
		Client c = new Client(ip, port);
		c.start();
		c.send("Client");
	}

}
