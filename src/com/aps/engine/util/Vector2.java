package com.aps.engine.util;

import java.util.ArrayList;

public class Vector2 {
	private float x;
	private float y;
	
	// CONSTRUCTORS
	public Vector2() {
		x = 0f;
		y = 0f;
	}
	public Vector2(float x) {
		this.x = x;
		y = 0f;
	}
	public Vector2(float x, float y) {
		this.x = x;
		this.y = y;
	}
	public Vector2(Vector2 v) {
		this.x = v.x;
		this.y = v.y;
	}
	
	public void setVector(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public float getAngle() {
		return (float)Math.atan2(y, x);
	}
	
	public float getMagnitude() {
		return (float)Math.sqrt(x * x + y * y);
	}
	
	public float getSqrMagnitude() {
		return x * x + y * y;
	}
	
	public Vector2 normalized() {
		float magnitude = getMagnitude();
		return new Vector2(x / magnitude, y / magnitude);
	}
	
	public Vector2 rotated(float angle) {
		return fromAngle(getAngle() + angle).multiply(getMagnitude());
	}
	
	public Vector2 inverse() {
		return new Vector2(-x, -y);
	}
	
	public Vector2 add(float x, float y) {
		return new Vector2(this.x + x, this.y + y);
	}
	public Vector2 add(Vector2 v) {
		return add(v.x, v.y);
	}
	
	public Vector2 subtract(float x, float y) {
		return new Vector2(this.x - x, this.y - y);
	}
	public Vector2 subtract(Vector2 v) {
		return subtract(v.x, v.y);
	}
	
	public Vector2 multiply(Vector2 v) {
		return multiply(v.getX(), v.getY());
	}
	public Vector2 multiply(float x, float y) {
		return new Vector2(this.x * x, this.y * y);
	}
	public Vector2 multiply(float times) {
		return new Vector2(x * times, y * times);
	}
	
	public Vector2 divide(Vector2 v) {
		return new Vector2(this.x / v.x, this.y / v.y);
	}
	public Vector2 divide(float scalar) {
		return new Vector2(this.x / scalar, this.y / scalar);
	}
	
	// CLASS METHODS
	public static Vector2 zero() {
		return new Vector2(0, 0);
	}
	
	public static Vector2 one() {
		return new Vector2(1, 1);
	}
	
	public static Vector2 up() {
		return new Vector2(0, -1);
	}
	
	public static Vector2 right() {
		return new Vector2(1, 0);
	}
	
	public static Vector2 down() {
		return new Vector2(0, 1);
	}
	
	public static Vector2 left() {
		return new Vector2(-1, 0);
	}
	
	public static Vector2 fill(float value) {
		return new Vector2(value, value);
	}
	
	public static Vector2 center(Vector2... vectors) {
		float xSum = 0;
		float ySum = 0;
		
		for (Vector2 v : vectors) {
			xSum += v.x;
			ySum += v.y;
		}
		
		return new Vector2(xSum / vectors.length, ySum / vectors.length);
	}
	public static Vector2 center(ArrayList<Vector2> vectors) {
		float xSum = 0;
		float ySum = 0;
		
		for (Vector2 v : vectors) {
			xSum += v.x;
			ySum += v.y;
		}
		
		return new Vector2(xSum / vectors.size(), ySum / vectors.size());
	}
	
	public static Vector2 fromAngle(float angle) {
		return new Vector2((float)Math.cos(angle), (float)Math.sin(angle));
	}
	
	public static float distance(Vector2 v1, Vector2 v2) {
		float deltaX = Math.abs(v2.getX() - v1.getX());
		float deltaY = Math.abs(v2.getY() - v1.getY());
		return (float)Math.sqrt(deltaX * deltaX + deltaY * deltaY);
	}
	
	public static float sqrDistance(Vector2 v1, Vector2 v2) {
		float deltaX = Math.abs(v2.getX() - v1.getX());
		float deltaY = Math.abs(v2.getY() - v1.getY());
		return deltaX * deltaX + deltaY * deltaY;
	}
	
	public static float dot(Vector2 v1, Vector2 v2) {
		return v1.getX() * v2.getX() + v1.getY() * v2.getY();
	}
	
	public static Vector2 normal(Vector2 v1, Vector2 v2) {
		return new Vector2(-(v2.y - v1.y), v2.x - v1.x);
	}
	
	// Getters and Setters
	public float getX() {
		return x;
	}
	
	public float getY() {
		return y;
	}
}
