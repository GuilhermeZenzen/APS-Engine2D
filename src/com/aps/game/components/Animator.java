package com.aps.game.components;

import com.aps.engine.GameController;
import com.aps.game.animation.Animation;
import com.aps.game.animation.AnimationController;
import com.aps.game.animation.AnimatorController;
import com.aps.game.animation.BooleanParameter;
import com.aps.game.animation.Condition;
import com.aps.game.animation.FloatParameter;
import com.aps.game.animation.IntegerParameter;
import com.aps.game.animation.Parameter;
import com.aps.game.animation.State;
import com.aps.game.animation.Transition;

public class Animator extends Component {
	private Sprite sprite;
	private AnimatorController animatorController;
	
	private float time;
	private float sampleElapsedTime;
	private int currentFrame;
	
	private State currentState;
	
	public void awake() {
		AnimationController.addAnimator(this);
		executionOrder = -1;
	}
	
	public void start() {
		sprite = gameObject.getComponent(Sprite.class);
		
		if (animatorController != null) {
			play(animatorController.getDefaultState());
		}
	}
	
	public void play(String stateTag) {
		if (animatorController == null) return;
		
		State state = getState(stateTag);
		
		if (state != null) {
			play(state);
		}
	}
	public void play(State state) {
		if (animatorController == null) return;
		
		stop();
		currentState = state;

		if (currentState != null) {
			executeFrame(currentState.getClip());
		}
	}
	
	public void stop() {
		time = 0;
		sampleElapsedTime = 0;
		currentFrame = 0;
	}
	
	public void run() {
		if (animatorController == null) return;

		updateTransitions();
		
		if (currentState == null) return;

		float deltaTime = GameController.getDeltaTime() * currentState.getVelocity() * GameController.getTimeScale();
		time += deltaTime;
		sampleElapsedTime += deltaTime;
		Animation currentAnimation = getCurrentAnimation();
		
		if (sampleElapsedTime > Animation.SAMPLE_TIME) {
			currentFrame++;
			currentFrame %= currentAnimation.getFrames().size();
			sampleElapsedTime = 0;
			executeFrame(currentAnimation);
			
			if (currentFrame == currentAnimation.getFrames().size() - 1) {
				if (!currentAnimation.canLoop()) {
					stop();
				}
			}
		}
	}
	
	private Animation getCurrentAnimation() {
		return currentState != null ? currentState.getClip() : null;
	}

	private void executeFrame(Animation animation) {
		if (sprite != null) {
			sprite.setImage(animation.getFrames().get(currentFrame));
		}
	}

	public float normalizedTime() {
		if (animatorController == null) return 0f;
		
		Animation currentAnimation = getCurrentAnimation();
		
		if (currentAnimation != null) {
			float duration = currentAnimation.getDuration();
			return duration != 0 ? time / duration : 0;
		} else {
			return 0;
		}
	}

	private void updateTransitions() {
		if (currentState != null) {
			float normalizedTime = normalizedTime();
			for (Transition transition : currentState.getTransitions()) {
				if (!transition.isCanTransiteToSelf() && transition.getTo() == currentState) continue;
				
				boolean canTransite = transition.isHasExit() ? transition.getExitTime() >= normalizedTime : true;
				
				if (canTransite) {
					for (Condition condition : transition.getConditions()) {
						if (!condition.isCorrect()) {
							canTransite = false;
							break;
						}
					}
				}

				if (canTransite) {
					play(transition.getTo());
					updateTransitions();
					break;
				}
			}
		}
		
		for (Transition transition : animatorController.getAnyState().getTransitions()) {
			if (!transition.isCanTransiteToSelf() && transition.getTo() == currentState) continue;
					
			boolean canTransite = true;
			
			for (Condition condition : transition.getConditions()) {
				if (!condition.isCorrect()) {
					canTransite = false;
					break;
				}
			}

			if (canTransite) {
				play(transition.getTo());
				break;
			}
		}
	}
	
	public State getState(String tag) {
		if (animatorController == null) return null;
		
		return animatorController.getState(tag);
	}
	
	private BooleanParameter findBool(String tag) {
		if (animatorController == null) return null;

		return animatorController.findBool(tag);
	}
	public boolean getBool(String tag) {
		BooleanParameter parameter = findBool(tag);
		return parameter != null ? parameter.getValue() : false;
	}
	public void setBool(String tag, boolean value) {
		BooleanParameter parameter = findBool(tag);
		if (parameter != null) {
			parameter.setValue(value);
		}
	}
	
	private IntegerParameter findInt(String tag) {
		if (animatorController == null) return null;
		
		return animatorController.findInt(tag);
	}
	public int getInt(String tag) {
		if (animatorController == null) return 0;
		
		IntegerParameter parameter = findInt(tag);
		return parameter != null ? parameter.getValue() : 0;
	}
	public void setInt(String tag, int value) {
		if (animatorController == null) return;
		
		IntegerParameter parameter = findInt(tag);
		if (parameter != null) {
			parameter.setValue(value);
		}
	}
	
	private FloatParameter findFloat(String tag) {
		if (animatorController == null) return null;
		
		return animatorController.findFloat(tag);
	}
	public float getFloat(String tag) {
		if (animatorController == null) return 0f;
		
		FloatParameter parameter = findFloat(tag);
		return parameter != null ? parameter.getValue() : 0f;
	}
	public void setFloat(String tag, float value) {
		if (animatorController == null) return;
		
		FloatParameter parameter = findFloat(tag);
		if (parameter != null) {
			parameter.setValue(value);
		}
	}

	public AnimatorController getAnimatorController() {
		return animatorController;
	}

	public void setAnimatorController(AnimatorController animatorController) {
		this.animatorController = animatorController;
		play(this.animatorController.getDefaultState());
	}
}
