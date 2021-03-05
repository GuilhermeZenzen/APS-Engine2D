package com.aps.game.animation;

import com.aps.game.animation.Condition;
import com.aps.game.animation.IntegerParameter;

public class IntegerCondition extends Condition {
	private IntegerComparasion comparasion = IntegerComparasion.EQUAL;
	private int compareValue = 0;
	
	public IntegerCondition(IntegerParameter parameter, IntegerComparasion comparasion, int compareValue) {
		super(parameter);
		this.comparasion = comparasion;
		this.compareValue = compareValue;
	}
	
	public boolean isCorrect() {
		int parameter = ((IntegerParameter)this.parameter).getValue();
		switch (comparasion) {
		case SMALLER:
			return parameter < compareValue;
		case SMALLER_EQUAL:
			return parameter <= compareValue;
		case EQUAL:
			return parameter == compareValue;
		case BIGGER_EQUAL:
			return parameter >= compareValue;
		default:
			return parameter > compareValue;
		}
	}
}
