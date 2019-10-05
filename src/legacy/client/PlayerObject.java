package legacy.client;

import java.awt.Graphics;

import common.TextureManager;

public class PlayerObject extends WorldObject{
	
	public PlayerObject() {
		image = TextureManager.dwarf;
	}
	
	@Override
	public void draw(Graphics g, int baseX, int baseY) {
		super.draw(g, baseX, baseY);
	}
	
}
