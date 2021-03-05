package com.aps.game.components;

import java.util.ArrayList;

import com.aps.engine.util.Vector2;
import com.aps.game.Physics;

public abstract class Collider extends Component {
	protected boolean collided;
	
	protected ArrayList<Collider> colliders = new ArrayList<Collider>();
	
	protected boolean trigger = false;
	
	protected Vector2 globalCenter = Vector2.zero();
	protected Vector2 previousCenter = Vector2.zero();
	
	protected float overlap;
	protected Vector2 resolve;

	public void awake() {
		super.awake();
		
		executionOrder = 1;
		Physics.addCollider(this);
	}
	
	public void start() {
		updateGlobalCenter();
	}
	
	public void physicsUpdate() {
		previousCenter = globalCenter;
		updateGlobalCenter();
	}
	
	public void onRemove() {
		removeFromPhysics();
	}
	
	public void onDestroy() {
		removeFromPhysics();
	}
	
	private void removeFromPhysics() {
		Physics.removeCollider(this);
	}
	
	public void updateGlobalCenter() {
		globalCenter = getCenter().add(gameObject.getPosition());
	}
	
	public void collision(Collider collider, boolean collided) {
		if (gameObject == null) return;
		
		if (collided) {
			if (!colliders.contains(collider)) {
				if (collider.isTrigger()) {
					for (Component component : gameObject.getComponents()) {
						component.onTriggerEnter(collider);
					}
				} else {
					for (Component component : gameObject.getComponents()) {
						component.onCollisionEnter(collider);
					}
				}
				
				colliders.add(collider);
			}
			
			if (collider.isTrigger()) {
				for (Component component : gameObject.getComponents()) {
					component.onTrigger(collider);
				}
			} else {
				for (Component component : gameObject.getComponents()) {
					component.onCollision(collider);
				}
			}
		} else if (colliders.contains(collider)) {
			if (collider.isTrigger()) {
				for (Component component : gameObject.getComponents()) {
					component.onTriggerExit(collider);
				}
			} else {
				for (Component component : gameObject.getComponents()) {
					component.onCollisionExit(collider);
				}
			}
			
			colliders.remove(collider);
		}
	}
	
	public boolean isTrigger() {
		return trigger;
	}
	public void setTrigger(boolean trigger) {
		this.trigger = trigger;
	}
	
	public abstract Vector2 getCenter();
	
	public Vector2 getGlobalCenter() {
		return globalCenter;
	}
	
	public Vector2 getPreviousCenter() {
		return previousCenter;
	}

	public float getOverlap() {
		return overlap;
	}

	public void setOverlap(float overlap) {
		this.overlap = overlap;
	}

	public Vector2 getResolve() {
		return resolve;
	}

	public void setResolve(Vector2 resolve) {
		this.resolve = resolve;
	}
}
