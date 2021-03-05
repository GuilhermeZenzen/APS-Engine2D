package com.aps.engine;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

import com.aps.engine.gfx.Image;

public class Window {
	private static JFrame frame;
	private static BufferedImage image;
	private static BufferedImage ui;
	private static Canvas canvas;
	private static BufferStrategy bufferStrategy;
	private static Graphics graphics;
	
	public static void setWindow() {
		image = new BufferedImage((int)(GameController.getWidth() * GameController.getScale()), (int)(GameController.getHeight() * GameController.getScale()), BufferedImage.TYPE_INT_RGB);
		ui = new BufferedImage((int)(GameController.getWidth() * GameController.getScale()), (int)(GameController.getHeight() * GameController.getScale()), BufferedImage.TYPE_INT_ARGB);
		canvas = new Canvas();
		Dimension dimension = new Dimension((int)(GameController.getWidth() * GameController.getScale()), (int)(GameController.getHeight() * GameController.getScale()));
		canvas.setPreferredSize(dimension);
		canvas.setMinimumSize(dimension);
		canvas.setMaximumSize(dimension);
		
		frame = new JFrame(GameController.getTitle());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		frame.add(canvas, BorderLayout.CENTER);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		frame.setVisible(true);
		
		canvas.createBufferStrategy(2);
		bufferStrategy = canvas.getBufferStrategy();
		graphics = bufferStrategy.getDrawGraphics();
	}
	
	public static void update() {
		graphics.drawImage(image, 0, 0, canvas.getWidth(), canvas.getHeight(), null);
		
		for (int i = 0; i < Renderer.getSegmentsToDraw().size(); i++) {
			Segment segment = Renderer.getSegmentsToDraw().get(i);
			Graphics2D g2d = (Graphics2D)graphics;
			BasicStroke previousStroke = (BasicStroke)g2d.getStroke();
			Color previousColor = g2d.getColor();
			g2d.setStroke(new BasicStroke(segment.width));
			g2d.setColor(new Color(segment.color, true));
			g2d.drawLine(segment.x1, segment.y1, segment.x2, segment.y2);
			g2d.setStroke(previousStroke);
			g2d.setColor(previousColor);
			Renderer.getSegmentsToDraw().remove(i);
			i--;
		}
		
		//graphics.drawImage(ui, 0, 0, (int)(GameController.getWidth() * GameController.getScale()), (int)(GameController.getHeight() * GameController.getScale()), null);
		bufferStrategy.show();
	}

	public static JFrame getFrame() {
		return frame;
	}

	public static BufferedImage getImage() {
		return image;
	}
	
	public static BufferedImage getUI() {
		return ui;
	}

	public static Canvas getCanvas() {
		return canvas;
	}
	
	public static Graphics getGraphics() {
		return graphics;
	}
}









