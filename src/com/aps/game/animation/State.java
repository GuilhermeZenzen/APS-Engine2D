package com.aps.game.animation;

import java.util.ArrayList;

import com.aps.game.animation.Transition;

public class State {
	private String tag;
	private Animation clip;
	private float velocity;

	private ArrayList<Transition> transitions = new ArrayList<Transition>();
	
	public State(String tag, Animation clip, float velocity) {
		this.tag = tag;
		this.clip = clip;
		this.velocity = velocity;

	}
	
	public void addTransition(Transition transition) {
		transitions.add(transition);
	}
	public ArrayList<Transition> getTransitions() {
		return transitions;
	}
	public String getTag() {
		return tag;
	}
	public Animation getClip() {
		return clip;
	}
	public float getVelocity() {
		return velocity;
	}
	public void setVelocity(float velocity) {
		this.velocity = velocity;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	public void setClip(Animation clip) {
		this.clip = clip;
	}
	public void setTransitions(ArrayList<Transition> transitions) {
		this.transitions = transitions;
	}
}
