package com.aps.game.animation;

import com.aps.game.animation.Parameter;

public class FloatParameter extends Parameter {
	private float value;
	
	public FloatParameter(String tag, float value) {
		super(tag);
		this.value = value;
	}
	
	public FloatParameter copy() {
		return new FloatParameter(tag, value);
	}
	
	public float getValue() {
		return value;
	}
	public void setValue(float value) {
		this.value = value;
	}
}