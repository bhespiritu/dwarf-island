package client;

import java.awt.Color;
import java.awt.Graphics;

import common.Transform;

public class LabelObject{

	Transform transform = new Transform();
	
	public static float MAX_AGE_MULTIPLIER = 1.1f;//amount of seconds per character
	
	public float age = 0;
	private float maxAge;
	public String value;
	
	public LabelObject(String message) {
		value = message;
		maxAge = message.length()*MAX_AGE_MULTIPLIER;
	}
	
	public void draw(Graphics g, int baseX, int baseY)
	{
		int width = g.getFontMetrics().stringWidth(value);
		
		int pixX = (int) (transform.X*16);
		int pixY = (int) (transform.Y*16);
		
		int dx = -(pixX-baseX);
		int dy = -(pixY-baseY);
		
		g.setColor(Color.WHITE);
		g.drawString(value, dx-width/2, dy);
	}
	
	
	
}
