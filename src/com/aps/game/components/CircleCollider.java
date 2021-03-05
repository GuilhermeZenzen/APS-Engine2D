package com.aps.game.components;

import com.aps.engine.util.Vector2;

public class CircleCollider extends Collider {
	private float radius;

	public float getRadius() {
		return radius;
	}
	public void setRadius(float radius) {
		this.radius = radius;
	}
	
	public Vector2 getCenter() {
		return Vector2.zero();
	}
}
