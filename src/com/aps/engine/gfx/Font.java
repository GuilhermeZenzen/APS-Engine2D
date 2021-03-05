package com.aps.engine.gfx;

import java.util.ArrayList;

public class Font {
	public static final Font STANDARD = new Font("/fonts/font.png");
	
	private Image fontImage;
	private ArrayList<Integer> offsets = new ArrayList<Integer>();
	private ArrayList<Integer> widths = new ArrayList<Integer>();
	private ArrayList<int[]> characters = new ArrayList<int[]>();
	
	public Font(String path) {
		fontImage = new Image(path);
		
		int unicode = 0;
		
		for (int i = 0; i < fontImage.getWidth(); i++) {
			if (fontImage.getPixels()[i] == 0xff0000ff) {
				offsets.add(i);
			}
			
			if (fontImage.getPixels()[i] == 0xffffff00) {
				widths.add(i - offsets.get(unicode));
				unicode++;
			}
		}
		
		setCharacters();
	}
	
	public int getStringWidth(String string, float scale, int index) {
		if (string.length() > 0) {
			return getStringWidth(string.substring(0, index), scale);
		}
		return -1;
	}
	public int getStringWidth(String string, float scale) {
		int width = 0;
		for (int i = 0; i < string.length(); i++) {
			width += Math.ceil(getWidth(string.charAt(i) - 32) * scale);
		}
		return width;
	}
	
	public int getStringHeight(String string, float scale) {
		float height = getHeight() * scale;
		
		for (int i = 0; i < string.length(); i++) {
			if (string.charAt(i) == '\n') {
				height += (getHeight() + 1 * scale);
			}
		}
		
		return (int)Math.ceil(height);
	}
	
	private void setCharacters() {
		for (int i = 0; i < widths.size(); i++) {
			int pixels[] = new int[getHeight() * getWidth(i)];
			
			for (int y = 1; y < getFontImage().getHeight(); y++) {
				for (int x = 0; x < getWidth(i); x++) {
					pixels[((y - 1) * getWidth(i)) + x] = fontImage.getPixels()[(y * fontImage.getWidth()) + getOffset(i) + x + 1];
				}
			}
			
			characters.add(pixels);
		}
	}
	
	public int[] getCharacter(int unicode) {
		return characters.get(unicode);
	}

	public int getWidth(int unicode) {
		return widths.get(unicode);
	}
	public int getWidth(int unicode, float scale) {
		return (int)Math.ceil(widths.get(unicode) * scale);
	}
	
	public int getHeight() {
		return getFontImage().getHeight() - 1;
	}
	
	public int getOffset(int unicode) {
		return offsets.get(unicode);
	}
	
	public Image getFontImage() {
		return fontImage;
	}

	public ArrayList<Integer> getOffsets() {
		return offsets;
	}

	public ArrayList<Integer> getWidths() {
		return widths;
	}
}