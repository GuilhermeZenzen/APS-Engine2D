package com.aps.engine;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.activation.UnsupportedDataTypeException;

public class Input implements KeyListener, MouseListener, MouseMotionListener, MouseWheelListener {
	private GameController gameController;
	
	private static final int NUM_KEYS = 256;
	private static boolean[] keys = new boolean[NUM_KEYS];
	private static boolean[] keysLast = new boolean[NUM_KEYS];
	
	private static final int NUM_BUTTONS = 5;
	private static boolean[] buttons = new boolean[NUM_BUTTONS];
	private static boolean[] buttonsLast = new boolean[NUM_BUTTONS];
	
	private static String pressedKeyChar;
	
	private static int mouseX, mouseY;
	private static int scroll;

	public Input(GameController gameController) {
		this.gameController = gameController;
		mouseX = 0;
		mouseY = 0;
		scroll = 0;
		
		Window.getCanvas().addKeyListener(this);
		Window.getCanvas().addMouseListener(this);
		Window.getCanvas().addMouseMotionListener(this);
		Window.getCanvas().addMouseWheelListener(this);
	}
	
	public static void update() {
		scroll = 0;
		pressedKeyChar = "";
		
		for (int i = 0; i < NUM_KEYS; i++) {
			keysLast[i] = keys[i];
		}
		
		for (int i = 0; i < NUM_BUTTONS; i++) {
			buttonsLast[i] = buttons[i];
		}
	}
	
	public static boolean isKey(int keyCode) {
		return keys[keyCode];
	}
	
	public static boolean isKeyDown(int keyCode) {
		return keys[keyCode] && !keysLast[keyCode];
	}
	
	public static boolean isKeyUp(int keyCode) {
		return !keys[keyCode] && keysLast[keyCode];
	}
	
	public static boolean isMouseButton(int button) {
		return buttons[button];
	}
	
	public static boolean isMouseButtonDown(int button) {
		return buttons[button] && !buttonsLast[button];
	}
	
	public static boolean isMouseButtonUp(int button) {
		return !buttons[button] && buttonsLast[button];
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		scroll = e.getWheelRotation();
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		mouseX = (int)(e.getX());
		mouseY = (int)(e.getY());
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		mouseX = (int)(e.getX());
		mouseY = (int)(e.getY());
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		buttons[e.getButton()] = true;
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		buttons[e.getButton()] = false;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		keys[e.getKeyCode()] = true;
		pressedKeyChar += e.getKeyChar();
	}

	@Override
	public void keyReleased(KeyEvent e) {
		keys[e.getKeyCode()] = false;
	}

	@Override
	public void keyTyped(KeyEvent e) {
		
	}

	public static int getMouseX() {
		return mouseX;
	}

	public static int getMouseY() {
		return mouseY;
	}

	public static int getScroll() {
		return scroll;
	}
	
	public static String getPressedKeysString() {
		return pressedKeyChar;
	}
}
