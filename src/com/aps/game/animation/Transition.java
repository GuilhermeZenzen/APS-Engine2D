package com.aps.game.animation;

import java.util.ArrayList;

import com.aps.game.animation.Condition;
import com.aps.game.animation.State;

public class Transition {
	private String tag;
	private State to;
	private boolean hasExit;
	private float exitTime;
	private boolean canTransiteToSelf;
	private ArrayList<Condition> conditions = new ArrayList<Condition>();
	
	public Transition(String tag, State to, boolean hasExit, float exitTime, boolean canTransiteToSelf) {
		this.tag = tag;
		this.to = to;
		this.hasExit = hasExit;
		this.exitTime = exitTime;
		this.canTransiteToSelf = false;
	}
	
	public String getTag() {
		return tag;
	}

	public State getTo() {
		return to;
	}

	public boolean isHasExit() {
		return hasExit;
	}

	public float getExitTime() {
		return exitTime;
	}
	
	public void addCondition(Condition condition) {
		conditions.add(condition);
	}
	public ArrayList<Condition> getConditions() {
		return conditions;
	}

	public boolean isCanTransiteToSelf() {
		return canTransiteToSelf;
	}
}
