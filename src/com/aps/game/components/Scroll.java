package com.aps.game.components;

import com.aps.engine.Input;

public class Scroll extends UIElement {
	private UIElement content;
	private float scrollValue;
	private float scrollVelocity;
	private int contentHeight;
	
	public void onHover() {
		scrollValue += Input.getScroll() * scrollVelocity; 
		
		super.onHover();;
	}
}
