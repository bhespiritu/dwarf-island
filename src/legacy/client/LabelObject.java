package legacy.client;

import java.awt.Color;
import java.awt.Graphics;

public class LabelObject extends WorldObject{
	
	public static float MAX_AGE_MULTIPLIER = 1.1f;//amount of seconds per character
	
	public float age;
	private float maxAge;
	public String value;
	
	public boolean isDone = false;
	
	public LabelObject(String message) {
		value = message;
		maxAge = message.length()*MAX_AGE_MULTIPLIER;
		age = -maxAge/10;
	}
	
	public void draw(Graphics g, int baseX, int baseY)
	{
		if(isDone) return;
 		int width = g.getFontMetrics().stringWidth(value);
		
		int pixX = (int) (transform.X*16);
		int pixY = (int) (transform.Y*16);
		
		int dx = -(pixX-baseX);
		int dy = -(pixY-baseY);
		
		age += .25f;
		if(age > maxAge) isDone = true;
		float progress = age/maxAge;
		if(age < 0) progress = 0;
		
		int offset = (int) (16*(progress));
		
		Color textC = new Color(1,1,1,1-(progress));
		
		g.setColor(textC);
		g.drawString(value, dx-width/2, dy - offset);
	}
	
	
	
}
