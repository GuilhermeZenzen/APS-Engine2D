package com.aps.game.animation;

import com.aps.game.animation.Condition;
import com.aps.game.animation.FloatParameter;

public class FloatCondition extends Condition {
	private FloatComparasion comparasion = FloatComparasion.SMALLER;
	private float compareValue = 0f;
	
	public FloatCondition(FloatParameter parameter, FloatComparasion comparasion, float compareValue) {
		super(parameter);
		this.comparasion = comparasion;
		this.compareValue = compareValue;
	}
	
	public boolean isCorrect() {
		float parameter = ((FloatParameter)this.parameter).getValue();
		if (comparasion == FloatComparasion.SMALLER) {
			return parameter < compareValue;
		} else {
			return parameter > compareValue;
		}
	}
}

