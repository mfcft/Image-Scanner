package server;

import java.awt.Rectangle;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Task implements Serializable {


	private TipoPesquisa tipoPesquisa;
	private String imagePath;
	private byte[] imageB;
	private byte[] logoB;
	private int dwcId;
	
	private List<Rectangle> resultados;
	
	private boolean hasLogo;

	public Task(TipoPesquisa tipoPesquisa, String imagePath, byte[] imageB, byte[] logoB) {
		this.tipoPesquisa = tipoPesquisa;
		this.imagePath = imagePath;
		this.imageB = imageB;
		this.logoB = logoB;
	}

	public TipoPesquisa getTipoPesquisa() {
		return tipoPesquisa;
	}

	public String getImagePath() {
		return imagePath;
	}

	public byte[] getImageB() {
		return imageB;
	}

	public byte[] getLogoB() {
		return logoB;
	}
	
	public List<Rectangle> getResultados() {
		return resultados;
	}
	
	public void setResultados(List<Rectangle> resultados) {
		this.resultados = resultados;
	}
	
	public int getDwcId() {
		return dwcId;
	}
	
	public void setHasLogo(boolean hasLogo) {
		this.hasLogo = hasLogo;
	}
	public boolean hasLogo(){
		return hasLogo;
	}
	
	public void setDwcId(int dwcId) {
		this.dwcId = dwcId;
	}

}
