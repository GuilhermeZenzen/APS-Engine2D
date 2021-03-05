package com.aps.game.animation;

import com.aps.game.animation.Parameter;

public class TriggerParameter extends Parameter {
	private boolean value;
	
	public TriggerParameter(String tag) {
		super(tag);
	}
	
	public boolean getValue() {
		return value;
	}
	public void setValue() {
		value = true;
	}
}
