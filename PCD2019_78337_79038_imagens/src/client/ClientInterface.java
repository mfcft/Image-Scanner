package client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import messages.PedidoPesquisa;
import messages.RespostaPesquisa;
import server.Task;
import server.TipoPesquisa;

public class ClientInterface {

	private RespostaPesquisa respostaPesquisa;
	private Client client;
	private JFrame frame = new JFrame("Find Images");
	private String currentDirectory;
	private DefaultListModel<String> imagelist;	
	private JList<String> imagenames;
	private JList<String> algorithmnames;
	private JScrollPane namePane;
	private String imageDirectory;
	
	

	public ClientInterface(Client client) {
		this.client = client;
		init();
	}

	private void framecontent() {


		JPanel imagedisplay = new JPanel(); //Painel que contém a imagem onde se realiza a procura
		imagedisplay.setLayout(new BorderLayout());
		JPanel imagelisting = new JPanel(); //Painel que contém a lista das imagens 
		JPanel algorithmlisting = new JPanel(); //Painel que contém a lista dos algorítmos
		JPanel southDisplay = new JPanel(); //Painel que contém todos os elementos que pertencem à zona sul da frame(botões)
		JPanel foldersearch = new JPanel(); //Painel que contém o botão da pasta assim como a zona que escreve o caminho da pasta escolhida
		JPanel imagesearch = new JPanel();	//Painel que contém o botão da imagem assim como a zona que escreve o caminho da imagem escolhida
		JPanel search = new JPanel(); //Painel para o botão da procura



		//ScrollPane dos algorítmos
		DefaultListModel<String> algorithmlist = getAlgNames();
		algorithmnames = new JList<String>(algorithmlist);
		JScrollPane algorithmPane = new JScrollPane(algorithmnames);

		//Zona de texto da pasta
		JTextField folderText = new JTextField();
		folderText.setText("Pasta");

		//Zona de texto da imagem
		JTextField imageText = new JTextField();
		imageText.setText("Imagem");
		
		//Botão da imagem
		JButton imageButton = new JButton("Imagem");
		imageButton.setSize(30, 20);
		imageButton.addActionListener(new ActionListener() {
			public synchronized void actionPerformed(ActionEvent e) {
				//Interface da escolha do caminho da imagem
				JFileChooser jfc = new JFileChooser(".");	 
				int returnValue = jfc.showOpenDialog(null);

				if (returnValue == JFileChooser.APPROVE_OPTION) {//O que acontece quando selecionamos a imagem
					File selectedFile = jfc.getSelectedFile();
//					BufferedImage image = getBufferedImage(selectedFile.getAbsolutePath());
					//					 client.setSubImage(image);
					imageDirectory = selectedFile.getAbsolutePath();
					System.out.println(imageDirectory);		
					imageText.setText(selectedFile.getName());
					imageText.revalidate();
					imageText.repaint();	 
					//					 JDialog dialog = new JDialog();
					//					 dialog.setUndecorated(true);
					//					 JLabel label = new JLabel( new ImageIcon(image) );
					//					 dialog.add( label );
					//					 dialog.pack();
					//					 dialog.setVisible(true);
				}
			}
		});

		//Botão da pasta
		JButton folderButton = new JButton("Pasta");
		folderButton.setSize(30, 20);
		folderButton.addActionListener(new ActionListener() {
			//O que acontece quando se clica no botão
			public synchronized void actionPerformed(ActionEvent e) {
				//JFileChooser para inserir da interface de escolha da pasta
				JFileChooser jfc = new JFileChooser(".");
				jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

				int returnValue = jfc.showOpenDialog(null);

				if (returnValue == JFileChooser.APPROVE_OPTION) { //o que acontece quando se escolhe a pasta
					File selectedFile = jfc.getSelectedFile();
					System.out.println(selectedFile.getName());
					currentDirectory = selectedFile.getAbsolutePath();
					imagelist = new DefaultListModel<String>();
					imagenames = new JList<String>(imagelist);
					namePane = new JScrollPane(imagenames); //adicionar o scrollpane com a lista das imagens
					imagenames.addListSelectionListener(new ListSelectionListener() {
						//O que acontece quando selecionamos uma imagem nova
						@Override
						public void valueChanged(ListSelectionEvent e) {
							imagedisplay.removeAll();
							String imagePath = currentDirectory+ "\\" + imagenames.getSelectedValue();
							BufferedImage image = getBufferedImage(imagePath);
							
							drawRectangles(image, imagePath);
							
							
							
							JLabel imagelabel = new JLabel(new ImageIcon(image));
							JScrollPane imageScrollPane1 = new JScrollPane(imagelabel);
							//								client.setMainImage(image);
							imagedisplay.add(imageScrollPane1);
							imagedisplay.revalidate();
							imagedisplay.repaint();							
						}});
					folderText.setText(currentDirectory);
					folderText.revalidate();
					folderText.repaint();
					imagelisting.add(namePane);
					imagelisting.revalidate();
					imagelisting.repaint();

				}

			}
			
			
			public void drawRectangles(BufferedImage image, String imagePath) {
				 //desenhar retângulo sobre imagem

				 Graphics2D g2d = image.createGraphics();
				 g2d.setColor(Color.RED);

				 
				 for (Task taskResult : respostaPesquisa.getTasksResults()) {
					if(taskResult.getImagePath().equalsIgnoreCase(imagePath)) {
						
						
						for(Rectangle rectangle : taskResult.getResultados()) {
							int x = (int) rectangle.getX();
							int y = (int) rectangle.getY();
							int width = (int) rectangle.getWidth();
							int height = (int) rectangle.getHeight();
							
							g2d.drawRect(x, y, width, height);
							
						}
						
					}
				}
				 

				 g2d.dispose();
			}
		});





		//Botão de procura
		JButton searchButton = new JButton("Procura");
		searchButton.addActionListener(new ActionListener() {
			public synchronized void actionPerformed(ActionEvent e) {

				if(!algorithmnames.getSelectedValuesList().isEmpty() && !folderText.getText().isEmpty() && !imageText.getText().isEmpty()) {
					searchButton.setEnabled(false);
					try {
						
						List<Task> tasks = createTasks();
						
						client.send(new PedidoPesquisa(tasks));
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				} else {
					JOptionPane.showMessageDialog(null, "Por favor selecione uma pasta, imagem e pelo menos um tipo de pesquisa válido...");
				}
			}
			
			private List<Task> createTasks() throws IOException {
				List<Task> tasks = new ArrayList<Task>();
				
				List<TipoPesquisa> listaTiposPesquisa = new ArrayList<>();
				if(algorithmnames.getSelectedValuesList().contains("Procura Simples")) {
					listaTiposPesquisa.add(TipoPesquisa.PESQUISA_SIMPLES);
				}
				if(algorithmnames.getSelectedValuesList().contains("Procura 90º")) {
					listaTiposPesquisa.add(TipoPesquisa.PESQUISA_90);
				}

				if(algorithmnames.getSelectedValuesList().contains("Procura 180º")) {
					listaTiposPesquisa.add(TipoPesquisa.PESQUISA_180);
				}

				
				File logoF = new File(imageDirectory);
				BufferedImage logoBI = ImageIO.read(logoF);
				byte[] logoB = bufferImageToByteArray(logoBI);
				

				File pasta = new File(folderText.getText());
				File[] ficheiros = pasta.listFiles();
				int taskcounter = 0;

				for (File file : ficheiros) {
					if(file.isFile() && file.getName().endsWith(".png")) {

						BufferedImage imageBI = ImageIO.read(file);
						byte[] imageB = bufferImageToByteArray(imageBI);
						
						for (TipoPesquisa tipoPesquisa : listaTiposPesquisa) {
							Task task = new Task(tipoPesquisa, file.getAbsolutePath(), imageB, logoB);
							tasks.add(task);
							taskcounter++;
						}
					}
				}
				System.out.println("Número de Tasks: " + taskcounter);
				return tasks;
			}
		});

		//Adicionar os objetos aos respetivos paineis
		algorithmlisting.add(algorithmPane);
		foldersearch.add(folderText, BorderLayout.WEST);
		foldersearch.add(folderButton, BorderLayout.EAST);
		imagesearch.add(imageText, BorderLayout.WEST);
		imagesearch.add(imageButton, BorderLayout.EAST);
		search.add(searchButton, BorderLayout.CENTER);
		southDisplay.add(foldersearch, BorderLayout.NORTH);
		southDisplay.add(imagesearch, BorderLayout.NORTH);
		southDisplay.add(search, BorderLayout.SOUTH);

		//Adicionar os paineis à frame
		frame.add(imagedisplay, BorderLayout.CENTER);
		frame.add(imagelisting, BorderLayout.EAST);
		frame.add(algorithmlisting, BorderLayout.WEST);
		frame.add(southDisplay, BorderLayout.SOUTH);
		frame.setSize(1150, 700);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


	}
	

	public byte[] bufferImageToByteArray(BufferedImage image) throws IOException {
		 // convert BufferedImage to byte array
		 ByteArrayOutputStream baos = new ByteArrayOutputStream();
		 ImageIO.write(image, "png", baos);
		 baos.flush();
		 byte[] img = baos.toByteArray();
		// baos.close();
		 return img;
	}
	 public void infoBox(String infoMessage, String titleBar)
	    {
	        JOptionPane.showMessageDialog(null, infoMessage, "InfoBox: " + titleBar, JOptionPane.INFORMATION_MESSAGE);
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

	//Método responsável por obter a defaultlistmodel com os nomes das imagens para inserir no scroll pane
	public DefaultListModel<String> getDLM(){
		DefaultListModel<String> aux = new DefaultListModel<String>();
		//		ArrayList<String> arrayaux = client.getImageListing(currentDirectory);
		//		for(String f : arrayaux) {
		//			aux.addElement(f);
		//		}

		//TODO
		return aux;
	}

	//Método responsável por adicionar as opções de procura da sub-imagem no default list model para ser inserido no scrollpane
	private DefaultListModel<String> getAlgNames(){
		DefaultListModel<String> aux = new DefaultListModel<String>();
		aux.addElement("Procura Simples");
		aux.addElement("Procura 90º");
		aux.addElement("Procura 180º");
		return aux;
	}

	//Inicia a IG
	private void init() {
		framecontent();
		frame.setVisible(true);

	}

	public void update(RespostaPesquisa respostaPesquisa) {
		
		DefaultListModel<String> aux = new DefaultListModel<String>();
		
		for (Task task : respostaPesquisa.getTasksResults()) {
			if(task.hasLogo() == true) {
				if(aux.contains(task.getImagePath()) == false) {
					String filenameString = task.getImagePath().substring(task.getImagePath().lastIndexOf("\\")+1);
					aux.addElement(filenameString);
				}
			}
		}
		
		imagenames.setModel(aux);
		this.respostaPesquisa = respostaPesquisa;
		
	}


}
