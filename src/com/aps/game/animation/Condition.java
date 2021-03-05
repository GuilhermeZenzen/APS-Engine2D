package com.aps.game.animation;

import com.aps.game.animation.Parameter;

public abstract class Condition {
	protected Parameter parameter;
	
	public Condition(Parameter parameter) {
		this.parameter = parameter;
	}
	
	public abstract boolean isCorrect();
}
