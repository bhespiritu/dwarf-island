package client;

import java.awt.Image;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

public class TextureManager {

	public static Image grass;
	public static Image dirt;
	public static Image dwarf;
	
	public static TextureManager instance = new TextureManager();
	
	private TextureManager() {
		try {
			refresh();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void refresh() throws IOException {
		URL grassURL = TextureManager.class.getResource("/resources/grass.png");
		grass = ImageIO.read(grassURL);
		URL dirtURL = TextureManager.class.getResource("/resources/dirt.png");
		dirt = ImageIO.read(dirtURL);
		URL dwarfURL = TextureManager.class.getResource("/resources/dwarf.png");
		dwarf = ImageIO.read(dwarfURL);
	}
	
	
	
}
