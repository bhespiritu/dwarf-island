package common;

import java.awt.Color;
import java.awt.Graphics;

public class GenericObject implements IDrawable{

	public Transform transform = new Transform();

	@Override
	public void draw(Graphics g, int baseX, int baseY) {
		int pixX = (int) (transform.X*16);
		int pixY = (int) (transform.Y*16);
		
		int dx = -(pixX-baseX);
		int dy = -(pixY-baseY);
		
		g.setColor(Color.BLUE.darker());
		g.fillRect(dx, dy, 16, 16);
		g.setColor(Color.BLUE);
		g.fillRect(dx, dy-10, 16, 16);
	}
	
	
	
}
