package com.aps.game.animation;

import com.aps.game.animation.BooleanParameter;
import com.aps.game.animation.Condition;

public class BooleanCondition extends Condition {
	private boolean isTrue;
	
	public BooleanCondition(BooleanParameter parameter, boolean isTrue) {
		super(parameter);
		this.isTrue = isTrue;
	}
	
	public boolean isCorrect() {
		boolean parameter = ((BooleanParameter)this.parameter).getValue();
		return isTrue ? parameter : !parameter;
	}
}
