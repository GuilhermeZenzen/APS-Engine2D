package com.aps.game.components;

import java.awt.Color;

import com.aps.engine.Renderer;
import com.aps.engine.gfx.Image;

public class ImageRenderer extends Component {
	private Image image;
	private Color color;
	private int depth;
	
	@Override
	public void update() {
		
	}
	
	@Override
	public void render(Renderer renderer) {

	}

	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
	}
	public Color getColor() {
		return color;
	}
	public void setColor(Color color) {
		this.color = color;
	}
	public int getDepth() {
		return depth;
	}
	
	public void setDepth(int depth) {
		this.depth = depth;
	}
}
