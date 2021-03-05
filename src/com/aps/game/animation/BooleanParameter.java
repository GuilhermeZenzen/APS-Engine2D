package com.aps.game.animation;

import com.aps.game.animation.Parameter;

public class BooleanParameter extends Parameter {
	private boolean value;
	
	public BooleanParameter(String tag, boolean value) {
		super(tag);
		this.value = value;
	}
	
	public BooleanParameter copy() {
		return new BooleanParameter(tag, value);
	}
	
	public boolean getValue() {
		return value;
	}
	public void setValue(boolean value) {
		this.value = value;
	}
}
