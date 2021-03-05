package com.aps.game.components;

import java.util.ArrayList;

import com.aps.engine.util.Vector2;

public class ShapeCollider extends Collider {
	private ArrayList<Vector2> points = new ArrayList<Vector2>();

	public Vector2[] getGlobalPoints() {
		Vector2[] globalPoints = new Vector2[points.size()];
		
		for (int i = 0; i < globalPoints.length; i++) {
			globalPoints[i] = gameObject.getPosition().add(points.get(i));
		}
		
		return globalPoints;
	}
	public ArrayList<Vector2> getPoints() {
		return points;
	}
	public void setPoints(ArrayList<Vector2> points) {
		this.points = points;
	}
	public void setPoints(Vector2... points) {
		this.points = new ArrayList<Vector2>();
		for (Vector2 point : points) {
			this.points.add(point);
		}
	}
	
	public void addPoint(Vector2 point) {
		points.add(point);
	}
	
	public Vector2 getCenter() {
		return Vector2.center(points);
	}
}
