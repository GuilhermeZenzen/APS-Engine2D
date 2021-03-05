package com.aps.game.components;

import java.awt.Color;

import com.aps.engine.Renderer;
import com.aps.engine.gfx.Image;

public class Sprite extends Component {
	private Image image;
	private int color = 0xffffffff;
	private int depth = 0;

	public void awake() {
		Camera.addSprite(this);
	}
	
	public void update() {
		
	}
	
	public void render(Renderer renderer) {

	}
	
	public void onDestroy() {
		Camera.removeSprite(this);
	}

	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
	}
	public int getColor() {
		return color;
	}
	public void setColor(int color) {
		this.color = color;
	}
	public int getDepth() {
		return depth;
	}
	
	public void setDepth(int depth) {
		this.depth = depth;
	}
}
