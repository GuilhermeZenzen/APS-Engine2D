package com.aps.game.components;

import com.aps.engine.GameController;
import com.aps.engine.Renderer;
import com.aps.game.GameObject;

public class Component {
	protected int executionOrder = 0;
	protected GameObject gameObject;
	protected boolean disabled = false;
	protected boolean initialized = false;
		
	public void awake() {
		
	}
	
	public void start() {
		
	}
	
	public void physicsUpdate() {
		
	}
	
	public void update() {
		
	}
	
	public void lateUpdate() {
		
	}
	
	public void render(Renderer renderer) {
		
	}
	
	public void onCollisionEnter(Collider collider) {
		
	}
	
	public void onCollision(Collider collider) {
		
	}

	public void onCollisionExit(Collider collider) {
		
	}
	
	public void onTriggerEnter(Collider collider) {
		
	}
	
	public void onTrigger(Collider collider) {
		
	}

	public void onTriggerExit(Collider collider) {
		
	}
	
	public void onEnable() {
		
	}
	
	public void onDisable() {
		
	}
	
	public void onDestroy() {
		
	}
	
	public void onRemove() {
		
	}
	
	public int getExecutionOrder() {
		return executionOrder;
	}
	
	public GameObject getGameObject() {
		return gameObject;
	}

	public void setGameObject(GameObject gameObject) {
		this.gameObject = gameObject;
	}

	public boolean isDisabled() {
		return disabled;
	}

	public void setDisabled(boolean isDisabled) {
		if (this.disabled && !isDisabled) {
			onEnable();
		} else if (!this.disabled && isDisabled) {
			onDisable();
		}
		
		this.disabled = isDisabled;
	}

	public boolean isInitialized() {
		return initialized;
	}

	public void setInitialized(boolean initialized) {
		this.initialized = initialized;
	}
}
