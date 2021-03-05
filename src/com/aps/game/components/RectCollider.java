package com.aps.game.components;

import com.aps.engine.util.Vector2;
import com.aps.game.GameObject;
import com.aps.game.Physics;

public class RectCollider extends Collider {
	private Vector2 center = Vector2.zero();
	private Vector2 size = Vector2.zero();
	private Vector2 halfSize = Vector2.zero();
	private Vector2 points[] = new Vector2[4];

	public void awake() {
		super.awake();
		updatePoints();
	}
	
	public void updatePoints() {
		points[0] = new Vector2(-halfSize.getX(), -halfSize.getY());
		points[1] = new Vector2(halfSize.getX(), -halfSize.getY());
		points[2] = new Vector2(halfSize.getX(), halfSize.getY());
		points[3] = new Vector2(-halfSize.getX(), halfSize.getY());
	}
	public Vector2[] getPoints() {
		Vector2 points[] = new Vector2[4];
		
		points[0] = globalCenter.add(this.points[0]);
		points[1] = globalCenter.add(this.points[1]);
		points[2] = globalCenter.add(this.points[2]);
		points[3] = globalCenter.add(this.points[3]);
		
		return points;
	}
	
	public void setProperties(Vector2 center, Vector2 size) {
		setCenter(center);
		setSize(size);
	}
	
	public void setBounds(Vector2 p1, Vector2 p2) {
		setProperties(Vector2.center(p1, p2).subtract(gameObject.getPosition()), new Vector2(Math.abs(p2.getX() - p1.getX()), Math.abs(p2.getY() - p1.getY())));
	}

	// GETTERS AND SETTERS
	public Vector2 getCenter() {
		return center;
	}
	public void setCenter(Vector2 center) {
		this.center = center;
	}
	
	public Vector2 getSize() {
		return size;
	}
	public void setSize(Vector2 size) {
		this.size = size;
		halfSize = new Vector2(size.getX() / 2, size.getY() / 2);
		updatePoints();
	}
	
	public Vector2 getHalfSize() {
		return halfSize;
	}
}
