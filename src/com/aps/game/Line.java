package com.aps.game;

import com.aps.engine.util.Mathf;
import com.aps.engine.util.Vector2;

public class Line {
	private Vector2 p1;
	private Vector2 p2;
	private boolean vertical = false;
	private float verticalX = 0f;
	private float slope = 0f;
	private float yIntercept = 0f;
	
	public float calculateY(float x) {
		return slope * x + yIntercept;
	}
	
	public Vector2 normal(boolean inverse) {
		float dx = p2.getX() - p1.getX();
		float dy = p2.getY() - p1.getY();
		Vector2 normal = !inverse ? new Vector2(-dy, dx) : new Vector2(dy, -dx); 
		return normal.normalized();
	}
	
	// CLASS METHODS
	public static Line calculateLine(Vector2 p1, Vector2 p2) {
		Line line = new Line();
		line.setP1(p1);
		line.setP2(p2);
		
		float deltaX = p2.getX() - p1.getX();
		float deltaY = p2.getY() - p1.getY();
		
		if (deltaX == 0) {
			line.setVertical(true);
			line.setVerticalX(p1.getX());
		}
		else {
			line.setSlope(deltaY / deltaX);
			line.setyIntercept(-line.getSlope() * p1.getX() + p1.getY());
		}
		
		return line;
	}
	
	public static Vector2 intersection(Line l1, Line l2) {
		Vector2 intersectionPoint = null;
		float slopeDiff = l1.getSlope() - l2.getSlope();
		
		if (l1.isVertical() || l2.isVertical()) {
			Line verticalLine = l1.isVertical() ? l1 : l2;
			Line line = l1.isVertical() ? l2 : l1;
			
			intersectionPoint = new Vector2(verticalLine.getVerticalX(), line.calculateY(verticalLine.getVerticalX()));
		} else if (slopeDiff != 0f) {
			float x = (l2.getyIntercept() - l1.getyIntercept()) / (slopeDiff);
			intersectionPoint = new Vector2(x, l1.calculateY(x));			
		}
		
		if (intersectionPoint != null) {
			boolean l1InRange = false;
			if (l1.isVertical()) {
				l1InRange = Mathf.inRange(intersectionPoint.getY(), l1.getP1().getY(), l1.getP2().getY());
			} else if (l1.getSlope() == 0f) {
				l1InRange = Mathf.inRange(intersectionPoint.getX(), l1.getP1().getX(), l1.getP2().getX());
			} else {
				l1InRange = Mathf.inRange(intersectionPoint.getX(), l1.getP1().getX(), l1.getP2().getX()) 
						 && Mathf.inRange(intersectionPoint.getY(), l1.getP1().getY(), l1.getP2().getY());
			}

			boolean l2InRange = false;
			if (l2.isVertical()) {
				l2InRange = Mathf.inRange(intersectionPoint.getY(), l2.getP1().getY(), l2.getP2().getY());
			} else if (l1.getSlope() == 0f) {
				l2InRange = Mathf.inRange(intersectionPoint.getX(), l2.getP1().getX(), l2.getP2().getX());
			} else {
				l2InRange = Mathf.inRange(intersectionPoint.getX(), l2.getP1().getX(), l2.getP2().getX()) 
						 && Mathf.inRange(intersectionPoint.getY(), l2.getP1().getY(), l2.getP2().getY());
			}
			
			if (l1InRange && l2InRange) {
				return intersectionPoint;
			}
		}
		
		return null;
	}
	
	// GETTERS AND SETTERS
	public Vector2 getP1() {
		return p1;
	}
	public void setP1(Vector2 p1) {
		this.p1 = p1;
	}
	
	public Vector2 getP2() {
		return p2;
	}
	public void setP2(Vector2 p2) {
		this.p2 = p2;
	}
	
	public boolean isVertical() {
		return vertical;
	}
	public void setVertical(boolean vertical) {
		this.vertical = vertical;
	}
	
	public float getVerticalX() {
		return verticalX;
	}
	public void setVerticalX(float verticalX) {
		this.verticalX = verticalX;
	}
	
	public float getSlope() {
		return slope;
	}
	public void setSlope(float slope) {
		this.slope = slope;
	}
	
	public float getyIntercept() {
		return yIntercept;
	}
	public void setyIntercept(float yIntercept) {
		this.yIntercept = yIntercept;
	}
}
