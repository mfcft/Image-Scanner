package workers;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import server.Task;
import server.TipoPesquisa;

public class Worker extends Thread {

	private String serverIp;
	private int serverPort;
	private TipoPesquisa tipoPesquisa;
	
	private boolean hasLogo;
	
	private Socket socket;
	private ObjectInputStream in;
	private ObjectOutputStream out;

	public Worker(String serverIp, int serverPort, TipoPesquisa tipoPesquisa) {
		this.serverIp = serverIp;
		this.serverPort = serverPort;
		this.tipoPesquisa = tipoPesquisa;

		try {
			this.socket = new Socket(serverIp, serverPort);

			this.out = new ObjectOutputStream(socket.getOutputStream());
			this.in = new ObjectInputStream(socket.getInputStream());
				
			

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
				Object respostaObject = recieve();
				Task task = (Task)respostaObject;
				List<Rectangle> list = imagesearch(task.getImageB(), task.getLogoB());
				task.setResultados(list);
				task.setHasLogo(hasLogo);

				send(task);
				
			}
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public BufferedImage getBufferedImage(String filename) {

		BufferedImage image;
		try {
			System.out.println(filename);
			image = ImageIO.read(new File(filename));
			return image;

		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}
	
	
	public static BufferedImage rotateClockwise90(BufferedImage src) {
	    int width = src.getWidth();
	    int height = src.getHeight();

	    BufferedImage dest = new BufferedImage(height, width, src.getType());

	    Graphics2D graphics2D = dest.createGraphics();
	    graphics2D.translate((height - width) / 2, (height - width) / 2);
	    graphics2D.rotate(Math.PI / 2, height / 2, width / 2);
	    graphics2D.drawRenderedImage(src, null);

	    return dest;
	}

	public List<Rectangle> imagesearch(byte[] imageB, byte[] logoB) throws IOException {
		List<Rectangle> list = new ArrayList<>();
		
		hasLogo = false;
		
		InputStream imageIS = new ByteArrayInputStream(imageB);
		BufferedImage mainImage = ImageIO.read(imageIS);
		
		InputStream logoIS = new ByteArrayInputStream(logoB);
		BufferedImage subImage = ImageIO.read(logoIS);
		
		if(tipoPesquisa == TipoPesquisa.PESQUISA_90) {
			subImage = rotateClockwise90(subImage);
		}
		
		if(tipoPesquisa == TipoPesquisa.PESQUISA_180) {
			subImage = rotateClockwise90(subImage);
			subImage = rotateClockwise90(subImage);
		}
		
		
		
		
		
		

		int nimagens = 0;
		for (int i = 0; i < mainImage.getHeight(); i++) {
			for (int j = 0; j < mainImage.getWidth(); j++) {
				if(mainImage.getRGB(j, i) == subImage.getRGB(0, 0)) {
					if(imagesearch2(j, i, mainImage, subImage)) {
						nimagens ++;
						hasLogo = true;
						list.add(new Rectangle(j, i, subImage.getWidth(), subImage.getHeight()));
					}
				}

			}	
		}
		System.out.println("N imagens encontradas: " + nimagens);
		return list;

	}
	private Boolean imagesearch2(int x, int y, BufferedImage mainImage, BufferedImage subImage) {
		for (int i = 0; i < subImage.getHeight(); i++) {
			for (int j = 0; j < subImage.getWidth(); j++) {
				if(mainImage.getRGB(j+x, i+y) != subImage.getRGB(j, i)) {


					return false;
				}
			}	
		}
		return true;
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
		
		TipoPesquisa tipoPesquisa = TipoPesquisa.PESQUISA_SIMPLES;
		if(args[2].equals("90"))
			tipoPesquisa = TipoPesquisa.PESQUISA_90;
		
		if(args[2].equals("180"))
			tipoPesquisa = TipoPesquisa.PESQUISA_180;
		
		
		
		Worker w = new Worker(ip, port, tipoPesquisa);
		w.start();
		w.send("Worker");
		w.send(w.tipoPesquisa);
	}

}
