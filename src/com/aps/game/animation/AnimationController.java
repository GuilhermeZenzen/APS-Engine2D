package com.aps.game.animation;

import java.util.ArrayList;

import com.aps.game.components.Animator;

public class AnimationController {
	private static ArrayList<Animator> animators = new ArrayList<Animator>();
	
	public static void addAnimator(Animator animator) {
		animators.add(animator);
	}
	
	public static void update() {
		for (Animator animator : animators) {
			if (!animator.isDisabled()) {
				animator.run();
			}
		}
	}
}
