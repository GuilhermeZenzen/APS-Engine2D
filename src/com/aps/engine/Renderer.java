package com.aps.engine;

import java.awt.Color;
import java.awt.image.DataBufferInt;
import java.util.ArrayList;

import com.aps.engine.gfx.Font;
import com.aps.engine.gfx.Image;

public class Renderer {
	private static int[] gamePixels;
	private static int gamePW, gamePH;
	private static int[] pixels;
	private static int pW, pH;
	private static float scale = 1f;
	//private static int[] uiPixels;
	
	private static Font font = Font.STANDARD;
	
	public static void setRenderer() {
		gamePW = GameController.getWidth();
		gamePH = GameController.getHeight();
		pixels = ((DataBufferInt)Window.getImage().getRaster().getDataBuffer()).getData();
		pW = (int)(GameController.getWidth() * GameController.getScale());
		pH = (int)(GameController.getHeight() * GameController.getScale());
		//uiPixels = ((DataBufferInt)Window.getUI().getRaster().getDataBuffer()).getData();
		gamePixels = new int[gamePW * gamePH];
	}
	
	public static float horizontalScale() {
		return (float)scaledPH() / gamePH;
	}
	
	public static float verticalScale() {
		return (float)scaledPH() / gamePH;
	}
	
	public static int scaledPW() {
		int newPW = Math.round(gamePW * scale);
		return newPW % 2 == 0 ? newPW : newPW + 1;
	}
	
	public static int scaledPH() {
		int newPH = Math.round(gamePH * scale);
		return newPH % 2 == 0 ? newPH : newPH + 1;
	}
	
	public static void scalePixels() {
		int newPW = scaledPW();
		int newPH = scaledPH();
		int oldPixels[] = new int[scaledPW() * newPH];
		int startVerticalIndex = Math.round(gamePH / 2 - newPH / 2);
		int startHorizontalIndex = Math.round(gamePW / 2 - newPW / 2);
		
		for (int i = 0; i < newPH; i++) {
			for (int j = 0; j < newPW; j++) {
				oldPixels[i * newPW + j] = gamePixels[(i + startVerticalIndex) * gamePW + (j + startHorizontalIndex)];
			}
		}
		
		int scaledPixels[] = Image.scaleImage(oldPixels, newPW, newPH, gamePW, gamePH);
		
		for (int i = 0; i < gamePH; i++) {
			for (int j = 0; j < gamePW; j++) {
				gamePixels[i * gamePW + j] = scaledPixels[i * gamePW + j];
			}
		}
	}
	
	public static void clear() {
		for (int i = 0; i < gamePixels.length; i++) {
			gamePixels[i] = 0x59b8cd;
		}
	}
	/*
	public static void clearUI() {
		for (int i = 0; i < uiPixels.length; i++) {
			uiPixels[i] = 0x00000000;
		}
	}
	*/
	public static void setPixel(int x, int y, int value) {
		int alpha = (value >> 24) & 0xff;

		if((x < 0 || x >= gamePW || y < 0 || y >= gamePH) || alpha == 0) {
			return;
		}
		
		int pixelIndex = x + y * gamePW;

		if(alpha == 255) {
			gamePixels[pixelIndex] = value;							
		} else {
			int pixelColor = gamePixels[pixelIndex];
			int pixelRed = (pixelColor >> 16) & 0xff, pixelGreen = (pixelColor >> 8) & 0xff, pixelBlue = pixelColor & 0xff;
			int newRed = pixelRed - (int)((pixelRed - ((value >> 16) & 0xff)) * (alpha / 255f));
			int newGreen = pixelGreen - (int)((pixelGreen - ((value >> 8) & 0xff)) * (alpha / 255f));
			int newBlue = pixelBlue - (int)((pixelBlue - (value & 0xff)) * (alpha / 255f));
			
			gamePixels[pixelIndex] = (255 << 24 | newRed << 16 | newGreen << 8 | newBlue);				
		}
	}
	
	public static void gameToScreenPixels() {
		int newPixels[] = Image.scaleImage(gamePixels, gamePW, gamePH, pW, pH);
		
		for (int i = 0; i < pixels.length; i++) {
			pixels[i] = newPixels[i];
		}
	}
	
	public static void setUIPixel(int x, int y, int value) {
		int alpha = (value >> 24) & 0xff;
		
		if((x < 0 || x >= pW || y < 0 || y >= pH) || alpha == 0) {
			return;
		}
		
		int pixelIndex = x + y * pW;
		//int pixelAlpha = (uiPixels[pixelIndex] >> 24) & 0xff;

		if(alpha == 255/* || pixelAlpha == 0*/) {
			pixels[pixelIndex] = value;									
		} else {
			int pixelColor = pixels[pixelIndex];
			int pixelRed = (pixelColor >> 16) & 0xff, pixelGreen = (pixelColor >> 8) & 0xff, pixelBlue = pixelColor & 0xff;
			int newRed = pixelRed - (int)((pixelRed - ((value >> 16) & 0xff)) * (alpha / 255f));
			int newGreen = pixelGreen - (int)((pixelGreen - ((value >> 8) & 0xff)) * (alpha / 255f));
			int newBlue = pixelBlue - (int)((pixelBlue - (value & 0xff)) * (alpha / 255f));
			
			pixels[pixelIndex] = (255 << 24 | newRed << 16 | newGreen << 8 | newBlue);
		}
	}
	
	public static void drawText(String text, int offX, int offY, int startXMask, int startYMask, int endXMask, int endYMask, int color, float scale) {
		drawText(text, offX, offY, startXMask, startYMask, endXMask, endYMask, color, scale, false, 0);
	}
	public static void drawText(String text, int offX, int offY, int startXMask, int startYMask, int endXMask, int endYMask, int color, float scale, boolean pointer, int pointerPos) {
		int xOffset = 0;
		int yOffset = 0;
		int pointerOffset = 0;

		int height = (int)Math.ceil(font.getHeight() * scale);
		
		for (int i = 0; i < text.length(); i++) {
			if (text.charAt(i) == '\n') {
				yOffset += Math.ceil((font.getHeight() + 1) * scale);
				xOffset = 0;
				continue;
			}
					
			int unicode = (int)(text.charAt(i)) - 32;
			int width = (int)Math.ceil(font.getWidth(unicode) * scale);
			int charPixels[] = Image.scaleImage(font.getCharacter(unicode), font.getWidth(unicode), font.getHeight(), width, height);
			
			int index = i == text.length() - 1 && pointerPos > i ? i + 1 : i;
			if (i == pointerPos) {
				pointerOffset = xOffset;
			} else if (pointerPos == text.length()) {
				pointerOffset = xOffset + width;
			}

			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					int pixel = charPixels[y * width + x];
					if (pixel == 0xffffffff) {
						int finalX = x + offX + xOffset;
						int finalY =  y + offY + yOffset;
						if (finalX >= startXMask && finalX <= endXMask && finalY >= startYMask && finalY <= endYMask) {
							setUIPixel(x + offX + xOffset, y + offY + yOffset, color);
						}	
					}
				}	
			}
			
			xOffset += Math.ceil(font.getWidth(unicode) * scale);
		}
		
		if (pointer) {
			int x = pointerOffset - 1; 
			for (int y = 0; y < (int)Math.ceil(font.getHeight() * scale); y++) {
				setUIPixel(offX + x, y + offY + yOffset, 0xff999999);
			}
		}
	}

	private static ArrayList<Segment> segmentsToDraw = new ArrayList<Segment>();
	
	public static ArrayList<Segment> getSegmentsToDraw(){
		return segmentsToDraw;
	}
	
	public static void drawLine(int x1, int y1, int x2, int y2, int width, int color) {
		segmentsToDraw.add(new Segment(x1, y1, x2, y2, width, color));
	}
	
	public static void drawImage(Image image, int offX, int offY, int color, float scale, boolean ui) {
		drawImage(image, offX, offY, color, scale, scale, ui);
	}
	public static void drawImage(Image image, int offX, int offY, int color, float scaleX, float scaleY, boolean ui) {	
		int width = scaleX != 1 ? (int)Math.ceil(image.getWidth() * scaleX) : image.getWidth();
		int height = scaleY != 1 ? (int)Math.ceil(image.getHeight() * scaleY) : image.getWidth();
		
		drawImage(image, offX, offY, width, height, color, ui);
	}
	public static void drawImage(Image image, int offX, int offY, int width, int height, int color, boolean ui) {
		int coloredImage[] = Image.colorImage(image.getPixels(), color);
		int pixels[] = width == image.getWidth() && height == image.getHeight() ? coloredImage :
					   Image.scaleImage(coloredImage, image.getWidth(), image.getHeight(), width, height);
		
		int finalPW = ui ? pW : gamePW;
		int finalPH = ui ? pH : gamePH;
		
		// Render need checker
		if (offX < -width) return;
		if (offX >= finalPW) return;
		if (offY < -height) return;
		if (offY >= finalPH) return;
		//
		
		int newX = 0;
		int newY = 0;
		int newWidth = width;
		int newHeight = height;
		
		// Clipping code
		if (offX < 0) {
			newX -= offX;
		}
		if (offY < 0) {
			newY -= offY;
		}
		if (newWidth + offX >= finalPW) {
			newWidth -= newWidth + offX - finalPW;
		}
		if (newHeight + offY >= finalPH) {
			newHeight -= newHeight + offY - finalPH; 
		}
		//
		
		for (int y = newY; y < newHeight; y++) {
			for (int x = newX; x < newWidth; x++) {
				if (ui) {
					setUIPixel(x + offX, y + offY, pixels[x + y * width]);
				} else {
					setPixel(x + offX, y + offY, pixels[x + y * width]);					
				}
			}
		}
	}
	
	public static void drawRect(int offX, int offY, int width, int height, int color) {
		for(int y = 0; y <= height; y++) {
			setPixel(offX, offY + y, color);
			setPixel(offX + width, y + offY, color);
		}
		for(int x = 0; x <= width; x++) {
			setPixel(offX + x, offY, color);
			setPixel(offX + x, offY + height, color);
		}
	}
	
	public static void drawFillRect(int offX, int offY, int width, int height, int color, boolean ui) {
		int finalPW = ui ? pW : gamePW;
		int finalPH = ui ? pH : gamePH;
		
		// Render need checker
		if (offX < -width) return;
		if (offX >= finalPW) return;
		if (offY < -height) return;
		if (offY >= finalPH) return;
		//
		
		int newX = 0;
		int newY = 0;
		int newWidth = width;
		int newHeight = height;
		
		// Clipping code
		if (offX < 0) {
			newX -= offX;
		}
		if (offY < 0) {
			newY -= offY;
		}
		if (newWidth + offX >= finalPW) {
			newWidth -= newWidth + offX - finalPW;
		}
		if (newHeight + offY >= finalPH) {
			newHeight -= newHeight + offY - finalPH;
		}
		//
		
		for(int y = newY; y <= newHeight; y++) {
			for(int x = newX; x <= newWidth; x++) {
				if (ui) {
					setUIPixel(offX + x, offY + y, color);
				} else {
					setPixel(offX + x, offY + y, color);					
				}
			}
		}
	}

	public static void drawLine(int x1, int y1, int x2, int y2) {
		drawLine(x1, y1, x2, y2, null);
	}
	public static void drawLine(int x1, int y1, int x2, int y2, Color color) {
		Color previousColor = Window.getGraphics().getColor();
		
		if (color == null) {
			Window.getGraphics().setColor(Color.WHITE);
		} else {
			Window.getGraphics().setColor(color);
		}
		
		Window.getGraphics().drawLine(x1, y1, x2, y2);
		
		Window.getGraphics().setColor(previousColor);
	}
	
	public static void drawCircle(int x, int y, int radius) {
		Color previousColor = Window.getGraphics().getColor();
		Window.getGraphics().setColor(Color.WHITE);
		Window.getGraphics().drawOval(x, y, radius * 2, radius * 2);
		Window.getGraphics().setColor(previousColor);
	}
	
	public static int getpW() {
		return gamePW;
	}

	public static int getpH() {
		return gamePH;
	}
	
	public static float getScale() {
		return scale;
	}

	public static void setScale(float scale) {
		Renderer.scale = scale;
	}

	public static int getUIpW() {
		return pW;
	}

	public static int getUIpH() {
		return pH;
	}
	
	public static Font getFont() {
		return font;
	}
}

class Segment {
	public int x1;
	public int y1;
	public int x2;
	public int y2;
	public int width;
	public int color;
	
	public Segment(int x1, int y1, int x2, int y2, int width, int color) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
		this.width = width;
		this.color = color;
	}
}
















