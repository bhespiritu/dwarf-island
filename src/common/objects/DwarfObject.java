package common.objects;

import java.awt.Color;
import java.awt.Graphics;

import client.DwarfClient;
import common.HashGenerator;
import common.TextureManager;

public class DwarfObject extends WorldObject{

	public String name = "BlueXephos";
	
	public DwarfObject() {
		icon = TextureManager.dwarf;
	}

	@Override
	protected void drawObject(DwarfClient context, Graphics g) {
		g.drawImage(icon,-8,-8,null);
		int labelWidth = g.getFontMetrics().stringWidth(name);
		g.setColor(Color.BLACK);
		g.drawString(name, -labelWidth/2, 12);
		
	}
	
}
