package com.aps.engine.gfx;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.aps.engine.util.Mathf;

public class Image {
	private int width, height;
	private int[] pixels;
	
	public Image(String path) {
		BufferedImage image = null;
		
		try {
			image = ImageIO.read(Image.class.getResourceAsStream(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		width = image.getWidth();
		height = image.getHeight();
		pixels = image.getRGB(0, 0, width, height, null, 0, width);
		
		image.flush();
	}
	
	public Image(int pixels[], int width, int height) {
		this.width = width;
		this.height = height;
		this.pixels = pixels;
	}
	
	public static int[] scaleImage(int pixels[], int width, int height, float scale) {
		return scaleImage(pixels, width, height, scale, scale);
	}
	public static int[] scaleImage(int pixels[], int width, int height, float scaleX, float scaleY) {
		return scaleImage(pixels, width, height, (int)Math.ceil(width * scaleX), (int)Math.ceil(height * scaleY));
	}
	public static int[] scaleImage(int pixels[], int w1, int h1, int w2, int h2) {
		if (w1 <= 0 || h1 <= 0 || w2 <= 0 || h2 <= 0) return null;

		int scaledPixels[] = new int[w2 * h2];
		
		int xRatio = (int)((w1 << 16) / w2) + 1;
		int yRatio = (int)((h1 << 16) / h2) + 1;
		
		int px, py;
		
		for (int y = 0; y < h2; y++) {
			for (int x = 0; x < w2; x++) {
				px = ((x * xRatio) >> 16);
				py = ((y * yRatio) >> 16);
				scaledPixels[(y * w2) + x] = pixels[(py * w1) + px];
			}
		}
		
		return scaledPixels;
	}
	
	public static int[] colorImage(int pixels[], int color) {
		int coloredPixels[] = new int[pixels.length];
		
		for (int i = 0; i < pixels.length; i++) {
			int pixel = pixels[i];
			int oldRed = (pixel >> 16) & 0xff, oldGreen = (pixel >> 8) & 0xff, oldBlue = pixel & 0xff;
			int pixelRed = (color >> 16) & 0xff, pixelGreen = (color >> 8) & 0xff, pixelBlue = color & 0xff;
			int newAlpha = (int)(((pixel >> 24) & 0xff) * (((color >> 24) & 0xff) / 255f));
			int newRed = (int)Mathf.lerp((oldRed + pixelRed) / 2, oldRed * (pixelRed / 255f), pixelRed / 255f);
			int newGreen = (int)Mathf.lerp((oldGreen + pixelGreen) / 2, oldGreen * (pixelGreen / 255f), pixelGreen / 255f);
			int newBlue = (int)Mathf.lerp((oldBlue + pixelBlue) / 2, oldBlue * (pixelBlue / 255f), pixelBlue / 255f);
			coloredPixels[i] = (newAlpha << 24) | (newRed << 16) | (newGreen << 8) | (newBlue);
		}

		return coloredPixels;
	}
	
	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int[] getPixels() {
		return pixels;
	}

	public void setPixels(int[] pixels) {
		this.pixels = pixels;
	}
}