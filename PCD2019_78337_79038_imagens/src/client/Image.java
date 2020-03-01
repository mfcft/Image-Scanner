package client;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.w3c.dom.css.RGBColor;

/*public class Image {
	
	private BufferedImage subImage;
	private BufferedImage mainImage;
	
	//Método que cria uma bufferedimage para ser possível a visualização de imagens
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
	
	public void imagesearch() {
		int nimagens = 0;
		for (int i = 0; i < mainImage.getHeight(); i++) {
			for (int j = 0; j < mainImage.getWidth(); j++) {
				if(mainImage.getRGB(j, i) == subImage.getRGB(0, 0)) {
					if(imagesearch2(j, i)) {
						nimagens ++;
					}
				}
				
			}	
		}
		System.out.println("N imagens encontradas: " + nimagens);
		
	}
	private Boolean imagesearch2(int x, int y) {
		for (int i = 0; i < subImage.getHeight(); i++) {
			for (int j = 0; j < subImage.getWidth(); j++) {
					if(mainImage.getRGB(j+x, i+y) != subImage.getRGB(j, i)) {

						
						return false;
					}
			}	
		}
		return true;
	}
	
	//Setters e Getters da Imagem principal e da sub-imagem
	
	public void setMainImage(BufferedImage mainImage) {
		this.mainImage = mainImage;
	}
	
	public void setSubImage(BufferedImage subImage) {
		this.subImage = subImage;
	}
	
	public BufferedImage getSubImage() {
		return subImage;
	}
	
	public BufferedImage getMainImage() {
		return mainImage;
	}
}*/
