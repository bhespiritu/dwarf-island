package legacy.client;

import java.awt.Graphics;
import java.awt.Image;

import legacy.common.Transform;

public abstract class WorldObject implements IDrawable{

	protected Transform transform = new Transform();
	protected Image image;
	public WorldObject parent = null;
	
	
	@Override
	public void draw(Graphics g, int baseX, int baseY) {
		float parentX = 0;
		float parentY = 0;
		if(parent != null)
		{
			parentX = parent.transform.X;
			parentY = parent.transform.Y;
		}
		int pixX = (int) ((parentX + transform.X)*16);
		int pixY = (int) ((parentY + transform.Y)*16);
		
		int dx = -(pixX-baseX);
		int dy = -(pixY-baseY);
		
		g.drawImage(image, dx,dy, null);
	}

}
