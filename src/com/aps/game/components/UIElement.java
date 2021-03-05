package com.aps.game.components;

import java.util.ArrayList;

import com.aps.engine.gfx.Image;
import com.aps.game.UI;
import com.aps.game.UIOnClick;
import com.aps.game.UIOnEnter;
import com.aps.game.UIOnExit;
import com.aps.game.UIOnHover;

public class UIElement extends Component {
	public enum Types {
		IMAGE, TEXT, BUTTON, BOOK, INPUT_TEXT;
	}
	
	protected int width;
	protected int height;
	protected float pivotX;
	protected float pivotY;
	protected int depth;
	
	protected Image image;
	protected int color;
	
	private boolean interactable = true;
	private boolean ignorable = true;
	
	protected ArrayList<UIOnEnter> enterListeners = new ArrayList<UIOnEnter>();
	protected ArrayList<UIOnHover> hoverListeners = new ArrayList<UIOnHover>();
	protected ArrayList<UIOnExit> exitListeners = new ArrayList<UIOnExit>();
	protected ArrayList<UIOnClick> clickListeners = new ArrayList<UIOnClick>();
	
	public void awake() {

	}
	
	public void start() {
		UI.addUIElement(this);
	}
	
	public void onEnter() {
		for (UIOnEnter listener : enterListeners) {
			listener.onEnter(this);
		}
	}
	
	public void onHover() {
		for (UIOnHover listener : hoverListeners) {
			listener.onHover(this);
		}
	}
	
	public void onExit() {
		for (UIOnExit listener : exitListeners) {
			listener.onExit(this);
		}
	}
	
	public void onClick() {
		for (UIOnClick listener : clickListeners) {
			listener.onClick(this);
		}
	}
	
	public void onDestroy() {
		UI.removeUIElement(this);
	}
	
	public void lock() {
		UI.lockUIElement(this);
	}
	
	public void unlock() {
		if (UI.getLockedUIElement() == this) {
			UI.lockUIElement(null);
		}
	}
	
	public void addEnterListener(UIOnEnter listener) {
		enterListeners.add(listener);
	}
	public void removeEnterListener(UIOnEnter listener) {
		enterListeners.remove(listener);
	}
	
	public void addHoverListener(UIOnHover listener) {
		hoverListeners.add(listener);
	}
	public void removeHoverListener(UIOnHover listener) {
		hoverListeners.remove(listener);
	}
	
	public void addExitListener(UIOnExit listener) {
		exitListeners.add(listener);
	}
	public void removeExitListener(UIOnExit listener) {
		exitListeners.remove(listener);
	}
	
	public void addClickListener(UIOnClick listener) {
		clickListeners.add(listener);
	}
	public void removeClickListener(UIOnClick listener) {
		clickListeners.remove(listener);
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

	public float getPivotX() {
		return pivotX;
	}
	public void setPivotX(float pivotX) {
		this.pivotX = pivotX;
	}

	public float getPivotY() {
		return pivotY;
	}
	public void setPivotY(float pivotY) {
		this.pivotY = pivotY;
	}

	public int getDepth() {
		return depth;
	}
	public void setDepth(int depth) {
		this.depth = depth;
	}

	public Image getImage() {
		return image;
	}

	public Image getColoredImage() {
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

	public boolean isInteractable() {
		return interactable;
	}

	public void setInteractable(boolean interactable) {
		this.interactable = interactable;
	}

	public boolean isIgnorable() {
		return ignorable;
	}

	public void setIgnorable(boolean ignorable) {
		this.ignorable = ignorable;
	}
}
