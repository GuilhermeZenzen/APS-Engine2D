package com.aps.game.animation;

import com.aps.game.animation.Parameter;

public class IntegerParameter extends Parameter {
	private int value;
	
	public IntegerParameter(String tag, int value) {
		super(tag);
		this.value = value;
	}
	
	public IntegerParameter copy() {
		return new IntegerParameter(tag, value);
	}
	
	public int getValue() {
		return value;
	}
	public void setValue(int value) {
		this.value = value;
	}
}
