package common.objects;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import client.DwarfClient;

public class LabelObject extends WorldObject{

	private static final Font messageFont = new Font("Courier New", Font.PLAIN, 7);
	
	public String message;
	
	private float birthTime = -1;
	
	private float maxAge;
	
	private boolean isDead = false;
	
	public boolean isDead() {return isDead;}
	
	private static float AGE_MULT = 0.2f;//number of seconds per character
	
	public LabelObject(String msg) {
		message = msg;
		maxAge = msg.length()*AGE_MULT;
	}
	
	@Override
	protected void drawObject(DwarfClient context, Graphics g) {
		if(isDead) return;
		Font hold = g.getFont();
		g.setFont(messageFont);
 		if(birthTime == -1) birthTime = context.time;
		int msgWidth = g.getFontMetrics().stringWidth(message);
		
		float diff = context.time - birthTime;
		int offset = 0;
		if(diff > maxAge)
		{
			offset = (int) (diff);
		}
		float doubleMax = maxAge*2;
		if(diff > doubleMax)
		{
			isDead = true;
			diff = doubleMax;
		}
		
		g.setColor(new Color(1,1,1,1-(diff/(doubleMax))));
		g.drawString(message, -msgWidth/2, -10 - offset);
		g.setFont(hold);
		
	}

	@Override
	public byte getObjectID() {
		return 2;
	}

	@Override
	public void deserializeData(byte[] data) {}

	@Override
	public byte[] serializeData() {return null;}

}
