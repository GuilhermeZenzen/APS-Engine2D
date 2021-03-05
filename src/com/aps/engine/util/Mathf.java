package com.aps.engine.util;

public class Mathf {
	private Mathf() {
		
	}
	
	public static float angleToRadians(float angle) {
		return angle / 57.295f;
	}
	
	public static float radiansToAngle(float radians) {
		return radians * 57.295f;
	}

	public static boolean inRange(float value, float min, float max) {
		float minValue = Math.min(min, max);
		float maxValue = Math.max(min, max);
		
		return value >= minValue && value <= maxValue;
	}
	
	public static float roundToHalf(float value) {
		return Math.round(value * 2) / 2.0f;
	}
	
	public static float roundTo(float value, float round) {
		return Math.round(value * round) / round;
	}
	
	public static float ceilTo(float value, float ceil) {
		return (float)Math.ceil(value * ceil) / ceil;
	}
	
	public static float clamp(float value, float min, float max) {
		return Math.max(Math.min(value, max), min);
	}
	
	public static float lerp(float min, float max, float time) {
		return min + (max - min) * time;
	}
}
