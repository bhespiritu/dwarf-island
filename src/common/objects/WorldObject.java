package common.objects;

import java.awt.Graphics;
import java.awt.Image;

import client.DwarfClient;

public abstract class WorldObject {

	public float posX = 0;
	public float posY = 0;
	
	public WorldObject parent = null;
	
	public int id = 0;
	
	public boolean isNetworked = false;
	
	protected static Image icon; 
	
	public WorldObject() {}
	
	public abstract void deserializeData(byte[] data);
	
	public abstract byte[] serializeData();
	
	protected abstract void drawObject(DwarfClient context, Graphics g);
	
	public void draw(DwarfClient context, Graphics g)
	{
		float relX = posX;
		float relY = posY;
		
		if(parent != null)
		{
			relX += parent.posX;
			relY += parent.posY;
		}
		
		int pixX = (int)(relX*16);
		int pixY = (int)(relY*16);
		g.translate(pixX, pixY);
		drawObject(context,g);
		g.translate(-pixX, -pixY);
		
	}
	
	public abstract byte getObjectID();
	
	
}
